package igel.gradle.check

import org.junit.Test

class GroovyCheckPluginTest extends BaseCheckPluginTest {

    @Test
    void testGroovy00Empty() {
        runPublishTest 'src/test/files/groovy-00-empty'
    }

}
