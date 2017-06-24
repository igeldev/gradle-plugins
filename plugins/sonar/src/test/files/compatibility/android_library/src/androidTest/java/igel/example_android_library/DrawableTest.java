package igel.example_android_library;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawableTest {

    @Rule
    public ActivityTestRule<Activity> activityRule = new ActivityTestRule<>(Activity.class);

    @Test
    public void drawableTest() {
        int s = 100;
        Bitmap bitmap = TestCommonClass.draw(activityRule.getActivity(), R.drawable.image, s, s);

        Assert.assertEquals(Color.RED, bitmap.getPixel(0, 0));
        Assert.assertEquals(Color.RED, bitmap.getPixel(s / 2, 0));
        Assert.assertEquals(Color.RED, bitmap.getPixel(0, s / 2));
        Assert.assertEquals(Color.GREEN, bitmap.getPixel(s / 2, s / 2));
    }

}
