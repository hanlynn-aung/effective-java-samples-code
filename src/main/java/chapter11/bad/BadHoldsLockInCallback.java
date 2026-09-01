package chapter11.bad;

import java.util.ArrayList;
import java.util.List;

public final class BadHoldsLockInCallback {

    public interface Listener {
        void onReady();
    }

    private final List<Listener> listeners = new ArrayList<>();
    private final Object lock = new Object();

    public void addListener(Listener listener) {
        synchronized (lock) {
            listeners.add(listener);
        }
    }

    public void setReady() {
        synchronized (lock) {
            // BAD: calls unknown, possibly re-entrant or blocking alien code
            // while holding the lock. If a listener calls addListener() or
            // blocks, this stalls or deadlocks the whole object.
            for (Listener listener : listeners) {
                listener.onReady();
            }
        }
    }
}