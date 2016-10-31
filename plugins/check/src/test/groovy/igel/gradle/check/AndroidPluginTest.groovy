package igel.gradle.check

import org.junit.Test

class AndroidPluginTest extends BasePluginTest {

    @Test
    void testAndroid00Empty() {
        runPublishTest 'src/test/files/android-00-empty'
    }

    @Test
    void testAndroid01Simple() {
        runPublishTest 'src/test/files/android-01-simple'
    }

}
