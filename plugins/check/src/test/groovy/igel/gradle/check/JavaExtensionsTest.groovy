package igel.gradle.check

import igel.gradle.check.runner.GradleRule
import igel.gradle.check.runner.GradleTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class JavaExtensionsTest {

    @Rule
    public final GradleRule gradleRule = new GradleRule()

    // todo нужно сделать проверки на наличие extensions и доступные методы

    @Test
    void config_empty() {
        GradleTest gradleTest = gradleRule.create('2.13', 'java-extensions/config_empty.gradle')
        gradleTest.buildSuccess('verify', ['verify'])
    }

    @Test
    void config_nothing() {
        GradleTest gradleTest = gradleRule.create('2.13', 'java-extensions/config_nothing.gradle')
        gradleTest.buildSuccess('verify', ['verify'])
    }

    @Test
    void method_checkstyle_empty() {
        GradleTest gradleTest = gradleRule.create('2.13', 'java-extensions/method_checkstyle_empty.gradle')
        gradleTest.buildSuccess('verify', ['verify'])
    }

    @Test
    void method_findbugs_empty() {
        GradleTest gradleTest = gradleRule.create('2.13', 'java-extensions/method_findbugs_empty.gradle')
        gradleTest.buildSuccess('verify', ['verify'])
    }

    @Test
    void method_pmd_empty() {
        GradleTest gradleTest = gradleRule.create('2.13', 'java-extensions/method_pmd_empty.gradle')
        gradleTest.buildSuccess('verify', ['verify'])
    }

    @Test
    void method_unknown() {
        GradleTest gradleTest = gradleRule.create('2.13', 'java-extensions/method_unknown.gradle')
        GradleTest.Result result = gradleTest.buildFailure('evaluate', [])
        Assert.assertTrue(result.outputFile.text.contains(
                'Unknown check method \'Unknown\'. Use one of [Checkstyle, FindBugs, PMD]'))
    }

}
