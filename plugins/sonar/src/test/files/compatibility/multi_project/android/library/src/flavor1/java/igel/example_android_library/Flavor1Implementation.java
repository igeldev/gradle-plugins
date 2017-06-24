package igel.example_android_library;

import android.content.Context;

public class Flavor1Implementation implements AndroidLibraryInterface {

    @Override
    public void methodVoid(Context context, int x) {
        // do nothing
    }

    @Override
    public String methodString(Context context, int x) {
        return context.getString(R.string.flavor1_string);
    }

}
