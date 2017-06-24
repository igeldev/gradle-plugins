package igel.example_java;

import org.junit.Assert;
import org.junit.Test;

public class LoggerTest {

    private static class TestPrinter implements Printer {

        private final StringBuilder builder = new StringBuilder();

        @Override
        public void print(String message) {
            builder.append(message).append('\n');
        }

        public String getLog() {
            return builder.toString();
        }

    }

    private static class TestBean {

        public Integer integerValue;
        public String stringValue;

        public TestBean(Integer integerValue, String stringValue) {
            this.integerValue = integerValue;
            this.stringValue = stringValue;
        }

    }

    @Test
    public void testSimple() {
        TestPrinter printer = new TestPrinter();
        Logger logger = new Logger(new GsonConverter(), printer);
        logger.log(new TestBean(123, "abc"));
        Assert.assertEquals(
                "{\"integerValue\":123,\"stringValue\":\"abc\"}\n",
                printer.getLog());
    }

}
