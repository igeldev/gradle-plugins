package igel.example_android_library;

import android.support.test.rule.ActivityTestRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

public class GetStringTest {

    @Rule
    public final ActivityTestRule<LibraryActivity> activityTestRule = new ActivityTestRule<>(LibraryActivity.class);

    @Test
    public void checkString() throws TimeoutException {
        LibraryActivity activity = activityTestRule.getActivity();
        String value = InterfaceProvider.createInstance().methodString(activity, 0);
        if (BuildConfig.FLAVOR_NUMBER == 1) {
            Assert.assertEquals("Flavor 1 string", value);
        } else {
            Assert.assertEquals(null, value);
        }
    }

}
