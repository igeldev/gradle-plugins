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

import igel.gradle.check.base.BaseMethod
import igel.gradle.check.base.BasePlugin
import igel.gradle.check.methods.MethodCheckstyle
import igel.gradle.check.methods.MethodFindBugs
import igel.gradle.check.methods.MethodPMD
import org.gradle.api.Project

class AndroidPlugin extends BasePlugin<AndroidProjectHelper, AndroidPluginExtension> {

    AndroidPlugin() {
        super(AndroidPluginExtension.class)
    }

    @Override
    protected AndroidProjectHelper createProjectHelper(Project project) {
        return new AndroidProjectHelper(project)
    }

    @Override
    protected Set<BaseMethod> createCheckMethods(Project project) {
        return [
                new MethodCheckstyle(project),
                new MethodFindBugs(project),
                new MethodPMD(project),
        ]
    }

}
