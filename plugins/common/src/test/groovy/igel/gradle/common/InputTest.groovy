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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class InputTest {

    static final String FMT_SECTION_NAME = 'Section #%s'
    static final String FMT_SECTION_DESCRIPTION = 'Section #%s description.'
    static final String FMT_PROPERTY_KEY = 'key-%s'
    static final String FMT_PROPERTY_DEFAULT = 'Default %s'
    static final String FMT_PROPERTY_NAME = 'Property #%s'
    static final String FMT_PROPERTY_VALUE = 'Value #%s'
    static final String FMT_PROPERTY_DESCRIPTION = 'Property #%s description.'

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
            it.section(FMT_SECTION_NAME.format('1'), FMT_SECTION_DESCRIPTION.format('1')) {
                it.property(FMT_PROPERTY_KEY.format('1-1'), Input.Type.MULTILINE,
                        FMT_PROPERTY_NAME.format('1-1'), FMT_PROPERTY_DESCRIPTION.format('1-1'))
                it.property(FMT_PROPERTY_KEY.format('1-2'),
                        FMT_PROPERTY_NAME.format('1-2'), FMT_PROPERTY_DESCRIPTION.format('1-2'))
            }
            it.property(FMT_PROPERTY_KEY.format('2'), Input.Type.PASSWORD, FMT_PROPERTY_DEFAULT.format('2'),
                    FMT_PROPERTY_NAME.format('2'), FMT_PROPERTY_DESCRIPTION.format('2'))
            it.property(FMT_PROPERTY_KEY.format('3'), FMT_PROPERTY_DEFAULT.format('3'),
                    FMT_PROPERTY_NAME.format('3'), FMT_PROPERTY_DESCRIPTION.format('3'))
        }

        Assert.assertEquals(4, input.properties.size())

        Input.Property property_1_1 = input.properties[FMT_PROPERTY_KEY.format('1-1')]
        Assert.assertEquals(Input.Type.MULTILINE, property_1_1.type)
        Assert.assertNull(property_1_1.defaultValue)
        Assert.assertEquals(FMT_PROPERTY_NAME.format('1-1'), property_1_1.name)
        Assert.assertEquals(FMT_PROPERTY_DESCRIPTION.format('1-1'), property_1_1.description)

        Input.Property property_1_2 = input.properties[FMT_PROPERTY_KEY.format('1-2')]
        Assert.assertEquals(Input.Type.SINGLE_LINE, property_1_2.type)
        Assert.assertNull(property_1_2.defaultValue)
        Assert.assertEquals(FMT_PROPERTY_NAME.format('1-2'), property_1_2.name)
        Assert.assertEquals(FMT_PROPERTY_DESCRIPTION.format('1-2'), property_1_2.description)

        Input.Property property_2 = input.properties[FMT_PROPERTY_KEY.format('2')]
        Assert.assertEquals(Input.Type.PASSWORD, property_2.type)
        Assert.assertEquals(FMT_PROPERTY_DEFAULT.format('2'), property_2.defaultValue)
        Assert.assertEquals(FMT_PROPERTY_NAME.format('2'), property_2.name)
        Assert.assertEquals(FMT_PROPERTY_DESCRIPTION.format('2'), property_2.description)

        Input.Property property_3 = input.properties[FMT_PROPERTY_KEY.format('3')]
        Assert.assertEquals(Input.Type.SINGLE_LINE, property_3.type)
        Assert.assertEquals(FMT_PROPERTY_DEFAULT.format('3'), property_3.defaultValue)
        Assert.assertEquals(FMT_PROPERTY_NAME.format('3'), property_3.name)
        Assert.assertEquals(FMT_PROPERTY_DESCRIPTION.format('3'), property_3.description)

        Assert.assertNull(input.rootSection.name)
        Assert.assertNull(input.rootSection.description)
        Assert.assertEquals([
                FMT_SECTION_NAME.format('1'),
        ], input.rootSection.sections*.name)
        Assert.assertEquals([
                FMT_PROPERTY_KEY.format('2'),
                FMT_PROPERTY_KEY.format('3'),
        ], input.rootSection.properties*.key)

        Input.Section section_1 = input.rootSection.sections[0]
        Assert.assertEquals(FMT_SECTION_NAME.format('1'), section_1.name)
        Assert.assertEquals(FMT_SECTION_DESCRIPTION.format('1'), section_1.description)
        Assert.assertEquals([
        ], section_1.sections*.name)
        Assert.assertEquals([
                FMT_PROPERTY_KEY.format('1-1'),
                FMT_PROPERTY_KEY.format('1-2'),
        ], section_1.properties*.key)
    }

    @Test
    void load1() {
        Input input = new Input()
        input.rootSection {
            it.section(FMT_SECTION_NAME.format('1'), FMT_SECTION_DESCRIPTION.format('1')) {
                it.property(FMT_PROPERTY_KEY.format('1'),
                        FMT_PROPERTY_NAME.format('1'), FMT_PROPERTY_DESCRIPTION.format('1'))
            }
            it.property(FMT_PROPERTY_KEY.format('2'),
                    FMT_PROPERTY_NAME.format('2'), FMT_PROPERTY_DESCRIPTION.format('2'))
            it.property(FMT_PROPERTY_KEY.format('3'),
                    FMT_PROPERTY_NAME.format('3'), FMT_PROPERTY_DESCRIPTION.format('3'))
        }

        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('1')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('2')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('3')].value)

        Map<String, ?> map = [:]
        map[FMT_PROPERTY_KEY.format('0')] = FMT_PROPERTY_VALUE.format('0')
        map[FMT_PROPERTY_KEY.format('1')] = FMT_PROPERTY_VALUE.format('1')
        map[FMT_PROPERTY_KEY.format('2')] = 2
        input.load(map)

        Assert.assertEquals(FMT_PROPERTY_VALUE.format('1'), input.properties[FMT_PROPERTY_KEY.format('1')].value)
        Assert.assertEquals('2', input.properties[FMT_PROPERTY_KEY.format('2')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('3')].value)
    }

    @Test
    void load2() {
        Input input = new Input()
        input.rootSection {
            it.section(FMT_SECTION_NAME.format('1'), FMT_SECTION_DESCRIPTION.format('1')) {
                it.property(FMT_PROPERTY_KEY.format('1'),
                        FMT_PROPERTY_NAME.format('1'), FMT_PROPERTY_DESCRIPTION.format('1'))
            }
            it.property(FMT_PROPERTY_KEY.format('2'),
                    FMT_PROPERTY_NAME.format('2'), FMT_PROPERTY_DESCRIPTION.format('2'))
            it.property(FMT_PROPERTY_KEY.format('3'),
                    FMT_PROPERTY_NAME.format('3'), FMT_PROPERTY_DESCRIPTION.format('3'))
        }

        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('1')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('2')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('3')].value)

        Project project = ProjectBuilder.builder().build();
        project.ext[FMT_PROPERTY_KEY.format('0')] = FMT_PROPERTY_VALUE.format('0')
        project.ext[FMT_PROPERTY_KEY.format('1')] = FMT_PROPERTY_VALUE.format('1')
        project.ext[FMT_PROPERTY_KEY.format('2')] = 2
        input.load(project)

        Assert.assertEquals(FMT_PROPERTY_VALUE.format('1'), input.properties[FMT_PROPERTY_KEY.format('1')].value)
        Assert.assertEquals('2', input.properties[FMT_PROPERTY_KEY.format('2')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('3')].value)
    }

    @Test
    void load3() {
        Input input = new Input()
        input.rootSection {
            it.section(FMT_SECTION_NAME.format('1'), FMT_SECTION_DESCRIPTION.format('1')) {
                it.property(FMT_PROPERTY_KEY.format('1'),
                        FMT_PROPERTY_NAME.format('1'), FMT_PROPERTY_DESCRIPTION.format('1'))
            }
            it.property(FMT_PROPERTY_KEY.format('2'),
                    FMT_PROPERTY_NAME.format('2'), FMT_PROPERTY_DESCRIPTION.format('2'))
        }

        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('1')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('2')].value)

        Properties properties = new Properties()
        properties.setProperty(FMT_PROPERTY_KEY.format('0'), FMT_PROPERTY_VALUE.format('0'))
        properties.setProperty(FMT_PROPERTY_KEY.format('1'), FMT_PROPERTY_VALUE.format('1'))
        input.load(properties)

        Assert.assertEquals(FMT_PROPERTY_VALUE.format('1'), input.properties[FMT_PROPERTY_KEY.format('1')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('2')].value)
    }

    @Test
    void load4() {
        Input input = new Input()
        input.rootSection {
            it.section(FMT_SECTION_NAME.format('1'), FMT_SECTION_DESCRIPTION.format('1')) {
                it.property(FMT_PROPERTY_KEY.format('1'),
                        FMT_PROPERTY_NAME.format('1'), FMT_PROPERTY_DESCRIPTION.format('1'))
            }
            it.property(FMT_PROPERTY_KEY.format('2'),
                    FMT_PROPERTY_NAME.format('2'), FMT_PROPERTY_DESCRIPTION.format('2'))
        }

        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('1')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('2')].value)

        Properties properties = new Properties()
        properties.setProperty(FMT_PROPERTY_KEY.format('0'), FMT_PROPERTY_VALUE.format('0'))
        properties.setProperty(FMT_PROPERTY_KEY.format('1'), FMT_PROPERTY_VALUE.format('1'))
        File propertiesFile = File.createTempFile('input-load-test-', '.properties')
        propertiesFile.withOutputStream { properties.save(it, '') }
        input.load(propertiesFile)

        Assert.assertEquals(FMT_PROPERTY_VALUE.format('1'), input.properties[FMT_PROPERTY_KEY.format('1')].value)
        Assert.assertNull(input.properties[FMT_PROPERTY_KEY.format('2')].value)
    }

}
