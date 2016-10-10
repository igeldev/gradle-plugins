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

import igel.gradle.check.methods.BaseCheckMethod
import igel.gradle.check.methods.Method1
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.compile.JavaCompile

class AndroidCheckPlugin extends BaseCheckPlugin<AndroidCheckPlugin, Extension> {

    static class Extension extends BaseCheckPlugin.Extension<AndroidCheckPlugin> {

        String valueAndroid

        Extension(Project project, AndroidCheckPlugin plugin) {
            super(project, plugin)
        }

    }

    AndroidCheckPlugin() {
        super(Extension.class)
    }

    @Override
    protected Set<BaseCheckMethod> createCheckMethods(Project project) {
        return [new Method1(project)]
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

        def variant = project.android.libraryVariants[0]
        List<File> sources = variant.sourceSets.inject([]) { dirs, sourceSet -> dirs + sourceSet.javaDirectories }

        File configFile = copyResource(project, 'configCheckstyle/config.xml',
                project.file('build/check/checkstyle/config.xml'))
        if (sources.findAll { it.exists() }.empty) {
            return
        }
        project.ant.checkstyle(config: configFile, failOnViolation: false) {
            formatter(type: 'xml', toFile: project.file('build/check/checkstyle/report.xml'))
            sources.each {
                if (it.exists()) {
                    fileset(dir: it.absolutePath)
                }
            }
        }
    }

    private Configuration configurationFindBugs

    private void prepareFindBugs(Project project) {
        configurationFindBugs = project.configurations.create('checkFindBugs')
        configurationFindBugs.description = 'FindBugs dependencies.'
        configurationFindBugs.visible = false

        configurationFindBugs.defaultDependencies { dependencies ->
            dependencies.add(project.dependencies.create('com.google.code.findbugs:findbugs:3.0.1'))
        }
    }

    private List<Task> getDepsFindBugs(Project project) {
        def variant = project.android.libraryVariants[0]
        return [variant.javaCompile]
    }

    private void performFindBugs(Project project) {
        project.ant.taskdef(name: 'findbugs', classname: 'edu.umd.cs.findbugs.anttask.FindBugsTask') {
            classpath {
                configurationFindBugs.resolve().each {
                    pathelement(location: it.absolutePath)
                }
            }
        }

        def variant = project.android.libraryVariants[0]
        List<File> sources = variant.sourceSets.inject([]) { dirs, sourceSet -> dirs + sourceSet.javaDirectories }

        JavaCompile javaCompileTask = variant.javaCompile as JavaCompile
        File configFile = copyResource(project, 'configFindBugs/config.xml',
                project.file('build/check/findbugs/config.xml'))
        if (sources.findAll { it.exists() }.empty) {
            return
        }
        project.ant.findbugs(
                effort: 'max',
                reportLevel: 'low',
                excludeFilter: configFile,
                output: 'xml:withMessages',
                outputFile: project.file('build/check/findbugs/report.xml')) {
            classpath {
                configurationFindBugs.resolve().each {
                    pathelement(location: it.absolutePath)
                }
            }
            fileset(dir: javaCompileTask.destinationDir)
            sourcePath {
                sources.each {
                    if (it.exists()) {
                        pathelement(location: it.absolutePath)
                    }
                }
            }
            auxClasspath {
                javaCompileTask.classpath.each {
                    pathelement(location: it.absolutePath)
                }
            }
        }
    }

    private Configuration configurationPMD

    private void preparePMD(Project project) {
        configurationPMD = project.configurations.create('checkPMD')
        configurationPMD.description = 'PMD dependencies.'
        configurationPMD.visible = false

        configurationPMD.defaultDependencies { dependencies ->
            dependencies.add(project.dependencies.create('net.sourceforge.pmd:pmd-java:5.5.1'))
        }
    }

    private void performPMD(Project project) {
        project.ant.taskdef(name: 'pmd', classname: 'net.sourceforge.pmd.ant.PMDTask') {
            classpath {
                configurationPMD.resolve().each {
                    pathelement(location: it.absolutePath)
                }
            }
        }

        def variant = project.android.libraryVariants[0]
        List<File> sources = variant.sourceSets.inject([]) { dirs, sourceSet -> dirs + sourceSet.javaDirectories }

        File configFile = copyResource(project, 'configPMD/config.xml',
                project.file('build/check/pmd/config.xml'))
        if (sources.findAll { it.exists() }.empty) {
            return
        }
        project.ant.pmd(
                rulesetfiles: configFile,
                failonerror: true) {
            formatter(type: 'xml', toFile: project.file('build/check/pmd/report.xml'))
            sources.each {
                if (it.exists()) {
                    fileset(dir: it.absolutePath)
                }
            }
        }
    }

    @Override
    protected void doApply(Project project) {
        Task checkTestTask = project.task('check-test')
        project.afterEvaluate { project.tasks['check'].dependsOn checkTestTask }

        // Checkstyle
        prepareCheckstyle(project)
        checkTestTask << { performCheckstyle(project) }

        // FindBugs
        prepareFindBugs(project)
        project.afterEvaluate { checkTestTask.dependsOn(getDepsFindBugs(project)) }
        checkTestTask << { performFindBugs(project) }

        // PMD
        preparePMD(project)
        checkTestTask << { performPMD(project) }
    }

}
