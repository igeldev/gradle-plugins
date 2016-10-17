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
import org.gradle.api.*
import org.gradle.api.tasks.compile.JavaCompile

abstract class BaseCheckPlugin<P extends BaseCheckPlugin, E extends Extension> implements Plugin<Project> {

    static class Extension<P extends BaseCheckPlugin> {

        private final Project project
        private final P plugin
        private final Map<String, BaseCheckMethod> checkMethodMap
        final NamedDomainObjectCollection<BaseCheckMethod.Extension> methods
        String valueCommon

        Extension(Project project, P plugin) {
            this.project = project
            this.plugin = plugin

            Map<String, BaseCheckMethod> checkMethodMapAlias = Collections.unmodifiableMap(
                    plugin.createCheckMethods(project).collectEntries { [it.name, it] })
            this.checkMethodMap = checkMethodMapAlias

            this.methods = project.container(BaseCheckMethod.Extension.class) { String name ->
                if (!checkMethodMapAlias.containsKey(name)) {
                    throw new GradleException("Unknown check method '$name'.")
                }
                return checkMethodMapAlias[name].extension
            }
        }

        void methods(Action<NamedDomainObjectCollection<BaseCheckMethod.Extension>> action) {
            action.execute(methods)
        }

    }

    final Class<E> extensionClass

    BaseCheckPlugin(Class<E> extensionClass) {
        this.extensionClass = extensionClass
    }

    abstract Set<File> getJavaSources(Project project)

    abstract JavaCompile getJavaCompileTask(Project project)

    protected abstract Set<BaseCheckMethod> createCheckMethods(Project project)

    protected abstract void doApply(Project project)

    @Override
    final void apply(Project target) {
        target.extensions.create('check', extensionClass, target, this)

        doApply(target)
    }

}
