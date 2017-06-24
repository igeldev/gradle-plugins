package igel.example_android_application;

import igel.example_android_library.AndroidLibraryInterface;
import igel.example_android_library.Flavor2Implementation;

public class Flavor2Dependency {

    public AndroidLibraryInterface getImplementation() {
        return new Flavor2Implementation();
    }

}
