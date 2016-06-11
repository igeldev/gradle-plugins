package igel.example.groovy_lib

import com.google.gson.Gson
import com.google.gson.JsonObject

/**
 * Example Groovy library: class.
 */
class LibraryClass {

    /**
     * Example Groovy library: method.
     *
     * @param input input value.
     * @return output value.
     */
    static String libraryMethod(String input) {
        JsonObject jsonObject = new JsonObject()
        jsonObject.addProperty('input', input)
        return new Gson().toJson(jsonObject)
    }

}
