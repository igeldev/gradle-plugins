package igel.example_android_application;

import android.app.Activity;
import android.content.res.Resources;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LayoutTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testMainLayout() {
        Activity activity = activityRule.getActivity();
        Resources resources = activity.getResources();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View contentView = inflater.inflate(R.layout.main, null, false);
        ImageView imageView = (ImageView) contentView.findViewById(R.id.main_image);
        TextView textView = (TextView) contentView.findViewById(R.id.main_text);

        imageView.setImageResource(android.R.drawable.ic_menu_save);
        textView.setText(String.format("%s\nNumber: %s",
                resources.getString(R.string.android_application_value),
                FlavorClass.getNumber().blockingFirst()));

        contentView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        TestCommonClass.assertDpPx(activity, 155, contentView.getMeasuredWidth());
        TestCommonClass.assertDpPx(activity, 35, contentView.getMeasuredHeight());

        contentView.layout(0, 0, contentView.getMeasuredWidth(), contentView.getMeasuredHeight());
        TestCommonClass.assertDpPx(activity, 0, contentView.getLeft());
        TestCommonClass.assertDpPx(activity, 0, contentView.getTop());
        TestCommonClass.assertDpPx(activity, 155, contentView.getRight());
        TestCommonClass.assertDpPx(activity, 35, contentView.getBottom());

        TestCommonClass.assertDpPx(activity, 0, imageView.getLeft());
        TestCommonClass.assertDpPx(activity, 2, imageView.getTop());
        TestCommonClass.assertDpPx(activity, 32, imageView.getRight());
        TestCommonClass.assertDpPx(activity, 34, imageView.getBottom());

        TestCommonClass.assertDpPx(activity, 32, textView.getLeft());
        TestCommonClass.assertDpPx(activity, 0, textView.getTop());
        TestCommonClass.assertDpPx(activity, 155, textView.getRight());
        TestCommonClass.assertDpPx(activity, 35, textView.getBottom());
    }

}
