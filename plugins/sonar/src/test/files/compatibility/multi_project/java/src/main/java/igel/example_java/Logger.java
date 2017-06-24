package igel.example_java;

public class Logger {

    private final Converter converter;
    private final Printer printer;

    public Logger(Converter converter, Printer printer) {
        this.converter = converter;
        this.printer = printer;
    }

    public void log(Object value) {
        printer.print(converter.convert(value));
    }

}
