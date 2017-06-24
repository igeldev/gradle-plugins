package igel.example_android_application;

import io.reactivex.Observable;

public class FlavorClass {

    public static Observable<Integer> getNumber() {
        return Observable.just(1);
    }

}
