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

import groovy.xml.QName
import org.gradle.api.*
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication

abstract class NewBasePublishPlugin<E extends Extension> implements Plugin<Project> {

    static class Extension {

        String publicationName

    }

    static final String POM_CONTENT_FILENAME = 'pom-content.xml'
    private static final String POM_XMLNS_URI = 'http://maven.apache.org/POM/4.0.0'
    private static final String POM_TAG_MODEL_VERSION = 'modelVersion'
    private static final String POM_TAG_GROUP = 'groupId'
    private static final String POM_TAG_ARTIFACT_ID = 'artifactId'
    private static final String POM_TAG_VERSION = 'version'
    private static final String POM_TAG_PACKAGING = 'packaging'
    private static final String POM_TAG_DEPENDENCIES = 'dependencies'

    protected static void checkGroupAndVersion(Project project) {
        if (!project.group) {
            throw new GradleException("" +
                    "Project group isn't set.\n" +
                    "Please specify it in build.gradle:\n" +
                    "\n" +
                    "group = 'com.your.group.name'")
        }

        if (!project.version || project.version == Project.DEFAULT_VERSION) {
            throw new GradleException("" +
                    "Project version isn't set.\n" +
                    "Please specify it in build.gradle:\n" +
                    "\n" +
                    "version = '1.0.0'")
        }
    }

    /**
     * Creates POM configuration action.
     * @param project the target project.
     * @param pomContentFile file with POM content.
     * @param compileConfiguration configuration containing compile POM dependencies.
     * @return
     */
    protected static Action<? super XmlProvider> createPomConfiguration(Project project, File pomContentFile,
                                                                        DependencySet compileDependencies) {
        return { XmlProvider provider ->
            Node node = provider.asNode()

            if (pomContentFile != null && pomContentFile.exists()) {
                Node pomNode = new XmlParser().parseText(pomContentFile.text)
                pomNode.children().each { Node child ->
                    QName name = child.name() as QName
                    if (name.namespaceURI != POM_XMLNS_URI) {
                        node.append(child)
                    } else {
                        switch (name.localPart) {
                            case POM_TAG_MODEL_VERSION:
                                project.logger.warn("" +
                                        "Please remove <$POM_TAG_MODEL_VERSION> from your $POM_CONTENT_FILENAME")
                                break
                            case POM_TAG_GROUP:
                                project.logger.warn("" +
                                        "Please remove <$POM_TAG_GROUP> from your $POM_CONTENT_FILENAME\n" +
                                        "  (Value of project.group will be used)")
                                break
                            case POM_TAG_ARTIFACT_ID:
                                project.logger.warn("" +
                                        "Please remove <$POM_TAG_ARTIFACT_ID> from your $POM_CONTENT_FILENAME\n" +
                                        "  (Value of project.name will be used)")
                                break
                            case POM_TAG_VERSION:
                                project.logger.warn("" +
                                        "Please remove <$POM_TAG_VERSION> from your $POM_CONTENT_FILENAME\n" +
                                        "  (Value of project.version will be used)")
                                break
                            case POM_TAG_PACKAGING:
                                project.logger.warn("" +
                                        "Please remove <$POM_TAG_PACKAGING> from your $POM_CONTENT_FILENAME")
                                break
                            case POM_TAG_DEPENDENCIES:
                                project.logger.warn("" +
                                        "Please remove <$POM_TAG_DEPENDENCIES> from your $POM_CONTENT_FILENAME\n" +
                                        "  (Gradle dependencies will be used)")
                                break
                            default:
                                node.append(child)
                        }
                    }
                }
            }

            Node dependenciesNode = node.appendNode(POM_TAG_DEPENDENCIES)
            compileDependencies.each { Dependency dependency ->
                Node dependencyNode = dependenciesNode.appendNode('dependency')
                dependencyNode.appendNode('scope', 'compile')
                dependencyNode.appendNode('groupId', dependency.group)
                dependencyNode.appendNode('artifactId', dependency.name)
                dependencyNode.appendNode('version', dependency.version)
            }
        } as Action
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
            // check that group and version are set
            checkGroupAndVersion(target)

            // prepare maven publication configuration
            Action<MavenPublication> configuration = prepareMavenConfiguration(target, extension)

            // create and configure maven publication
            PublicationContainer publications = target.publishing.publications
            publications.create(extension.publicationName, MavenPublication, configuration)
        }
    }

}
