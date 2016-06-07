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
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.XmlProvider
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.javadoc.Groovydoc
import org.gradle.jvm.tasks.Jar

class GroovyPublishPlugin extends BasePublishPlugin<BasePublishPlugin.Extension> {

    GroovyPublishPlugin() {
        super('groovy',
                Extension, 'publishGroovy',
                'groovy')
    }

    private static DefaultPublishArtifact prepareArtifact(Project project) {
        Jar jarTask = project.tasks['jar'] as Jar
        File jarFile = jarTask.archivePath
        return new DefaultPublishArtifact(tasks: [jarTask], extension: 'jar', file: jarFile)
    }

    private static File findPomContentFile(Project project) {
        List<File> files = [
                project.file("src/main/$POM_CONTENT_FILENAME"),
        ]
        return files.find { it.exists() }
    }

    private Task createSourcesTask(Project project) {
        return project.task("sourcesJar", type: Jar) {
            group = 'documentation'
            description = 'Assembles a jar archive containing the main source code.'
            classifier = 'sources'
            from project.sourceSets.main.allSource
        }
    }

    private Task createGroovydocTask(Project project) {
        Groovydoc groovydocTask = project.tasks['groovydoc'] as Groovydoc
        return project.task('groovydocJar', type: Jar, dependsOn: groovydocTask) {
            group = 'documentation'
            description = 'Assembles a jar archive containing the Groovydoc API documentation.'
            classifier = 'groovydoc'
            from groovydocTask.destinationDir
        }
    }

    @Override
    protected Action<MavenPublication> prepareMavenConfiguration(Project project) {
        Task sourcesTask = createSourcesTask(project)
        Task groovydocTask = createGroovydocTask(project)

        Action<? super XmlProvider> pomConfiguration = createPomConfiguration(
                project, findPomContentFile(project),
                project.configurations.compile.allDependencies)

        return { MavenPublication publication ->
            publication.artifact prepareArtifact(project)

            if (extension.publishSrc) {
                publication.artifact sourcesTask
            }
            if (extension.publishDoc) {
                publication.artifact groovydocTask
            }

            publication.pom.withXml pomConfiguration
        }
    }

}
