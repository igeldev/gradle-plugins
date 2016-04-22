package igel.example.android_lib;

import android.test.AndroidTestCase;

public class LibraryClassAndroidTest extends AndroidTestCase {

    public void test_getValue() throws Exception {
        assertEquals("Example Android library: resource", LibraryClass.getResourceValue(getContext()));
    }

}
