package igel.gradle.publish

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar

class GroovyPublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project target) {
        target.afterEvaluate {
            if (!target.plugins.hasPlugin('groovy')) {
                throw new GradleException("plugin 'groovy' should be applied")
            }

            target.apply plugin: 'maven-publish'

            Task sourcesJarTask = target.task('sourcesJar', type: Jar) {
                group = 'documentation'
                description = 'Assembles a jar archive containing the main source code.'
                classifier = 'sources'
                from target.sourceSets.main.allSource
            }

            target.publishing.publications.create('maven', MavenPublication) {
                from target.components.java
                artifact sourcesJarTask
            }
        }
    }

}
