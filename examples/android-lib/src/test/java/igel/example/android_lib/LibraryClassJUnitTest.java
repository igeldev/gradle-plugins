package igel.example.android_lib;

import junit.framework.Assert;

import org.junit.Test;

public class LibraryClassJUnitTest {

    @Test
    public void test_libraryMethod() throws Exception {
        Assert.assertEquals("{\"input\":\"value\"}", LibraryClass.libraryMethod("value"));
    }

}
