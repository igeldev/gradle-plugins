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

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

abstract class BaseProjectHelper<E extends BasePluginExtension> {

    private static final String EXTENSION_NAME = 'check'

    final Project project
    private E extension

    BaseProjectHelper(Project project) {
        this.project = project
    }

    abstract Class<E> getExtensionClass()

    void createExtension(Set<BaseMethod> methods) {
        extension = project.extensions.create(EXTENSION_NAME, extensionClass, project, methods)
    }

    E getExtension() {
        return extension
    }

    abstract Set<File> getJavaSources()

    abstract JavaCompile getJavaCompileTask()

}
