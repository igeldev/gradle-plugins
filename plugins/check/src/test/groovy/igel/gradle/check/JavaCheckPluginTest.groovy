package igel.gradle.check

import org.junit.Test

class JavaCheckPluginTest extends BaseCheckPluginTest {

    @Test
    void testJava00Empty() {
        runPublishTest 'src/test/files/java-00-empty'
    }

    @Test
    void testJava01Simple() {
        runPublishTest 'src/test/files/java-01-simple'
    }

}
