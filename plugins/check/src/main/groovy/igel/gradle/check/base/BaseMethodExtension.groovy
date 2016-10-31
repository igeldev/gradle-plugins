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

class BaseMethodExtension<M extends BaseMethod> {

    final M method

    private final String defaultDependencyGroup
    private final String defaultDependencyModule
    private final String defaultDependencyVersion
    private String dependency

    final File configFile
    final File reportFile

    BaseMethodExtension(
            M method,
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
