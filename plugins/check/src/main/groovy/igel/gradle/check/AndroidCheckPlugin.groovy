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
import igel.gradle.check.methods.MethodCheckstyle
import igel.gradle.check.methods.MethodFindBugs
import igel.gradle.check.methods.MethodPMD
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.compile.JavaCompile

class AndroidCheckPlugin extends BaseCheckPlugin<AndroidCheckPlugin, Extension> {

    static class Extension extends BaseCheckPlugin.Extension<AndroidCheckPlugin> {

        String valueAndroid

        Extension(Project project, AndroidCheckPlugin plugin) {
            super(project, plugin)
        }

    }

    AndroidCheckPlugin() {
        super(Extension.class)
    }

    @Override
    Set<File> getJavaSources(Project project) {
        def variant = project.android.libraryVariants[0]
        return variant.sourceSets.inject([]) { dirs, sourceSet ->
            dirs + sourceSet.javaDirectories
        }
    }

    @Override
    JavaCompile getJavaCompileTask(Project project) {
        def variant = project.android.libraryVariants[0]
        return variant.javaCompile
    }

    @Override
    protected Set<BaseCheckMethod> createCheckMethods(Project project) {
        return []
    }

    static File copyResource(Project project, String resourcePath, File outputFile) {
        ClassLoader loader = project.buildscript.classLoader

        outputFile.delete()
        outputFile.parentFile.mkdirs()
        outputFile << loader.getResourceAsStream(resourcePath).text
        return outputFile
    }

    private MethodCheckstyle methodCheckstyle
    private MethodFindBugs methodFindBugs
    private MethodPMD methodPMD

    @Override
    protected void doApply(Project project) {
        Task checkTestTask = project.task('check-test')
        project.afterEvaluate { project.tasks['check'].dependsOn checkTestTask }

        // Checkstyle
        methodCheckstyle = new MethodCheckstyle(project)
        methodCheckstyle.prepareDependency()
        checkTestTask << {
            methodCheckstyle.extension.resolveConfig()
            methodCheckstyle.performCheck(
                    getJavaSources(project),
                    getJavaCompileTask(project),
                    methodCheckstyle.extension.configFile,
                    methodCheckstyle.extension.reportFile)
        }

        // FindBugs
        methodFindBugs = new MethodFindBugs(project)
        methodFindBugs.prepareDependency()
        project.afterEvaluate { checkTestTask.dependsOn(getJavaCompileTask(project)) }
        checkTestTask << {
            methodFindBugs.extension.resolveConfig()
            methodFindBugs.performCheck(
                    getJavaSources(project),
                    getJavaCompileTask(project),
                    methodFindBugs.extension.configFile,
                    methodFindBugs.extension.reportFile)
        }

        // PMD
        methodPMD = new MethodPMD(project)
        methodPMD.prepareDependency()
        checkTestTask << {
            methodPMD.extension.resolveConfig()
            methodPMD.performCheck(
                    getJavaSources(project),
                    getJavaCompileTask(project),
                    methodPMD.extension.configFile,
                    methodPMD.extension.reportFile)
        }
    }

}
