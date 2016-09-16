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

    @Override
    protected void doApply(Project project) {
    }

}
