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

import igel.gradle.check.base.BaseProjectHelper
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.compile.JavaCompile

class JavaProjectHelper extends BaseProjectHelper {

    JavaProjectHelper(Project project) {
        super(project)
    }

    @Override
    Set<File> getJavaSources() {
        SourceDirectorySet sources = project.sourceSets.main.java
        return sources.srcDirs
    }

    @Override
    JavaCompile getJavaCompileTask() {
        return project.tasks.find { it instanceof JavaCompile } as JavaCompile
    }

}
