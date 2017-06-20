package igel.gradle.publish

import org.junit.Test

class AndroidPublishPluginTest extends BasePublishPluginTest {

    @Test
    void testAndroid00Empty() {
        runPublishTest 'android-00-empty'
    }

    @Test
    void testAndroid01Simple() {
        runPublishTest 'android-01-simple'
    }

    @Test
    void testAndroid02Aidl() {
        runPublishTest 'android-02-aidl'
    }

    @Test
    void testAndroid03Resources() {
        runPublishTest 'android-03-resources'
    }

    @Test
    void testAndroid04LibraryVariant() {
        runPublishTest 'android-04-library-variant'
    }

    @Test
    void testAndroid05VersionRange() {
        runPublishTest 'android-05-version-range'
    }

    @Test
    void testAndroid06Provided() {
        runPublishTest 'android-06-provided'
    }

}
