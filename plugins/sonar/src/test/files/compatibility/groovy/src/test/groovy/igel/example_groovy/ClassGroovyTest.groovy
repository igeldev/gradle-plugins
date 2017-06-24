package igel.example_groovy

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class ClassGroovyTest {

    @Test
    void someTest() {
        String str1 = new Gson().toJson([1, 2, 3])
        String str2 = new Gson().toJson([3, 2, 1])
        Assert.assertEquals('[1,2,3]', ClassGroovy.someMethod(str1, str2))
    }

}
