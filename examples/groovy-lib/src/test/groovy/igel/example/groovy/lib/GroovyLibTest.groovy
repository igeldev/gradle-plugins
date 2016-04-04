package igel.example.groovy.lib

import org.junit.Assert
import org.junit.Test

class GroovyLibTest {

    @Test
    void test() throws Exception {
        Assert.assertEquals('value', GroovyLib.getValue())
    }

}
