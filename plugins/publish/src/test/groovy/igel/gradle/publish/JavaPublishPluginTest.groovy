package igel.gradle.publish

import org.junit.Test

class JavaPublishPluginTest extends BasePublishPluginTest {

    @Test
    void testJava00Empty() {
        runPublishTest 'src/test/files/java-00-empty'
    }

    @Test
    void testJava01Simple() {
        runPublishTest 'src/test/files/java-01-simple'
    }

    @Test
    void testJava02VersionRange() {
        runPublishTest 'src/test/files/java-02-version-range'
    }

    @Test
    void testJava03VersionVariable() {
        runPublishTest 'src/test/files/java-03-version-variable'
    }

    @Test
    void testJava04Provided() {
        runPublishTest 'src/test/files/java-04-provided'
    }

}
