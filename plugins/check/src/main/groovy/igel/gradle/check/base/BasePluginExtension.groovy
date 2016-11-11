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

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project

class BasePluginExtension<P extends BasePlugin> {

    private final Project project
    private final P plugin
    private final Map<String, BaseMethod> checkMethodMap
    final NamedDomainObjectCollection<BaseMethodExtension> methods
    String valueCommon

    BasePluginExtension(Project project, P plugin) {
        this.project = project
        this.plugin = plugin

        Map<String, BaseMethod> checkMethodMapAlias = Collections.unmodifiableMap(
                plugin.methods.collectEntries { [it.name, it] })
        this.checkMethodMap = checkMethodMapAlias

        this.methods = project.container(BaseMethodExtension.class) { String name ->
            if (!checkMethodMapAlias.containsKey(name)) {
                throw new GradleException("Unknown check method '$name'.")
            }
            return checkMethodMapAlias[name].extension
        }
    }

    void methods(Action<NamedDomainObjectCollection<BaseMethodExtension>> action) {
        action.execute(methods)
    }

}