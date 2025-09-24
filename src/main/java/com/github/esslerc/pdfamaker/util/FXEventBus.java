package com.github.esslerc.pdfamaker.util;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class FXEventBus {

    private static final CopyOnWriteArrayList<Consumer<Object>> listeners = new CopyOnWriteArrayList<>();

    public static <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.add(event -> {
            if (eventType.isInstance(event)) {
                listener.accept(eventType.cast(event));
            }
        });
    }

    public static void unsubscribe(Consumer<Object> listener) {
        listeners.remove(listener);
    }

    public static void publish(Object event) {
        for (Consumer<Object> listener : listeners) {
            listener.accept(event);
        }
    }
}
