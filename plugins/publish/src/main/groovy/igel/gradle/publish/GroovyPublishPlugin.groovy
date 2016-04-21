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
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Groovydoc

class GroovyPublishPlugin extends BasePublishPlugin {

    @Override
    protected Action<MavenPublication> getMavenConfiguration(Project target) {
        // check that 'groovy' plugin is applied
        // because we need 'java' component, 'main' source set and groovydoc task
        if (!target.plugins.hasPlugin('groovy')) {
            throw new GradleException('plugin \'groovy\' should be applied first')
        }

        // create a task to prepare source code jar
        Task sourcesJarTask = target.task('sourcesJar', type: Jar) {
            group = 'documentation'
            description = 'Assembles a jar archive containing the main source code.'
            classifier = 'sources'
            from target.sourceSets.main.allSource
        }

        // create a task to prepare groovydoc jar
        Groovydoc groovydocTask = target.tasks['groovydoc'] as Groovydoc
        Task groovydocJarTask = target.task('groovydocJar', type: Jar,
                dependsOn: groovydocTask) {
            group = 'documentation'
            description = 'Assembles a jar archive containing the Groovydoc API documentation.'
            classifier = 'groovydoc'
            from groovydocTask.destinationDir
        }

        // return maven configuration
        return { MavenPublication publication ->
            publication.from target.components.java
            publication.artifact sourcesJarTask
            publication.artifact groovydocJarTask
        }
    }

}
