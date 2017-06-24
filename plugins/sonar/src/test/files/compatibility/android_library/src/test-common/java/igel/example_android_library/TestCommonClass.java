package igel.example_android_library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class TestCommonClass {

    public static Bitmap draw(Context context, int resourceId, int width, int height) {
        Drawable drawable = context.getResources().getDrawable(resourceId);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        return bitmap;
    }

}
