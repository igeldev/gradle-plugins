package igel.example_java;

import com.google.common.base.Strings;

public class ClassJava {

    public static String someMethod(String str1, String str2) {
        return Strings.commonPrefix(AddClassJava.add(str1, str2), str1);
    }

    public static String getGenValue() {
        return GenClassJava.someValue;
    }

}
