package igel.gradle.check.runner

import org.apache.commons.io.FileUtils
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

class GradleTest {

    private static final File TEST_FILES_ROOT = new File('src/test/files')
    private static final File TEST_BUILD_ROOT = new File('build/tests')

    class Result {

        final String buildName
        final BuildResult buildResult
        final File stdoutFile
        final File stderrFile
        final File outputFile

        private Result(String buildName, BuildResult buildResult, File stdoutFile, File stderrFile, File outputFile) {
            this.buildName = buildName
            this.buildResult = buildResult
            this.stdoutFile = stdoutFile
            this.stderrFile = stderrFile
            this.outputFile = outputFile
        }

        GradleTest getTest() {
            return GradleTest.this
        }

    }

    final String gradleVersion
    final String testClass
    final String testMethod
    final String testPath
    final File script

    private final File scriptCopy
    private final File sourceCopy

    GradleTest(String gradleVersion, String testClass, String testMethod, String testPath, String scriptPath) {
        this(gradleVersion, testClass, testMethod, testPath, new File(TEST_FILES_ROOT, scriptPath))
    }

    GradleTest(String gradleVersion, String testClass, String testMethod, String testPath, File script) {
        this.gradleVersion = gradleVersion
        this.testClass = testClass
        this.testMethod = testMethod
        this.testPath = testPath
        this.script = script

        // copy source
        String pathCopy = "$testClass/$testMethod/${testPath ?: ''}"
        this.sourceCopy = new File(TEST_BUILD_ROOT, pathCopy)
        this.scriptCopy = new File(sourceCopy, script.name)
        FileUtils.deleteDirectory(sourceCopy)
        FileUtils.copyDirectory(script.parentFile, sourceCopy)
    }

    private Result build(String buildName, List<String> args, Closure<BuildResult> buildClosure) {
        String prefixConsole = "$testClass:$testMethod" +
                "${testPath ? "|$testPath" : ''}" +
                "${buildName ? "|$buildName" : ''}" +
                "\$ "
        String prefixLogFile = buildName ? "$buildName-" : ''

        // prepare output writers
        File stdoutFile = new File(sourceCopy, "${prefixLogFile}stdout.txt")
        File stderrFile = new File(sourceCopy, "${prefixLogFile}stderr.txt")
        File outputFile = new File(sourceCopy, "${prefixLogFile}output.txt")
        Writer consoleWriter = new ConsoleWriter(prefixConsole)
        Writer outputWriter = outputFile.newWriter()
        Writer stdoutWriter = stdoutFile.newWriter()
        Writer stderrWriter = stderrFile.newWriter()
        Writer stdoutWrapperWriter = new WrapperWriter(consoleWriter, stdoutWriter, outputWriter)
        Writer stderrWrapperWriter = new WrapperWriter(consoleWriter, stderrWriter, outputWriter)

        try {
            GradleRunner runner = GradleRunner.create()
                    .forwardStdOutput(stdoutWrapperWriter)
                    .forwardStdError(stderrWrapperWriter)
                    .withPluginClasspath()
                    .withGradleVersion(gradleVersion)
                    .withProjectDir(sourceCopy)
                    .withArguments(['--build-file', scriptCopy.absolutePath] + args)
            BuildResult buildResult = buildClosure.call(runner)
            return new Result(buildName, buildResult, stdoutFile, stderrFile, outputFile)
        } finally {
            stdoutWrapperWriter.flush()
            stderrWrapperWriter.flush()
            [consoleWriter, outputWriter, stdoutWriter, stderrWriter].each {
                DefaultGroovyMethodsSupport.closeQuietly(it)
            }
        }

    }

    Result buildSuccess(String buildName, List<String> args) {
        return build(buildName, args) { GradleRunner runner -> runner.build() }
    }

    Result buildFailure(String buildName, List<String> args) {
        return build(buildName, args) { GradleRunner runner -> runner.buildAndFail() }
    }

    Result buildSuccess(List<String> args) {
        return buildSuccess(null, args)
    }

    Result buildFailure(List<String> args) {
        return buildFailure(null, args)
    }

}
