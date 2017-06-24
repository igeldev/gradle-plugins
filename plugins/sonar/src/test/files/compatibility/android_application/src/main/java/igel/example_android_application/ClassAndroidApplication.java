package igel.example_android_application;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class ClassAndroidApplication {

    public static <T> T someMethod(String str, Function<String, T> function) {
        return Observable.just(str).map(function).blockingFirst();
    }

}
