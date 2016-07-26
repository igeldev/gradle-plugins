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

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class InputDialogTest {

    static String getInputTitle() { 'Input title' }

    static String getSectionName(String id) { "Section #$id" }

    static String getSectionDescription(String id) { "Section #$id description" }

    static String getPropertyKey(String id) { "key-#$id" }

    static String getPropertyDefault(String id) { "Default #$id" }

    static String getPropertyName(String id) { "Property #$id" }

    static String getPropertyValue(String id) { "Value #$id" }

    static String getPropertyDescription(String id) { "Property #$id description" }

    Input input
    InputDialog inputDialog

    @Before
    void initUI() {
        input = new Input(getInputTitle())
        input.rootSection {
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
        input.properties[getPropertyKey('4')].value = getPropertyValue('4')
        inputDialog = new InputDialog(input)
    }

    @After
    void hideUI() {
        inputDialog.hideUI()
    }

    @Test
    void uiVisible() {
        Assert.assertFalse(inputDialog.visible)
        inputDialog.showUI()
        Assert.assertTrue(inputDialog.visible)
        inputDialog.hideUI()
    }

    @Test
    void uiJoin() {
        List<String> messages = []

        Thread.start {
            Thread.sleep(1000)
            messages << 'hideUI'
            inputDialog.hideUI()
        }

        messages << 'showUI'
        inputDialog.showUI()

        messages << 'joinUI'
        inputDialog.joinUI()

        messages << 'done'

        Assert.assertEquals([
                'showUI', 'joinUI', 'hideUI', 'done'
        ], messages)
    }

}
