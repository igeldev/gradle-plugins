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

import igel.gradle.check.BaseProjectHelper
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

abstract class BasePlugin<P extends BasePlugin, E extends BasePluginExtension> implements Plugin<Project> {

    final Class<E> extensionClass
    private Set<BaseMethod> methods

    BasePlugin(Class<E> extensionClass) {
        this.extensionClass = extensionClass
    }

    Set<BaseMethod> getMethods() {
        return methods
    }

    abstract Set<File> getJavaSources(Project project)

    abstract JavaCompile getJavaCompileTask(Project project)

    protected abstract Set<BaseMethod> createCheckMethods(Project project)

    protected abstract void doApply(Project project)

    @Override
    final void apply(Project target) {
        methods = createCheckMethods(target)
        target.extensions.create('check', extensionClass, target, this)

        doApply(target)
    }

}
