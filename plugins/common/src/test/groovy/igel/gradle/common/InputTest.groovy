/*
 * Copyright 2016 Pavel Stepanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package igel.gradle.common

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class InputTest {

    static String getInputTitle() { 'Input title' }

    static String getSectionName(String id) { "Section #$id" }

    static String getSectionDescription(String id) { "Section #$id description" }

    static String getPropertyKey(String id) { "key-#$id" }

    static String getPropertyDefault(String id) { "Default #$id" }

    static String getPropertyName(String id) { "Property #$id" }

    static String getPropertyValue(String id) { "Value #$id" }

    static String getPropertyDescription(String id) { "Property #$id description" }

    @Test
    void contentNothing1() {
        Input input = new Input()

        Assert.assertEquals(0, input.properties.size())
        Assert.assertNull(input.rootSection.owner)
        Assert.assertNull(input.rootSection.name)
        Assert.assertNull(input.rootSection.description)
        Assert.assertEquals(0, input.rootSection.sections.size())
        Assert.assertEquals(0, input.rootSection.properties.size())
    }

    @Test
    void contentNothing2() {
        Input input = new Input()
        input.rootSection {}

        Assert.assertEquals(0, input.properties.size())
        Assert.assertNull(input.rootSection.owner)
        Assert.assertNull(input.rootSection.name)
        Assert.assertNull(input.rootSection.description)
        Assert.assertEquals(0, input.rootSection.sections.size())
        Assert.assertEquals(0, input.rootSection.properties.size())
    }

    @Test
    void contentSimple() {
        Input input = new Input()
        input.rootSection {
            it.section(getSectionName('1'), getSectionDescription('1')) {
                it.property(getPropertyKey('1-1'), Input.Type.MULTILINE,
                        getPropertyName('1-1'), getPropertyDescription('1-1'))
                it.property(getPropertyKey('1-2'),
                        getPropertyName('1-2'), getPropertyDescription('1-2'))
                it.section(getSectionName('1-3')) {
                }
            }
            it.property(getPropertyKey('2'), Input.Type.PASSWORD, getPropertyDefault('2'),
                    getPropertyName('2'), getPropertyDescription('2'))
            it.property(getPropertyKey('3'), getPropertyDefault('3'),
                    getPropertyName('3'), getPropertyDescription('3'))
        }

        Assert.assertNull(input.rootSection.owner)
        Assert.assertNull(input.rootSection.name)
        Assert.assertNull(input.rootSection.description)
        Assert.assertEquals([
                getSectionName('1'),
        ], input.rootSection.sections*.name)
        Assert.assertEquals([
                getPropertyKey('2'),
                getPropertyKey('3'),
        ], input.rootSection.properties*.key)

        Input.Section section_1 = input.rootSection.sections[0]
        Assert.assertEquals(input.rootSection, section_1.owner)
        Assert.assertEquals(getSectionName('1'), section_1.name)
        Assert.assertEquals(getSectionDescription('1'), section_1.description)
        Assert.assertEquals([
                getSectionName('1-3')
        ], section_1.sections*.name)
        Assert.assertEquals([
                getPropertyKey('1-1'),
                getPropertyKey('1-2'),
        ], section_1.properties*.key)

        Input.Section section_1_3 = section_1.sections[0]
        Assert.assertEquals(section_1, section_1_3.owner)
        Assert.assertEquals(getSectionName('1-3'), section_1_3.name)
        Assert.assertNull(section_1_3.description)
        Assert.assertEquals([
        ], section_1_3.sections*.name)
        Assert.assertEquals([
        ], section_1_3.properties*.key)

        Assert.assertEquals(4, input.properties.size())

        Input.Property property_1_1 = input.properties[getPropertyKey('1-1')]
        Assert.assertEquals(section_1, property_1_1.owner)
        Assert.assertEquals(Input.Type.MULTILINE, property_1_1.type)
        Assert.assertNull(property_1_1.defaultValue)
        Assert.assertEquals(getPropertyName('1-1'), property_1_1.name)
        Assert.assertEquals(getPropertyDescription('1-1'), property_1_1.description)

        Input.Property property_1_2 = input.properties[getPropertyKey('1-2')]
        Assert.assertEquals(section_1, property_1_2.owner)
        Assert.assertEquals(Input.Type.SINGLE_LINE, property_1_2.type)
        Assert.assertNull(property_1_2.defaultValue)
        Assert.assertEquals(getPropertyName('1-2'), property_1_2.name)
        Assert.assertEquals(getPropertyDescription('1-2'), property_1_2.description)

        Input.Property property_2 = input.properties[getPropertyKey('2')]
        Assert.assertEquals(input.rootSection, property_2.owner)
        Assert.assertEquals(Input.Type.PASSWORD, property_2.type)
        Assert.assertEquals(getPropertyDefault('2'), property_2.defaultValue)
        Assert.assertEquals(getPropertyName('2'), property_2.name)
        Assert.assertEquals(getPropertyDescription('2'), property_2.description)

        Input.Property property_3 = input.properties[getPropertyKey('3')]
        Assert.assertEquals(input.rootSection, property_3.owner)
        Assert.assertEquals(Input.Type.SINGLE_LINE, property_3.type)
        Assert.assertEquals(getPropertyDefault('3'), property_3.defaultValue)
        Assert.assertEquals(getPropertyName('3'), property_3.name)
        Assert.assertEquals(getPropertyDescription('3'), property_3.description)
    }

    Input inputLoad

    @Before
    void initLoad() {
        inputLoad = new Input()
        inputLoad.rootSection {
            it.section(getSectionName('1'), getSectionDescription('1')) {
                it.property(getPropertyKey('1'),
                        getPropertyName('1'), getPropertyDescription('1'))
            }
            it.property(getPropertyKey('2'),
                    getPropertyName('2'), getPropertyDescription('2'))
            it.property(getPropertyKey('3'),
                    getPropertyName('3'), getPropertyDescription('3'))
        }

        Assert.assertNull(inputLoad.properties[getPropertyKey('1')].value)
        Assert.assertNull(inputLoad.properties[getPropertyKey('2')].value)
        Assert.assertNull(inputLoad.properties[getPropertyKey('3')].value)
    }

    @Test
    void load1() {
        Map<String, ?> map = [:]
        map[getPropertyKey('0')] = getPropertyValue('0')
        map[getPropertyKey('1')] = getPropertyValue('1')
        map[getPropertyKey('2')] = 2
        inputLoad.load(map)

        Assert.assertEquals(getPropertyValue('1'), inputLoad.properties[getPropertyKey('1')].value)
        Assert.assertEquals('2', inputLoad.properties[getPropertyKey('2')].value)
        Assert.assertNull(inputLoad.properties[getPropertyKey('3')].value)
    }

    @Test
    void load2() {
        Project project = ProjectBuilder.builder().build()
        project.ext[getPropertyKey('0')] = getPropertyValue('0')
        project.ext[getPropertyKey('1')] = getPropertyValue('1')
        project.ext[getPropertyKey('2')] = 2
        inputLoad.load(project)

        Assert.assertEquals(getPropertyValue('1'), inputLoad.properties[getPropertyKey('1')].value)
        Assert.assertEquals('2', inputLoad.properties[getPropertyKey('2')].value)
        Assert.assertNull(inputLoad.properties[getPropertyKey('3')].value)
    }

    @Test
    void load3() {
        Properties properties = new Properties()
        properties.setProperty(getPropertyKey('0'), getPropertyValue('0'))
        properties.setProperty(getPropertyKey('1'), getPropertyValue('1'))
        inputLoad.load(properties)

        Assert.assertEquals(getPropertyValue('1'), inputLoad.properties[getPropertyKey('1')].value)
        Assert.assertNull(inputLoad.properties[getPropertyKey('2')].value)
    }

    @Test
    void load4() {
        Properties properties = new Properties()
        properties.setProperty(getPropertyKey('0'), getPropertyValue('0'))
        properties.setProperty(getPropertyKey('1'), getPropertyValue('1'))
        File propertiesFile = File.createTempFile('input-load-test-', '.properties')
        propertiesFile.withOutputStream { properties.store(it, '') }
        inputLoad.load(propertiesFile)

        Assert.assertEquals(getPropertyValue('1'), inputLoad.properties[getPropertyKey('1')].value)
        Assert.assertNull(inputLoad.properties[getPropertyKey('2')].value)
    }

    Input inputMissing

    @Before
    void initMissing() {
        inputMissing = new Input()
        inputMissing.rootSection {
            it.section(getSectionName('1'), getSectionDescription('1')) {
                it.property(getPropertyKey('1'), getPropertyDefault('1'),
                        getPropertyName('1'), getPropertyDescription('1'))
            }
            it.property(getPropertyKey('2'),
                    getPropertyName('2'), getPropertyDescription('2'))
            it.property(getPropertyKey('3'),
                    getPropertyName('3'), getPropertyDescription('3'))
        }
    }

    @Test
    void assertMissing1() {
        try {
            inputMissing.assertMissing()
            Assert.fail('Test should fail')
        } catch (GradleException e) {
            Assert.assertEquals(
                    "Property '${getPropertyKey('2')}' and 1 more are missing" as String,
                    e.message)
        }
    }

    @Test
    void assertMissing2() {
        inputMissing.properties[getPropertyKey('2')].value = getPropertyValue('2')

        try {
            inputMissing.assertMissing()
            Assert.fail('Test should fail')
        } catch (GradleException e) {
            Assert.assertEquals(
                    "Property '${getPropertyKey('3')}' is missing" as String,
                    e.message)
        }
    }

    @Test
    void assertMissing3() {
        inputMissing.properties[getPropertyKey('2')].value = getPropertyValue('2')
        inputMissing.properties[getPropertyKey('3')].value = getPropertyValue('3')

        inputMissing.assertMissing()
    }

    Input inputUI

    @Before
    void initUI() {
        inputUI = new Input(getInputTitle())
        inputUI.rootSection {
            it.section(getSectionName('1'), getSectionDescription('1')) {
                it.property(getPropertyKey('1.1'), Input.Type.MULTILINE,
                        getPropertyName('1.1'), getPropertyDescription('1.1'))
                it.property(getPropertyKey('1.2'), Input.Type.PASSWORD, getPropertyDefault('1.2'),
                        getPropertyName('1.2'), getPropertyDescription('1.2'))
            }

            it.section(getSectionName('2'), getSectionDescription('2')) {
                it.property(getPropertyKey('2.1'), getPropertyDefault('2.1'),
                        getPropertyName('2.1'), getPropertyDescription('2.1'))
            }

            it.section(getSectionName('3'), getSectionDescription('3')) {
                it.property(getPropertyKey('3.1'),
                        getPropertyName('3.1'), getPropertyDescription('3.1'))
                it.property(getPropertyKey('3.2'),
                        getPropertyName('3.2'), getPropertyDescription('3.2'))

                it.section(getSectionName('3.3'), getSectionDescription('3.3')) {
                    it.property(getPropertyKey('3.3.1'),
                            getPropertyName('3.3.1'), getPropertyDescription('3.3.1'))
                    it.property(getPropertyKey('3.3.2'),
                            getPropertyName('3.3.2'), getPropertyDescription('3.3.2'))
                }
            }
            it.property(getPropertyKey('4'),
                    getPropertyName('4'), getPropertyDescription('4'))
            it.property(getPropertyKey('5'),
                    getPropertyName('5'), getPropertyDescription('5'))
        }
    }

    @After
    void hideUI() {
        inputUI.hideUI()
    }

    @Test
    void uiVisible() {
        Assert.assertFalse(inputUI.visible)
        inputUI.showUI()
        Assert.assertTrue(inputUI.visible)
        inputUI.hideUI()
    }

    @Test
    void uiJoin() {
        List<String> messages = []

        Thread.start {
            Thread.sleep(1000)
            messages << 'hideUI'
            inputUI.hideUI()
        }

        messages << 'showUI'
        inputUI.showUI()

        messages << 'joinUI'
        inputUI.joinUI()

        messages << 'done'

        Assert.assertEquals([
                'showUI', 'joinUI', 'hideUI', 'done'
        ], messages)
    }

}
