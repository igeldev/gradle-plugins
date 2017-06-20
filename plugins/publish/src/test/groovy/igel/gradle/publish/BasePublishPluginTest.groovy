package igel.gradle.publish

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import igel.gradle.test.runner.GradleRule
import igel.gradle.test.runner.GradleTestRunner
import org.junit.Assert
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile

abstract class BasePublishPluginTest {

    @Rule
    public final GradleRule gradle = new GradleRule(
            new File('src/test/files/'),
            new File('build/gradle-tests/'),
            '2.13',
            ['--stacktrace'])

    @Rule
    public final TemporaryFolder tempDir = new TemporaryFolder()

    private static String formatXml(String xmlText) {
        Node node = new XmlParser().parseText(xmlText)

        StringWriter stringWriter = new StringWriter()
        PrintWriter printWriter = new PrintWriter(stringWriter)
        XmlNodePrinter printer = new XmlNodePrinter(printWriter)
        printer.setPreserveWhitespace(true)
        printer.print(node)

        return stringWriter.toString()
    }

    private static void checkRepo(File repoDir, JsonArray repoSpec) {
        // get repo files
        List<File> repoFiles = []
        repoDir.eachFileRecurse { repoFiles << it }
        repoFiles = repoFiles.findAll {
            it.file && !it.name.endsWith('md5') && !it.name.endsWith('sha1')
        }

        // get repo paths
        Path repoPath = Paths.get(repoDir.absolutePath)
        List<String> repoPaths = repoFiles.collect {
            repoPath.relativize(Paths.get(it.absolutePath)).toString()
        }

        // check repo files
        Assert.assertEquals('Publication files are wrong',
                new HashSet<String>(repoSpec
                        .collect { it.asJsonObject.get('path').asString }),
                new HashSet<String>(repoPaths))

        // check repo files content
        repoSpec.each { JsonElement repoFileElement ->
            JsonObject repoFile = repoFileElement.asJsonObject
            File file = new File(repoDir, repoFile.get('path').asString)
            switch (repoFile.get('type').asString) {
                case 'raw':
                    // do not check content of raw files
                    break
                case 'xml':
                    Assert.assertEquals("Wrong content of $file",
                            formatXml(repoFile.get('text').asJsonArray
                                    .collect { it.asString }.join('\n')),
                            formatXml(file.text))
                    break
                case 'zip':
                    Set<String> entries = []
                    new ZipFile(file).withCloseable {
                        it.entries().each {
                            if (!it.directory) {
                                entries << it.name
                            }
                        }
                    }
                    Assert.assertEquals("Wrong content of $file",
                            new HashSet<String>(repoFile.get('files').asJsonArray
                                    .collect { it.asString }),
                            entries)
                    break
            }
        }
    }

    protected void runPublishTest(String testPath) {
        File testJsonFile = new File("src/test/files/$testPath/test.json")
        JsonObject testSpec = new JsonParser().parse(testJsonFile.text).asJsonObject

        println '-' * 60
        println "Test name: ${testSpec.get('name').asString}"
        println "Description:\n${testSpec.get('description').asString}\n"

        // run gradle project
        File repoDir = tempDir.newFolder(testPath)
        GradleTestRunner runner = gradle.create(testPath, 'build.gradle', testPath,
                ['-Prepo=' + repoDir.absolutePath])
        runner.buildSuccess([testSpec.get('task').asString])

        // check repo content
        checkRepo(repoDir, testSpec.get('repo').asJsonArray)
    }

}
