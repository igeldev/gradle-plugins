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

package igel.gradle.check.base

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

abstract class BasePlugin<P extends ProjectHelper, E extends BasePluginExtension> implements Plugin<Project> {

    final Class<E> extensionClass
    private Set<BaseMethod> methods

    BasePlugin(Class<E> extensionClass) {
        this.extensionClass = extensionClass
    }

    Set<BaseMethod> getMethods() {
        return methods
    }

    protected abstract P createProjectHelper(Project project)

    protected abstract Set<BaseMethod> createCheckMethods(Project project)

    @Override
    final void apply(Project target) {
        methods = createCheckMethods(target)
        target.extensions.create('check', extensionClass, target, this)

        Task checkTestTask = target.task('check-test')
        target.afterEvaluate { target.tasks['check'].dependsOn checkTestTask }

        ProjectHelper projectHelper = createProjectHelper(target)
        methods.each { method ->
            method.prepareDependency()
            checkTestTask << {
                method.extension.resolveConfig()
                method.performCheck(
                        projectHelper.getJavaSources(),
                        projectHelper.getJavaCompileTask(),
                        method.extension.configFile,
                        method.extension.reportFile)
            }
        }

        // FindBugs requires compiled code
        target.afterEvaluate { checkTestTask.dependsOn(projectHelper.getJavaCompileTask()) }
    }

}
