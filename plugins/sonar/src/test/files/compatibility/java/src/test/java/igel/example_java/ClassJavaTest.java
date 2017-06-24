package igel.example_java;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

public class ClassJavaTest {

    @Test
    public void someTest() {
        String str1 = new Gson().toJson(new int[]{1, 2, 3});
        String str2 = new Gson().toJson(new int[]{3, 2, 1});
        Assert.assertEquals("[1,2,3]", ClassJava.someMethod(str1, str2));
    }

}
