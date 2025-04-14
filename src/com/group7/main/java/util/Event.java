package util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Event<T> {
    private final List<Consumer<T>> listenerList;

    public Event() {
        listenerList = new ArrayList<>();
    }

    public void addListener(Consumer<T> listener) {
        listenerList.add(listener);
    }

    public void removeListener(Consumer<T> listener) {
        listenerList.remove(listener);
    }

    public void invoke(Object sender, T args) {
        for (Consumer<T> listener : listenerList) {
            listener.accept(args);
        }
    }

}
