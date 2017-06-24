package igel.gradle.sonar

import igel.gradle.test.runner.GradleRule
import igel.gradle.test.runner.GradleTestRunner
import org.junit.Rule
import org.junit.Test

class CompatibilityTest {

    @Rule
    public final GradleRule gradle = new GradleRule(
            new File('src/test/files/'),
            new File('build/gradle-tests/'),
            '3.3',
            ['--stacktrace',
             '-Pgradle.plugin.version=2.3.1',
             '-Pandroid.sdk.version=25',
             '-Pandroid.min.sdk.version=14',
             '-Pandroid.build.tools.version=25.0.2',
            ])

    @Test
    void test_android_application() {
        GradleTestRunner runner = gradle.create('compatibility/android_application', 'build.gradle')
        runner.buildSuccess(['clean', 'build'])
    }

    @Test
    void test_android_library() {
        GradleTestRunner runner = gradle.create('compatibility/android_library', 'build.gradle')
        runner.buildSuccess(['clean', 'build'])
    }

    @Test
    void test_groovy() {
        GradleTestRunner runner = gradle.create('compatibility/groovy', 'build.gradle')
        runner.buildSuccess(['clean', 'build'])
    }

    @Test
    void test_java() {
        GradleTestRunner runner = gradle.create('compatibility/java', 'build.gradle')
        runner.buildSuccess(['clean', 'build'])
    }

    @Test
    void test_multi_project() {
        GradleTestRunner runner = gradle.create('compatibility/multi_project', 'build.gradle')
        runner.buildSuccess(['clean', 'build'])
    }

}
