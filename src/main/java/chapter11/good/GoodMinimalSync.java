package chapter11.good;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class GoodMinimalSync {

    public interface Listener {
        void onReady();
    }

    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void setReady() {
        // Snapshot, then call alien code OUTSIDE any synchronization. Even if a
        // listener re-enters or blocks, it cannot deadlock this lock (there is
        // none held here).
        List<Listener> snapshot = new java.util.ArrayList<>(listeners);
        for (Listener listener : snapshot) {
            listener.onReady();
        }
    }
}