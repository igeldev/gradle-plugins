package igel.gradle.check

import org.junit.Test

class JavaPluginTest extends BasePluginTest {

    @Test
    void testJava00Empty() {
        runPublishTest 'src/test/files/java-00-empty'
    }

    @Test
    void testJava01Simple() {
        runPublishTest 'src/test/files/java-01-simple'
    }

}
