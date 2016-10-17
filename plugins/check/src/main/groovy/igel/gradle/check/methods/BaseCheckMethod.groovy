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

import igel.gradle.check.Utils
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.compile.JavaCompile

abstract class BaseCheckMethod<E extends Extension> {

    static class Extension<M extends BaseCheckMethod> {

        final M method

        private final String defaultDependencyGroup
        private final String defaultDependencyModule
        private final String defaultDependencyVersion
        private String dependency

        final File configFile
        final File reportFile

        Extension(M method,
                  String defaultDependencyGroup, String defaultDependencyModule, String defaultDependencyVersion) {
            this.method = method

            this.defaultDependencyGroup = defaultDependencyGroup
            this.defaultDependencyModule = defaultDependencyModule
            this.defaultDependencyVersion = defaultDependencyVersion
            this.dependency = null

            this.configFile = method.project.file("build/check/$method.name/config.xml")
            this.reportFile = method.project.file("build/check/$method.name/report.xml")
        }

        void version(String version) {
            dependency("$defaultDependencyGroup:$defaultDependencyModule:$version")
        }

        void dependency(String dependency) {
            if (dependency) {
                throw new IllegalStateException("Dependency of $method.name is already set as '$dependency'")
            }
            this.dependency = dependency
        }

        String getDependency() {
            return dependency ?: "$defaultDependencyGroup:$defaultDependencyModule:$defaultDependencyVersion"
        }

        void resolveConfig() {
            Utils.copyResource(method.project, "config$method.name/config.xml", configFile)
        }

    }

    final Project project
    final String name
    final E extension
    private Configuration configuration

    BaseCheckMethod(Project project, String name) {
        this.project = project
        this.name = name
        this.extension = createExtension()
    }

    protected abstract E createExtension()

    void prepareDependency() {
        configuration = project.configurations.create("___igel_check_${name}___")
        configuration.description = 'Checkstyle dependencies.'
        configuration.visible = false

        Project projectAlias = project
        Extension extensionAlias = extension
        configuration.defaultDependencies { dependencies ->
            dependencies.add(projectAlias.dependencies.create(extensionAlias.dependency))
        }
    }

    Set<File> resolveDependency() {
        return configuration.resolve()
    }

    abstract void performCheck(Set<File> sources, JavaCompile javaCompileTask, File config, File xmlOutput)

}
