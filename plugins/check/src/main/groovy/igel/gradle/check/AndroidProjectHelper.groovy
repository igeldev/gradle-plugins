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

import igel.gradle.check.base.ProjectHelper
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class AndroidProjectHelper extends ProjectHelper {

    AndroidProjectHelper(Project project) {
        super(project)
    }

    @Override
    Set<File> getJavaSources() {
        def variant = project.android.libraryVariants[0]
        return variant.sourceSets.inject([]) { dirs, sourceSet ->
            dirs + sourceSet.javaDirectories
        }
    }

    @Override
    JavaCompile getJavaCompileTask() {
        def variant = project.android.libraryVariants[0]
        return variant.javaCompile
    }

}
