package igel.example.groovy_lib

import org.junit.Assert
import org.junit.Test

class LibraryClassTest {

    @Test
    void test_libraryMethod() throws Exception {
        Assert.assertEquals('{"input":"value"}', LibraryClass.libraryMethod('value'))
    }

}
