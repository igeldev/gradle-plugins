package igel.example.android_lib;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Example Android library: class.
 */
public final class LibraryClass {

    /**
     * Example Android library: method.
     *
     * @param input input value.
     * @return output value.
     */
    public static String libraryMethod(String input) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("input", input);
        return new Gson().toJson(jsonObject);
    }

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
