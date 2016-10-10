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

import org.gradle.api.Project

class Method1 extends BaseCheckMethod<Extension> {

    static class Extension extends BaseCheckMethod.Extension<Method1> {

        String value1

        Extension(Method1 method) {
            super(method)
        }

    }

    Method1(Project project) {
        super(project, 'method1')
    }

    @Override
    Extension createExtension() {
        return new Extension(this)
    }

}
