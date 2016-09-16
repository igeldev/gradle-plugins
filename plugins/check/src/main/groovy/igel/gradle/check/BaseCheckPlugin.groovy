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
import org.gradle.api.*

abstract class BaseCheckPlugin<P extends BaseCheckPlugin, E extends Extension> implements Plugin<Project> {

    static class Extension<P extends BaseCheckPlugin> {

        private final Project project
        private final P plugin
        private final Map<String, AbstractCheckMethod> checkMethodMap
        final NamedDomainObjectCollection<AbstractCheckMethod.Extension> methods
        String valueCommon

        Extension(Project project, P plugin) {
            this.project = project
            this.plugin = plugin

            Map<String, AbstractCheckMethod> checkMethodMap = Collections.unmodifiableMap(
                    plugin.createCheckMethods(project).collectEntries { [it.methodName, it] })
            this.checkMethodMap = checkMethodMap

            this.methods = project.container(AbstractCheckMethod.Extension.class) { String name ->
                if (!checkMethodMap.containsKey(name)) {
                    throw new GradleException("Unknown check method '$name'.")
                }
                return checkMethodMap[name].createExtension()
            }
        }

        void methods(Action<NamedDomainObjectCollection<AbstractCheckMethod.Extension>> action) {
            action.execute(methods)
        }

    }

    final Class<E> extensionClass

    BaseCheckPlugin(Class<E> extensionClass) {
        this.extensionClass = extensionClass
    }

    protected abstract Set<AbstractCheckMethod> createCheckMethods(Project project)

    protected abstract void doApply(Project project)

    @Override
    final void apply(Project target) {
        target.extensions.create('check', extensionClass, target, this)

        doApply(target)
    }

}
