package igel.example.java_lib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Example Java library: class.
 */
public final class LibraryClass {

    /**
     * Example Java library: method.
     *
     * @param input input value.
     * @return output value.
     */
    public static String libraryMethod(String input) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("input", input);
        return new Gson().toJson(jsonObject);
    }

}
