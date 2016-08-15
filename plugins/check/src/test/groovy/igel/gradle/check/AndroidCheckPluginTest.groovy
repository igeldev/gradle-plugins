package igel.gradle.check

import org.junit.Test

class AndroidCheckPluginTest extends BaseCheckPluginTest {

    @Test
    void testAndroid00Empty() {
        runPublishTest 'src/test/files/android-00-empty'
    }

}
