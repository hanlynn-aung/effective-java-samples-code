package chapter2.item8.good;

public final class GoodAutoCloseableResource implements AutoCloseable {
    private boolean closed;

    public void use() {
        if (closed) throw new IllegalStateException("resource is closed");
    }

    @Override
    public void close() {
        closed = true;
    }
}
