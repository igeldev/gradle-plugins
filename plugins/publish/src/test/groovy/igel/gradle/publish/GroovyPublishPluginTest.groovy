package igel.gradle.publish

import org.junit.Test

class GroovyPublishPluginTest extends BasePublishPluginTest {

    @Test
    void testGroovy00Empty() {
        runPublishTest 'groovy-00-empty'
    }

    @Test
    void testGroovy01Simple() {
        runPublishTest 'groovy-01-simple'
    }

    @Test
    void testGroovy02GradlePlugin() {
        runPublishTest 'groovy-02-gradle-plugin'
    }

    @Test
    void testGroovy03VersionRange() {
        runPublishTest 'groovy-03-version-range'
    }

    @Test
    void testGroovy04Provided() {
        runPublishTest 'groovy-04-provided'
    }

    @Test
    void testGroovy05LocalDeps() {
        runPublishTest 'groovy-05-local-deps'
    }

}
