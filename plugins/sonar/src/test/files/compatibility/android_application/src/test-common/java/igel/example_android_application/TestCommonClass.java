package igel.example_android_application;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import org.junit.Assert;

public class TestCommonClass {

    public static void assertDpPx(Context context, int expectedDp, int actualPx) {
        Resources resources = context.getResources();
        float deltaPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, resources.getDisplayMetrics());
        float expectedPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                expectedDp, resources.getDisplayMetrics());

        Assert.assertTrue(
                String.format(
                        "expected size is %s, actual size is %s, delta is %s",
                        expectedPx, actualPx, deltaPx),
                Math.abs(expectedPx - actualPx) <= deltaPx);
    }

}
