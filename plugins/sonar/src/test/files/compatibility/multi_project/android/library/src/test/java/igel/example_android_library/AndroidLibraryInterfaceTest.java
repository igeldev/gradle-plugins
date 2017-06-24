package igel.example_android_library;

import org.junit.Test;

public class AndroidLibraryInterfaceTest {

    @Test
    public void simpleCheck() {
        AndroidLibraryInterface instance = InterfaceProvider.createInstance();
        instance.methodVoid(null, 0);
    }

}
