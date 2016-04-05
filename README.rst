==============
Gradle Plugins
==============

----------------------------
Plugin 'igel.publish.groovy'
----------------------------

The goal of this plugin is to make easier publishing Groovy libraries to `Bintray JCenter <https://bintray.com/bintray/jcenter>`__.

It uploads *binaries JAR*, *sources JAR* and *POM file* to **Maven local repository** and / or **JCenter**.

Example of Groovy library project configuration is placed `here <examples/groovy-lib>`__.

Usage
-----

1. Check your Groovy library project uses ``'groovy'`` plugin:

   .. code-block:: groovy

       apply plugin: 'groovy'

2. Apply plugin ``'igel.publish.groovy'``:

   **build.gradle**:

   .. code-block:: groovy

       buildscript {
           repositories {
               jcenter()
           }
           dependencies {
               classpath 'igel.gradle:publish:1.0.0'
           }
       }

       apply plugin: 'groovy'

       group = 'com.example'
       version = '1.2.3'

       apply plugin: 'igel.publish.groovy'

3. Rename project if it is needed (project name is used as artifact ID):

   **settings.gradle**:

   .. code-block:: groovy

       rootProject.name = 'groovy-library'

4. Now you can use task **publishToMavenLocal** to publish artifacts to **Maven local repository**:

   .. code-block:: bash

       ./gradlew publishToMavenLocal

5. Configure content of *POM file* (using internal `MarkupBuilder <http://docs.groovy-lang.org/latest/html/api/groovy/xml/MarkupBuilder.html>`__):

   **build.gradle**:

   .. code-block:: groovy

       // ...

       apply plugin: 'igel.publish.groovy'

       publishGroovy {
           pom {
               name 'Groovy library'
               // because tag 'description' conflicts with project.description
               description([:], 'Description of Groovy library')
               url 'https://github.com/john-jameson/groovy-library'
               licenses {
                   license {
                       name 'Apache License, Version 2.0'
                       url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                       distribution 'repo'
                   }
               }
           }
       }

6. Configure bintray *inside publishGroovy block* using imported configuration of `gradle-bintray-plugin <https://github.com/bintray/gradle-bintray-plugin>`__:

   **build.gradle**:

   .. code-block:: groovy

       publishGroovy {
           pom {
               // ...
           }
           bintray {
               user = project.hasProperty('bintray.user') ? project['bintray.user'] : 'anonymous'
               key = project.hasProperty('bintray.key') ? project['bintray.key'] : '???'
               pkg {
                   repo = 'groovy-library'
                   websiteUrl = 'https://github.com/john-jameson/groovy-library'
                   licenses = ['Apache-2.0']
               }
           }
       }

7. Now you can use task **bintrayUpload** to publish artifacts to **JCenter**:

   .. code-block:: bash

       ./gradlew bintrayUpload -Pbintray.user=john-jameson -Pbintray.key=<your API key>

-------
License
-------

::

    Copyright 2016 Pavel Stepanov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
