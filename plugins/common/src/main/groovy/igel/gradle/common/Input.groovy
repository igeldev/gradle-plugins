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
import org.gradle.api.Project

class Input {

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

        SectionBuilder section(String name,
                               Action<SectionBuilder> action)

        SectionBuilder section(String name, String description,
                               Action<SectionBuilder> action)

        SectionBuilder property(String key,
                                String name, String description)

        SectionBuilder property(String key, String defaultValue,
                                String name, String description)

        SectionBuilder property(String key, Type type,
                                String name, String description)

        SectionBuilder property(String key, Type type, String defaultValue,
                                String name, String description)

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
        SectionBuilder section(String name, String description = null,
                               Action<SectionBuilder> action) {
            SectionBuilder builder = new SectionBuilderImpl(name, description)
            action.execute(builder)
            sections << builder.build()
            return this
        }

        @Override
        SectionBuilder property(String key, Type type = Type.SINGLE_LINE,
                                String name, String description) {
            return property(key, type, null, name, description)
        }

        @Override
        SectionBuilder property(String key, Type type = Type.SINGLE_LINE, String defaultValue,
                                String name, String description) {
            properties << new Property(key, type, defaultValue, name, description)
            return this
        }

    }

    String title
    private Map<String, Property> properties = Collections.emptyMap()
    private Section rootSection = new Section(null, null, [], [])

    Input() {
        this('')
    }

    Input(String title) {
        this.title = title
    }

    Map<String, Property> getProperties() {
        return properties
    }

    Map<String, String> getValues() {
        return properties.collectEntries {
            String key, Property property ->
                [key, property.value ?: property.defaultValue]
        } as Map<String, String>
    }

    Section getRootSection() {
        return rootSection
    }

    void rootSection(Action<SectionBuilder> action) {
        SectionBuilder builder = new SectionBuilderImpl(null, null)
        action.execute(builder)
        this.rootSection = builder.build()

        Map<String, Property> map = [:]
        Closure propertiesClosure
        propertiesClosure = { List<Section> sections ->
            if (!sections.empty) {
                sections.each {
                    it.properties.each {
                        if (map.containsKey(it.key)) {
                            throw new IllegalArgumentException("Property '$it.key' is already registered")
                        }
                        map.put(it.key, it)
                    }
                }

                propertiesClosure.trampoline(sections.collect { it.sections }.sum())
            }
        }.trampoline()
        propertiesClosure.call([rootSection])
        this.properties = Collections.unmodifiableMap(map)
    }

    void assertMissing() throws GradleException {
        List<String> missingKeys = properties.values()
                .findAll { it.value == null && it.defaultValue == null }
                .collect { it.key }
        if (missingKeys.size() == 1) {
            throw new GradleException("Property '${missingKeys[0]}' is missing")
        } else if (missingKeys.size() > 1) {
            throw new GradleException("Property '${missingKeys[0]}' and ${missingKeys.size() - 1} more are missing")
        }
    }

    void load(File propertiesFile) {
        propertiesFile.withReader {
            Properties properties = new Properties()
            properties.load(it)
            load(properties)
        }
    }

    void load(Properties properties) {
        Map<String, String> map = [:]
        properties.stringPropertyNames().each { map[it] = properties.getProperty(it) }
        load(map)
    }

    void load(Project project) {
        load(project.properties)
    }

    void load(Map<String, ?> properties) {
        properties.each { Map.Entry<String, ?> entry ->
            this.@properties[entry.key]?.value = String.valueOf(entry.value)
        }
    }

    private InputDialog dialog = null

    void loadFromUI() throws GradleException {
        if (dialog != null && dialog.visible) {
            // UI is already visible
        } else {
            dialog = new InputDialog(this)
            dialog.showUI()

            try {
                dialog.joinUI()
                if (dialog.interrupted) {
                    throw new GradleException('Interrupted by user.')
                }
            } catch (InterruptedException ignored) {
                dialog.hideUI()
            } finally {
                dialog = null
            }
        }
    }

}
