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
import igel.gradle.check.methods.MethodCheckstyle
import igel.gradle.check.methods.MethodFindBugs
import igel.gradle.check.methods.MethodPMD
import org.gradle.api.Project
import org.gradle.api.Task
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
    Set<File> getJavaSources(Project project) {
        def variant = project.android.libraryVariants[0]
        return variant.sourceSets.inject([]) { dirs, sourceSet ->
            dirs + sourceSet.javaDirectories
        }
    }

    @Override
    JavaCompile getJavaCompileTask(Project project) {
        def variant = project.android.libraryVariants[0]
        return variant.javaCompile
    }

    @Override
    protected Set<BaseCheckMethod> createCheckMethods(Project project) {
        return []
    }

    static File copyResource(Project project, String resourcePath, File outputFile) {
        ClassLoader loader = project.buildscript.classLoader

        outputFile.delete()
        outputFile.parentFile.mkdirs()
        outputFile << loader.getResourceAsStream(resourcePath).text
        return outputFile
    }

    private MethodCheckstyle methodCheckstyle

    private void performCheckstyle(Project project) {
        Set<File> sources = getJavaSources(project)
        if (sources.findAll { it.exists() }.empty) {
            return
        }

        project.ant.taskdef(resource: 'com/puppycrawl/tools/checkstyle/ant/checkstyle-ant-task.properties') {
            classpath {
                methodCheckstyle.resolveDependency().each {
                    pathelement(location: it.absolutePath)
                }
            }
        }

        File configFile = copyResource(project, 'configCheckstyle/config.xml',
                project.file('build/check/checkstyle/config.xml'))
        project.ant.checkstyle(config: configFile, failOnViolation: false) {
            formatter(type: 'xml', toFile: project.file('build/check/checkstyle/report.xml'))
            sources.each {
                if (it.exists()) {
                    fileset(dir: it.absolutePath)
                }
            }
        }
    }

    private MethodFindBugs methodFindBugs

    private void performFindBugs(Project project) {
        Set<File> sources = getJavaSources(project)
        if (sources.findAll { it.exists() }.empty) {
            return
        }

        project.ant.taskdef(name: 'findbugs', classname: 'edu.umd.cs.findbugs.anttask.FindBugsTask') {
            classpath {
                methodFindBugs.resolveDependency().each {
                    pathelement(location: it.absolutePath)
                }
            }
        }

        JavaCompile javaCompileTask = getJavaCompileTask(project)
        File configFile = copyResource(project, 'configFindBugs/config.xml',
                project.file('build/check/findbugs/config.xml'))
        project.ant.findbugs(
                effort: 'max',
                reportLevel: 'low',
                excludeFilter: configFile,
                output: 'xml:withMessages',
                outputFile: project.file('build/check/findbugs/report.xml')) {
            classpath {
                methodFindBugs.resolveDependency().each {
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

    private MethodPMD methodPMD

    private void performPMD(Project project) {
        Set<File> sources = getJavaSources(project)
        if (sources.findAll { it.exists() }.empty) {
            return
        }

        project.ant.taskdef(name: 'pmd', classname: 'net.sourceforge.pmd.ant.PMDTask') {
            classpath {
                methodPMD.resolveDependency().each {
                    pathelement(location: it.absolutePath)
                }
            }
        }

        File configFile = copyResource(project, 'configPMD/config.xml',
                project.file('build/check/pmd/config.xml'))
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
        methodCheckstyle = new MethodCheckstyle(project)
        methodCheckstyle.prepareDependency()
        checkTestTask << { performCheckstyle(project) }

        // FindBugs
        methodFindBugs = new MethodFindBugs(project)
        methodFindBugs.prepareDependency()
        project.afterEvaluate { checkTestTask.dependsOn(getJavaCompileTask(project)) }
        checkTestTask << { performFindBugs(project) }

        // PMD
        methodPMD = new MethodPMD(project)
        methodPMD.prepareDependency()
        checkTestTask << { performPMD(project) }
    }

}
