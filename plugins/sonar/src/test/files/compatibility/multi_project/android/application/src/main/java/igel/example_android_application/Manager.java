package igel.example_android_application;

import android.content.Context;

public class Manager {

    private final Context context;

    public Manager(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public String getName() {
        return context.getString(R.string.mod_name);
    }

    public boolean getConfigBoolean() {
        return BuildConfig.testBooleanValue;
    }

    public String getConfigName() {
        return BuildConfig.MOD;
    }

}
