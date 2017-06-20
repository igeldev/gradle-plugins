package igel.gradle.publish

import org.junit.Test

class JavaPublishPluginTest extends BasePublishPluginTest {

    @Test
    void testJava00Empty() {
        runPublishTest 'java-00-empty'
    }

    @Test
    void testJava01Simple() {
        runPublishTest 'java-01-simple'
    }

    @Test
    void testJava02VersionRange() {
        runPublishTest 'java-02-version-range'
    }

    @Test
    void testJava03VersionVariable() {
        runPublishTest 'java-03-version-variable'
    }

    @Test
    void testJava04Provided() {
        runPublishTest 'java-04-provided'
    }

}
