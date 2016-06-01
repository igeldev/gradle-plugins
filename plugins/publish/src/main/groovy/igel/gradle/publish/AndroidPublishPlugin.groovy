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
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar

class AndroidPublishPlugin extends NewBasePublishPlugin<Extension> {

    static class Extension extends NewBasePublishPlugin.Extension {

        String variantName = 'release'

    }

    AndroidPublishPlugin() {
        super('com.android.library',
                Extension, 'publishAndroid',
                'android')
    }

    private def findLibraryVariant(Project project, Extension extension) {
        def variant = project.android.libraryVariants.find { it.name == extension.variantName }
        if (variant == null) {
            throw new GradleException("" +
                    "Library variant '$extension.variantName' is not found.\n" +
                    "Please specify variant to publish in build.gradle using:\n" +
                    "\n" +
                    "$extensionName {\n" +
                    "    variantName = 'customRelease'\n" +
                    "}")
        }

        return variant
    }

    private static DefaultPublishArtifact prepareArtifact(def variant) {
        if (variant.outputs.size() <= 0) {
            throw new GradleException("Library variant $variant.name has no outputs.")
        }

        Task task = variant.assemble
        File file = variant.outputs[0].outputFile
        return new DefaultPublishArtifact(tasks: [task], extension: 'aar', file: file)
    }

    private Task createSourcesTask(Project project, def variant) {
        return project.task("generate${variant.name.capitalize()}SourcesJar", type: Jar) {
            group = 'sources'
            description = "Generates sources jar for '$variant.name' build variant."
            classifier = "${variant.name}-sources"

            variant.sourceSets.each { sourceSet ->
                // Java source
                from sourceSet.java.sourceFiles

                // AIDL source and generated code
                from sourceSet.aidl.sourceFiles
                from variant.aidlCompile.sourceOutputDir
            }
        }
    }

    private Task createJavadocTask(Project project, def variant) {
        Task javadocTask = project.task("generate${variant.name.capitalize()}Javadoc",
                type: Javadoc) {
            group = 'Javadoc'
            description = "Generates Javadoc for $variant.name build."

            File dir = destinationDir
            destinationDir = new File(dir.parentFile, "$dir.name-$variant.name")

            variant.sourceSets.each { sourceSet ->
                // Java source
                source sourceSet.java.sourceFiles

                // AIDL generated code
                source variant.aidlCompile.sourceOutputDir
            }

            classpath = variant.javaCompile.classpath +
                    project.files(variant.javaCompile.options.bootClasspath) +
                    project.files(variant.javaCompile.destinationDir)

            options.links(
                    'https://developer.android.com/reference/',
                    'http://docs.oracle.com/javase/7/docs/api/')
        }

        return project.task("generate${variant.name.capitalize()}JavadocJar",
                type: Jar, dependsOn: javadocTask) {
            group = 'Javadoc'
            description = "Generates Javadoc jar for $variant.name build."
            classifier = "${variant.name}-javadoc"
            from javadocTask.destinationDir
        }

    }

    @Override
    protected Action<MavenPublication> prepareMavenConfiguration(Project project, Extension extension) {
        Map<String, Task> sourcesTask = [:]
        Map<String, Task> javadocTask = [:]
        project.android.libraryVariants.all { variant ->
            String variantName = variant.name
            sourcesTask[variantName] = createSourcesTask(project, variant)
            javadocTask[variantName] = createJavadocTask(project, variant)
        }

        def variant = findLibraryVariant(project, extension)
        return { MavenPublication publication ->
            publication.artifact prepareArtifact(variant)

            String variantName = variant.name
            publication.artifact(sourcesTask[variantName]) { classifier 'sources' }
            publication.artifact(javadocTask[variantName]) { classifier 'javadoc' }
        }
    }

}
