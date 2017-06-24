package igel.example_android_application;

import android.support.test.rule.ActivityTestRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class ActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void managerTest() {
        Manager manager = new Manager(activityTestRule.getActivity());

        Assert.assertFalse(manager.getConfigBoolean());
        Assert.assertEquals(manager.getConfigName(), manager.getName());
    }

    @Test
    public void labelTest() {
        MainActivity activity = activityTestRule.getActivity();
        Assert.assertEquals(activity.getString(R.string.application_name), activity.getTitle());
    }

}
