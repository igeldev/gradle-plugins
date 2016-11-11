package igel.gradle.check.runner

class WrapperWriter extends Writer {

    private final List<Writer> writers

    WrapperWriter(List<Writer> writers) {
        this.writers = Collections.unmodifiableList(new ArrayList<>(writers))
    }

    WrapperWriter(Writer... writers) {
        this(Arrays.asList(writers))
    }

    private void all(Closure closure) {
        List<Exception> exceptions = []
        writers.each {
            try {
                closure.call(it)
            } catch (Exception e) {
                exceptions.add(e)
            }
        }
        if (exceptions) {
            throw new RuntimeException(exceptions[0])
        }
    }

    @Override
    void write(char[] buffer, int offset, int length) {
        all { it.write(buffer, offset, length) }
    }

    @Override
    void flush() {
        all { it.flush() }
    }

    @Override
    void close() {
        all { it.close() }
    }

}
