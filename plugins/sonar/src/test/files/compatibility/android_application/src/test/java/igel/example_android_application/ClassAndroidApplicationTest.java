package igel.example_android_application;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import io.reactivex.functions.Function;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        shadows = {ClassAndroidApplicationTest.ResourcesShadow.class})
public class ClassAndroidApplicationTest {

    @Implements(Resources.class)
    public static class ResourcesShadow {

        private static int dpi = DisplayMetrics.DENSITY_MEDIUM;

        public static void setDpiMedium() {
            ResourcesShadow.dpi = DisplayMetrics.DENSITY_MEDIUM;
        }

        public static void setDpiXHigh() {
            ResourcesShadow.dpi = DisplayMetrics.DENSITY_XHIGH;
        }

        @Implementation
        public DisplayMetrics getDisplayMetrics() {
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.density = dpi / (float) DisplayMetrics.DENSITY_MEDIUM;
            metrics.scaledDensity = metrics.density;
            metrics.densityDpi = dpi;
            metrics.xdpi = metrics.ydpi = metrics.densityDpi;
            return metrics;
        }

    }

    @Test
    public void someMethodTest() {
        Assert.assertEquals("--- bla bla bla ---",
                ClassAndroidApplication.someMethod("bla bla bla", new Function<String, String>() {
                    @Override
                    public String apply(String str) throws Exception {
                        return String.format("--- %s ---", str);
                    }
                }));
    }

    @Test
    public void resourceTest() {
        Assert.assertEquals("Android Application",
                RuntimeEnvironment.application.getString(R.string.android_application_value));
    }

    @Test
    public void testCommonClassTest() {
        ResourcesShadow.setDpiXHigh();
        TestCommonClass.assertDpPx(RuntimeEnvironment.application,
                0, -2);
        TestCommonClass.assertDpPx(RuntimeEnvironment.application,
                0, -1);
        TestCommonClass.assertDpPx(RuntimeEnvironment.application,
                0, 0);
        TestCommonClass.assertDpPx(RuntimeEnvironment.application,
                0, +1);
        ResourcesShadow.setDpiMedium();
        try {
            TestCommonClass.assertDpPx(RuntimeEnvironment.application,
                    0, +2);
            Assert.fail();
        } catch (AssertionError ignored) {
        }
    }

    @Test
    public void testFlavorValue() {
        Assert.assertEquals(
                String.format("Value of Flavor #%s", FlavorClass.getNumber().blockingFirst()),
                BuildConfig.FLAVOR_VALUE);
    }

}
