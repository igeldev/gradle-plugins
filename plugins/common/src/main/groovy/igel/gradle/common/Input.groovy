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

        private Section owner

        Section getOwner() {
            return owner
        }

        final String key
        final Type type
        final String defaultValue
        final String name
        final String description
        String value

        Property(String key, Type type, String defaultValue, String name, String description) {
            this.key = key
            this.type = type
            this.name = name
            this.description = description
            this.defaultValue = defaultValue
        }

        @Override
        String toString() {
            "Property{key='$key', type=$type, name='$name'}"
        }

    }

    static class Section {

        private Section owner

        Section getOwner() {
            return owner
        }

        final String name
        final String description
        final List<Section> sections
        final List<Property> properties

        Section(String name, String description, List<Section> sections, List<Property> properties) {
            this.name = name
            this.description = description
            this.sections = Collections.unmodifiableList(new ArrayList<>(sections))
            this.properties = Collections.unmodifiableList(new ArrayList<>(properties))

            sections.each { it.owner = this }
            properties.each { it.owner = this }
        }

        @Override
        String toString() {
            "Section{name='$name', sections=$sections, properties=$properties}"
        }

    }

    static interface SectionBuilder {

        SectionBuilder section(String name, Action<SectionBuilder> action)

        SectionBuilder section(String name, String description, Action<SectionBuilder> action)

        SectionBuilder property(String key, String name, String description)

        SectionBuilder property(String key, String defaultValue, String name, String description)

        SectionBuilder property(String key, Type type, String name, String description)

        SectionBuilder property(String key, Type type, String defaultValue, String name, String description)

    }

    private static class SectionBuilderImpl implements SectionBuilder {

        private final String name
        private final String description
        private final List<Section> sections = []
        private final List<Property> properties = []

        SectionBuilderImpl(String name, String description) {
            this.name = name
            this.description = description
        }

        Section build() {
            return new Section(name, description, sections, properties)
        }

        @Override
        SectionBuilder section(String name, Action<SectionBuilder> action) {
            return section(name, null, action)
        }

        @Override
        SectionBuilder section(String name, String description, Action<SectionBuilder> action) {
            SectionBuilder builder = new SectionBuilderImpl(name, description)
            action.execute(builder)
            sections << builder.build()
            return this
        }

        @Override
        SectionBuilder property(String key, String name, String description) {
            return property(key, Type.SINGLE_LINE, null, name, description)
        }

        @Override
        SectionBuilder property(String key, String defaultValue, String name, String description) {
            return property(key, Type.SINGLE_LINE, defaultValue, name, description)
        }

        @Override
        SectionBuilder property(String key, Type type, String name, String description) {
            return property(key, type, null, name, description)
        }

        @Override
        SectionBuilder property(String key, Type type, String defaultValue, String name, String description) {
            properties << new Property(key, type, defaultValue, name, description)
            return this
        }

    }

    private Map<String, Property> properties = Collections.emptyMap()
    private Section rootSection

    @Override
    Iterator<Property> iterator() {
        return properties.values().iterator()
    }

    Property getAt(String key) {
        return properties.get(key)
    }

    Section getRootSection() {
        return rootSection
    }

    void rootSection(Action<SectionBuilder> action) {
        SectionBuilder builder = new SectionBuilderImpl(null, null)
        action.execute(builder)
        this.rootSection = builder.build()

        // todo set properties map
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
