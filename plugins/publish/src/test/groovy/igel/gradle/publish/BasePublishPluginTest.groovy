package igel.gradle.publish

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.apache.commons.io.FileUtils
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile

abstract class BasePublishPluginTest {

    private static String formatXml(String xmlText) {
        Node node = new XmlParser().parseText(xmlText)

        StringWriter stringWriter = new StringWriter()
        PrintWriter printWriter = new PrintWriter(stringWriter)
        XmlNodePrinter printer = new XmlNodePrinter(printWriter)
        printer.setPreserveWhitespace(true)
        printer.print(node)

        return stringWriter.toString()
    }

    private static void gradle(File testDir, File repoDir, String task) {
        GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testDir)
                .withArguments(['-Prepo=' + repoDir.absolutePath, task])
                .forwardOutput()
                .build()
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
            File repoDir = new File(tempDir, 'repo')

            // copy test project sources
            FileUtils.copyDirectory(dir, testDir)

            // run gradle project
            gradle(testDir, repoDir, testSpec.get('task').asString)

            // check repo content
            checkRepo(repoDir, testSpec.get('repo').asJsonArray)
        } finally {
            FileUtils.deleteDirectory(tempDir)
        }
    }

}
