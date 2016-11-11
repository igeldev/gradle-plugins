package igel.gradle.check.runner

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class GradleRule implements TestRule {

    private volatile Description description

    @Override
    Statement apply(Statement base, Description description) {
        return {
            this.description = description
            base.evaluate()
            this.description = null
        }
    }

    private void checkInsideTest() {
        if (description == null) {
            throw new IllegalStateException('GradleRule is used outside test')
        }
    }

    GradleTest create(String gradleVersion, String testPath, File script) {
        checkInsideTest()
        return new GradleTest(gradleVersion, description.className, description.methodName, testPath, script)
    }

    GradleTest create(String gradleVersion, String testPath, String scriptPath) {
        checkInsideTest()
        return new GradleTest(gradleVersion, description.className, description.methodName, testPath, scriptPath)
    }

    GradleTest create(String gradleVersion, String scriptPath) {
        checkInsideTest()
        return new GradleTest(gradleVersion, description.className, description.methodName, null, scriptPath)
    }

}
