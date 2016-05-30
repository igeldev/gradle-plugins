package igel.example.android_lib;

import android.content.Context;
import android.support.v4.util.Pair;

import com.google.gson.JsonElement;
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
    public static Pair<String, JsonElement> libraryMethod(String input) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("input", input);
        return new Pair<String, JsonElement>(input, jsonObject);
    }

    /**
     * Returns {@code null}.
     *
     * @return {@code null}.
     */
    public static ILibraryInterface getInterface() {
        return null;
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
