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

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication

abstract class NewBasePublishPlugin<E extends Extension> implements Plugin<Project> {

    static class Extension {

        String publicationName

    }

    protected final String requiredPlugin
    protected final Class<E> extensionClass
    protected final String extensionName
    protected final String defaultPublicationName

    NewBasePublishPlugin(String requiredPlugin,
                         Class<E> extensionClass, String extensionName,
                         String defaultPublicationName) {
        this.requiredPlugin = requiredPlugin
        this.extensionClass = extensionClass
        this.extensionName = extensionName
        this.defaultPublicationName = defaultPublicationName
    }

    /**
     * Returns configuration of MavenPublication.
     * <br/>
     * <b>Called only once.</b>
     *
     * @param target the target project.
     * @return configuration closure.
     */
    protected abstract Action<MavenPublication> prepareMavenConfiguration(Project project, E extension)

    @Override
    final void apply(Project target) {
        // check that required plugin is applied
        if (!target.plugins.hasPlugin(requiredPlugin)) {
            throw new GradleException("plugin '$requiredPlugin' should be applied first")
        }

        // apply 'maven-publish' plugin
        target.apply plugin: 'maven-publish'

        // create our extension to get user's preferences
        E extension = target.extensions.create(extensionName, extensionClass)
        extension.publicationName = defaultPublicationName

        // create publication AFTER evaluation
        target.afterEvaluate {
            // prepare maven publication configuration
            Action<MavenPublication> configuration = prepareMavenConfiguration(target, extension)

            // create and configure maven publication
            PublicationContainer publications = target.publishing.publications
            publications.create(extension.publicationName, MavenPublication, configuration)
        }
    }

}
