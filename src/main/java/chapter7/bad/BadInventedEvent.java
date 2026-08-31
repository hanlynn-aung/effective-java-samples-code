package chapter7.bad;

public final class BadInventedEvent {

    @FunctionalInterface
    public interface PriceListener {
        void onPrice(double price);
    }

    private PriceListener listener;

    public void onPrice(double price) {
        if (listener != null) {
            listener.onPrice(price);
        }
    }

    public void setListener(PriceListener listener) {
        this.listener = listener;
    }
}