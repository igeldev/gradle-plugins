package igel.example.java_lib;

import org.junit.Assert;
import org.junit.Test;

public class LibraryClassTest {

    @Test
    public void test_libraryMethod() throws Exception {
        Assert.assertEquals("{\"input\":\"value\"}", LibraryClass.libraryMethod("value"));
    }

}
