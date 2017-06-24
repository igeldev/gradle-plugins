package igel.example_groovy

import igel.example_java.Printer

class FilePrinter implements Printer {

    final File file

    FilePrinter(File file) {
        this.file = file
    }

    @Override
    void print(String message) {
        file << (message + '\n')
    }

}
