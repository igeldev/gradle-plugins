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

import org.gradle.api.Action
import org.gradle.api.GradleException

class Input implements Iterable<Property> {

    static enum Type {
        SINGLE_LINE,
        PASSWORD,
        MULTILINE,
    }

    static class Property {

        Section owner
        String key
        Type type
        String defaultValue
        String name
        String description
        String value

    }

    static class Section {

        Section owner
        String name
        String description
        List<Section> sections
        List<Property> properties

    }

    static interface SectionBuilder {

        SectionBuilder section(String name, String description, Action<SectionBuilder> action)

        SectionBuilder property(String key, Type type, String defaultValue, String name, String description)

    }

    @Override
    Iterator<Property> iterator() {
        return null
    }

    Property getAt(String key) {
        return null
    }

    Section getRootSection() {
        return null
    }

    void rootSection(Action<SectionBuilder> action) {
    }

    void assertMissing() throws GradleException {
    }

    void load(Properties properties) {
    }

    void load(Map<String, String> properties) {
    }

    boolean isVisible() {
        return false
    }

    void showUI() {
    }

    void hideUI() {
    }

    void joinUI() throws InterruptedException {
    }

}
