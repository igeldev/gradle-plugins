package igel.example_groovy

import com.google.common.base.Strings

class ClassGroovy {

    static String someMethod(String str1, String str2) {
        return Strings.commonPrefix(AddClassGroovy.add(str1, str2), str1)
    }

    static String getGenValue() {
        return GenClassGroovy.someValue
    }

    static String getJavaFileInGroovyValue() {
        return JavaFileInGroovy.someValue
    }

    static String getJavaFileInJavaValue() {
        return JavaFileInJava.someValue
    }

}
