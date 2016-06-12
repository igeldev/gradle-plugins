package igel.gradle.publish

import org.junit.Test

class GroovyPublishPluginTest extends BasePublishPluginTest {

    @Test
    void testGroovy00Empty() {
        runPublishTest 'src/test/files/groovy-00-empty'
    }

    @Test
    void testGroovy01Simple() {
        runPublishTest 'src/test/files/groovy-01-simple'
    }

    @Test
    void testGroovy02GradlePlugin() {
        runPublishTest 'src/test/files/groovy-02-gradle-plugin'
    }

}
