package igel.example.groovy_lib

import foo.Bar
import groovy.io.FileType
import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner

/**
 * Example Groovy library: class.
 */
class LibraryClass {

    /**
     * Example Groovy library: method.
     */
    static void libraryMethod() {
        println Bar.class
        println FileType.class
        println Project.class
        println GradleRunner.class
    }

}
