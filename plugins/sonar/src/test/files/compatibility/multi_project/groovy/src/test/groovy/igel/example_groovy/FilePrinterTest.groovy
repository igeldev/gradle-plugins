package igel.example_groovy

import igel.example_java.GsonConverter
import igel.example_java.Logger
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FilePrinterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test
    void testPrinter() {
        FilePrinter printer = new FilePrinter(temporaryFolder.newFile())
        Logger logger = new Logger(new GsonConverter(), printer)
        logger.log('a')
        logger.log('b')
        logger.log('c')

        Assert.assertEquals("\"a\"\n\"b\"\n\"c\"\n", printer.file.text)
    }

}
