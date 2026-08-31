package chapter2.item8.bad;

public class BadFinalizerResource {
    public void close() {
        // Release the resource.
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
