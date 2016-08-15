package igel.gradle.check

import org.junit.Test

class JavaCheckPluginTest extends BaseCheckPluginTest {

    @Test
    void testJava00Empty() {
        runPublishTest 'src/test/files/java-00-empty'
    }

}
