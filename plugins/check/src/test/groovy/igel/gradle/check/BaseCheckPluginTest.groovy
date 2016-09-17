package igel.gradle.check

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner

import java.nio.file.Files

abstract class BaseCheckPluginTest {

    private static void gradle(File testDir, String... tasks) {
        List<String> arguments = ['--stacktrace']
        arguments += tasks as List<String>
        GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testDir)
                .withArguments(arguments)
                .forwardOutput()
                .build()
    }

    protected static void runPublishTest(String testPath) {
        File dir = new File(testPath)
        JsonObject testSpec = new JsonParser()
                .parse(new File(dir, 'test.json').text).asJsonObject

        println '-' * 60
        println "Test name: ${testSpec.get('name').asString}"
        println "Description:\n${testSpec.get('description').asString}\n"

        File tempDir = Files.createTempDirectory('junit').toFile()
        try {
            File testDir = new File(tempDir, "test/$dir.name")

            // copy test project sources
            FileUtils.copyDirectory(dir, testDir)

            // run gradle project
            gradle(testDir, testSpec.get('task').asString)
        } finally {
            FileUtils.deleteDirectory(tempDir)
        }
    }

}
