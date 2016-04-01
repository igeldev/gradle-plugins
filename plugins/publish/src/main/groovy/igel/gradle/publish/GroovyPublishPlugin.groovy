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

package igel.gradle.publish

import groovy.xml.MarkupBuilder
import org.gradle.api.*
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar

class GroovyPublishPlugin implements Plugin<Project> {

    static class Extension {

        private final GroovyPublishPlugin plugin

        Extension(GroovyPublishPlugin plugin) {
            this.plugin = plugin
        }

        void pom(Closure pom) {
            plugin.configurePom(pom)
        }

    }

    private Project project
    private MavenPublication publication

    private boolean configurePomDone

    private void configurePom(Closure pomClosure) {
        // ensure user doesn't try to configure pom twice
        if (configurePomDone) {
            throw new GradleException('pom is already configured')
        }
        configurePomDone = true

        Action<XmlProvider> action = { XmlProvider xmlProvider ->
            // create our pom.xml using markup builder
            StringWriter xmlWriter = new StringWriter()
            MarkupBuilder xmlBuilder = new MarkupBuilder(xmlWriter)
            xmlBuilder.pom pomClosure

            // merge our pom.xml with the default one
            Node pomNode = new XmlParser().parseText(xmlWriter.toString())
            pomNode.children().each { Node child -> xmlProvider.asNode().append(child) }
        }

        publication.pom.withXml(action)
    }

    private boolean configureBintrayDone

    private void configureBintray(Closure bintrayClosure) {
        // ensure user doesn't try to configure pom twice
        if (configurePomDone) {
            throw new GradleException('bintray is already configured')
        }
        configurePomDone = true

        // delegate closure to bintray extension
        bintrayClosure.delegate = project.extensions['bintray']
        bintrayClosure.call()
    }


    @Override
    void apply(Project target) {
        this.project = target

        // check that 'groovy' plugin is applied
        // because we need 'java' component and 'main' source set
        if (!target.plugins.hasPlugin('groovy')) {
            throw new GradleException('plugin \'groovy\' should be applied')
        }

        // create a task to prepare source code jar
        Task sourcesJarTask = target.task('sourcesJar', type: Jar) {
            group = 'documentation'
            description = 'Assembles a jar archive containing the main source code.'
            classifier = 'sources'
            from target.sourceSets.main.allSource
        }

        // apply 'maven-publish' plugin ...
        target.apply plugin: 'maven-publish'

        // ... and create new 'maven' publication
        PublicationContainer publications = target.publishing.publications
        publication = publications.create('maven', MavenPublication) {
            from target.components.java
            artifact sourcesJarTask
        }

        // apply bintray plugin
        target.apply plugin: 'com.jfrog.bintray'

        // set default values for bintray
        target.extensions['bintray'].publications = ['maven']
        target.extensions['bintray'].pkg.name = project.name
        target.extensions['bintray'].pkg.desc = project.description
        target.extensions['bintray'].pkg.version.name = project.version

        // create our extension to get user's preferences
        target.extensions.create('publishGroovy', Extension, this)
    }

}
