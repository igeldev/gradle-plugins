package igel.gradle.publish

import org.junit.Test

class AndroidPublishPluginTest extends BasePublishPluginTest {

    @Test
    void testAndroid00Empty() {
        runPublishTest 'src/test/files/android-00-empty'
    }

    @Test
    void testAndroid01Simple() {
        runPublishTest 'src/test/files/android-01-simple'
    }

    @Test
    void testAndroid02Aidl() {
        runPublishTest 'src/test/files/android-02-aidl'
    }

    @Test
    void testAndroid03Resources() {
        runPublishTest 'src/test/files/android-03-resources'
    }

}
