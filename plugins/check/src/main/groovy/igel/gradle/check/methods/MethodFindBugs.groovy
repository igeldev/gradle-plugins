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

package igel.gradle.check.methods

import igel.gradle.check.base.BaseMethod
import igel.gradle.check.base.BaseMethodExtension
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class MethodFindBugs extends BaseMethod<Extension> {

    static class Extension extends BaseMethodExtension<MethodFindBugs> {

        Extension(MethodFindBugs method) {
            super(method,
                    'com.google.code.findbugs', 'findbugs', '3.0.1')
        }

    }

    MethodFindBugs(Project project) {
        super(project, 'FindBugs')
    }

    @Override
    protected Extension createExtension() {
        return new Extension(this)
    }

    @Override
    void performCheck(Set<File> sources, JavaCompile javaCompileTask, File config, File xmlOutput) {
        if (sources.findAll { it.exists() }.empty) {
            return
        }

        project.ant.taskdef(name: 'findbugs', classname: 'edu.umd.cs.findbugs.anttask.FindBugsTask') {
            classpath {
                resolveDependency().each {
                    pathelement(location: it.absolutePath)
                }
            }
        }

        project.ant.findbugs(
                effort: 'max',
                reportLevel: 'low',
                excludeFilter: config,
                output: 'xml:withMessages',
                outputFile: xmlOutput) {
            classpath {
                resolveDependency().each {
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

}
