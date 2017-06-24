package igel.example_android_library;

import com.orhanobut.logger.Logger;

public class ClassAndroidLibrary {

    public static String someMethod(String str) {
        Logger.i("ClassAndroidLibrary::someMethod(str=%s)", str);

        return String.format("[%s]", str);
    }

}
