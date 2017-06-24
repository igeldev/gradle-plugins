package igel.example_android_application;

import igel.example_android_library.AndroidLibraryInterface;
import igel.example_android_library.Flavor1Implementation;

public class Flavor1Dependency {

    public AndroidLibraryInterface getImplementation() {
        return new Flavor1Implementation();
    }

}
