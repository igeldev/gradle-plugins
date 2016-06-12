package igel.example.android_lib;

import android.content.Context;

/**
 * Example Android library: class.
 */
public final class LibraryClass {

    /**
     * Returns resource value.
     *
     * @param context context to use.
     * @return resource value.
     */
    public static String getResourceValue(Context context) {
        return context.getString(R.string.android_lib_value);
    }

}
