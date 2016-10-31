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

class MethodCheckstyle extends BaseMethod<Extension> {

    static class Extension extends BaseMethodExtension<MethodCheckstyle> {

        Extension(MethodCheckstyle method) {
            super(method,
                    'com.puppycrawl.tools', 'checkstyle', '6.19')
        }

    }

    MethodCheckstyle(Project project) {
        super(project, 'Checkstyle')
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

        project.ant.taskdef(resource: 'com/puppycrawl/tools/checkstyle/ant/checkstyle-ant-task.properties') {
            classpath {
                resolveDependency().each {
                    pathelement(location: it.absolutePath)
                }
            }
        }

        project.ant.checkstyle(config: config, failOnViolation: false) {
            formatter(type: 'xml', toFile: xmlOutput)
            sources.each {
                if (it.exists()) {
                    fileset(dir: it.absolutePath)
                }
            }
        }
    }

}
