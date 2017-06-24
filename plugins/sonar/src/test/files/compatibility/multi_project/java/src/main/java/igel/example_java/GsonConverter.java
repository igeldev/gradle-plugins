package igel.example_java;

import com.google.gson.Gson;

public class GsonConverter implements Converter {

    private final Gson gson;

    public GsonConverter() {
        this.gson = new Gson();
    }

    public GsonConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String convert(Object value) {
        return gson.toJson(value);
    }

}
