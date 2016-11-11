package igel.gradle.check.runner

class ConsoleWriter extends Writer {

    private final PipedWriter pipedWriter
    private final PipedReader pipedReader
    private final Thread thread

    ConsoleWriter(String prefix) {
        this.pipedWriter = new PipedWriter()
        this.pipedReader = new PipedReader(pipedWriter)
        this.thread = Thread.start {
            while (!Thread.currentThread().isInterrupted()) {
                String line = pipedReader.readLine()
                if (line == null) {
                    break
                }
                System.out.println("$prefix$line")
            }
        }
    }

    @Override
    void write(char[] buffer, int offset, int length) throws IOException {
        pipedWriter.write(buffer, offset, length)
    }

    @Override
    void flush() throws IOException {
        pipedWriter.flush()
    }

    @Override
    void close() throws IOException {
        pipedWriter.close()
        thread.interrupt()
    }

}
