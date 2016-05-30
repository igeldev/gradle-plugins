package igel.example.android_lib;

import android.support.v4.util.Pair;

import com.google.gson.JsonParser;

import junit.framework.Assert;

import org.junit.Test;

public class LibraryClassJUnitTest {

    @Test
    public void test_libraryMethod() throws Exception {
        Assert.assertEquals(
                new Pair<>("value", new JsonParser().parse("{\"input\":\"value\"}")),
                LibraryClass.libraryMethod("value"));
    }

}
