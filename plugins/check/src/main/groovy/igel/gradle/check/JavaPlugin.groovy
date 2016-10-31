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
import org.gradle.api.Task
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.compile.JavaCompile

class JavaPlugin extends BasePlugin<JavaPlugin, JavaPluginExtension> {

    JavaPlugin() {
        super(JavaPluginExtension.class)
    }

    @Override
    Set<File> getJavaSources(Project project) {
        SourceDirectorySet sources = project.sourceSets.main.java
        return sources.srcDirs
    }

    @Override
    JavaCompile getJavaCompileTask(Project project) {
        return project.tasks.find { it instanceof JavaCompile } as JavaCompile
    }

    @Override
    protected Set<BaseMethod> createCheckMethods(Project project) {
        return [
                new MethodCheckstyle(project),
                new MethodFindBugs(project),
                new MethodPMD(project),
        ]
    }

    @Override
    protected void doApply(Project project) {
        Task checkTestTask = project.task('check-test')
        project.afterEvaluate { project.tasks['check'].dependsOn checkTestTask }

        methods.each { method ->
            method.prepareDependency()
            checkTestTask << {
                method.extension.resolveConfig()
                method.performCheck(
                        getJavaSources(project),
                        getJavaCompileTask(project),
                        method.extension.configFile,
                        method.extension.reportFile)
            }
        }

        // FindBugs requires compiled code
        project.afterEvaluate { checkTestTask.dependsOn(getJavaCompileTask(project)) }
    }

}
