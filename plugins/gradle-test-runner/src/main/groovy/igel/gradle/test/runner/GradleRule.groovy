package igel.gradle.test.runner

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class GradleRule implements TestRule {

    private final File testFilesRoot
    private final File testBuildRoot
    private final String gradleVersion
    private final List<String> commonFlags

    private volatile Description description

    GradleRule(File testFilesRoot, File testBuildRoot, String gradleVersion, List<String> commonFlags = []) {
        this.testFilesRoot = testFilesRoot
        this.testBuildRoot = testBuildRoot
        this.gradleVersion = gradleVersion
        this.commonFlags = Collections.unmodifiableList(new ArrayList<>(commonFlags))
    }

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
            throw new IllegalStateException("${GradleRule.class.simpleName} is used outside test")
        }
    }

    GradleTestRunner create(String sourcePath, String scriptPath, String testPath, List<String> flags = []) {
        checkInsideTest()
        return new GradleTestRunner(
                new File(testFilesRoot, sourcePath), scriptPath, testBuildRoot,
                description.className, description.methodName, testPath,
                gradleVersion, commonFlags + flags)
    }

    GradleTestRunner create(String sourcePath, String scriptPath, List<String> flags = []) {
        return create(sourcePath, scriptPath, null, flags)
    }

}
