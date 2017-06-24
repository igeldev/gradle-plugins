package igel.example_android_library;

public class InterfaceProvider {

    public static AndroidLibraryInterface createInstance() {
        return new Flavor1Implementation();
    }

}
