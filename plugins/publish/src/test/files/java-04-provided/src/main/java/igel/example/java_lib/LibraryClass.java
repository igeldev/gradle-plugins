package igel.example.java_lib;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import rx.Observable;

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
    public static Observable<String> libraryMethod(String input) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("input", input);
        return Observable.just(new Gson().toJson(jsonObject));
    }

}
