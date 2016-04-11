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

abstract class BasePublishPlugin implements Plugin<Project> {

    private static void checkPluginConflicts(Project project) {
        // check there are no other publish plugins
        Plugin brother = project.plugins.find {
            BasePublishPlugin.class.isAssignableFrom(it.class)
        }
        if (brother != null) {
            throw new GradleException('Only one publish plugin can be applied to a project')
        }
    }

    static class Extension {

        private final GroovyPublishPlugin plugin

        Extension(GroovyPublishPlugin plugin) {
            this.plugin = plugin
        }

        void pom(Closure pom) {
            plugin.configurePom(pom)
        }

        void bintray(Closure bintray) {
            plugin.configureBintray(bintray)
        }

    }

    private Project project
    private MavenPublication publication

    private boolean configurePomDone

    protected void configurePom(Closure pomClosure) {
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

    protected void configureBintray(Closure bintrayClosure) {
        // ensure user doesn't try to configure pom twice
        if (configureBintrayDone) {
            throw new GradleException('bintray is already configured')
        }
        configureBintrayDone = true

        // apply bintray plugin
        project.apply plugin: 'com.jfrog.bintray'

        // set publication for bintray
        project.extensions['bintray'].publications = [publication.name]

        // delegate closure to bintray extension
        bintrayClosure.delegate = project.extensions['bintray']
        bintrayClosure.call()
    }

    /**
     * Returns configuration of MavenPublication. Called only once.
     *
     * @param target the target project.
     * @return configuration closure.
     */
    protected abstract Action<MavenPublication> getMavenConfiguration(Project target)

    @Override
    final void apply(Project target) {
        this.project = target

        // check conflicts
        checkPluginConflicts(target)

        // apply 'maven-publish' plugin ...
        target.apply plugin: 'maven-publish'

        // ... and create new 'maven' publication
        PublicationContainer publications = target.publishing.publications
        publication = publications.create('maven',
                MavenPublication, getMavenConfiguration(target))

        // create our extension to get user's preferences
        target.extensions.create('publish', Extension, this)
    }

}
