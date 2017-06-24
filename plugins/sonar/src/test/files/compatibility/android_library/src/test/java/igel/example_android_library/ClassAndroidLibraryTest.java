package igel.example_android_library;

import android.graphics.Bitmap;
import com.orhanobut.logger.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.util.Locale;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        shadows = {ClassAndroidLibraryTest.LoggerShadow.class})
public class ClassAndroidLibraryTest {

    @Implements(Logger.class)
    public static class LoggerShadow {

        private static final StringBuilder LOG_BUILDER = new StringBuilder();

        static synchronized void cleanLog() {
            LOG_BUILDER.delete(0, LOG_BUILDER.length());
        }

        static synchronized String getLog() {
            return LOG_BUILDER.toString();
        }

        @Implementation
        public static synchronized void i(String format, Object... args) {
            LOG_BUILDER.append(String.format(format, args));
        }

    }

    @Before
    public void setUp() {
        LoggerShadow.cleanLog();
    }

    @Test
    public void someMethodTest() {
        Assert.assertEquals("[bla bla bla]", ClassAndroidLibrary.someMethod("bla bla bla"));
        Assert.assertEquals("ClassAndroidLibrary::someMethod(str=bla bla bla)", LoggerShadow.getLog());
    }

    @Test
    public void resourceTest() {
        Assert.assertEquals("Android Library",
                RuntimeEnvironment.application.getString(R.string.android_library_value));
    }

    @Test
    public void testCommonClassTest() {
        int s = 25;
        Bitmap bitmap = TestCommonClass.draw(
                RuntimeEnvironment.application, R.drawable.image, s, s);
        Assert.assertEquals(s, bitmap.getWidth());
        Assert.assertEquals(s, bitmap.getHeight());
    }

    @Test
    public void testFlavorValue() {
        Assert.assertEquals(
                String.format(Locale.US, "Value of %s", FlavorClass.getName()),
                BuildConfig.FLAVOR_VALUE);
    }

}
