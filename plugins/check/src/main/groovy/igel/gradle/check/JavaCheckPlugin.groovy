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

package igel.gradle.check

import igel.gradle.check.methods.AbstractCheckMethod
import igel.gradle.check.methods.Method1
import igel.gradle.check.methods.Method2
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.SourceDirectorySet

class JavaCheckPlugin extends BaseCheckPlugin<JavaCheckPlugin, Extension> {

    static class Extension extends BaseCheckPlugin.Extension<JavaCheckPlugin> {

        String valueJava

        Extension(Project project, JavaCheckPlugin plugin) {
            super(project, plugin)
        }

    }

    JavaCheckPlugin() {
        super(Extension.class)
    }

    @Override
    protected Set<AbstractCheckMethod> createCheckMethods(Project project) {
        return [new Method1(project), new Method2(project)]
    }

    static File copyResource(Project project, String resourcePath, File outputFile) {
        ClassLoader loader = project.buildscript.classLoader

        outputFile.delete()
        outputFile.parentFile.mkdirs()
        outputFile << loader.getResourceAsStream(resourcePath).text
        return outputFile
    }

    private Configuration configurationCheckstyle

    private void prepareCheckstyle(Project project) {
        configurationCheckstyle = project.configurations.create('checkCheckstyle')
        configurationCheckstyle.description = 'Checkstyle dependencies.'
        configurationCheckstyle.visible = false

        configurationCheckstyle.defaultDependencies { dependencies ->
            dependencies.add(project.dependencies.create('com.puppycrawl.tools:checkstyle:6.19'))
        }
    }

    private void performCheckstyle(Project project) {
        project.ant.taskdef(resource: 'com/puppycrawl/tools/checkstyle/ant/checkstyle-ant-task.properties') {
            classpath {
                configurationCheckstyle.resolve().each {
                    pathelement(location: it.absolutePath)
                }
            }
        }

        File configFile = copyResource(project, 'configCheckstyle/config.xml',
                project.file('build/check/checkstyle/config.xml'))
        SourceDirectorySet sources = project.sourceSets.main.java
        if (sources.findAll { it.exists() }.empty) {
            return
        }
        project.ant.checkstyle(config: configFile, failOnViolation: false) {
            formatter(type: 'xml', toFile: project.file('build/check/checkstyle/report.xml'))
            sources.getSrcDirs().each { fileset(dir: it.absolutePath) }
        }
    }

    @Override
    protected void doApply(Project project) {
        Task checkTestTask = project.task('check-test')
        project.afterEvaluate { project.tasks['check'].dependsOn checkTestTask }

        // Checkstyle
        prepareCheckstyle(project)
        checkTestTask << { performCheckstyle(project) }
    }

}
