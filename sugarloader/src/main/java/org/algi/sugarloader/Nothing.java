package org.algi.sugarloader;

import org.algi.sugarloader.function.Call;
import org.algi.sugarloader.function.Consumer;
import org.algi.sugarloader.function.Supplier;

class Nothing<T> implements Call, Consumer<T>, Supplier<T> {
    @Override
    public void apply() {
    }

    @Override
    public T get() throws Exception {
        return null;
    }

    @Override
    public void accept(final T o) {
    }

    private static final Nothing DO_NOTHING = new Nothing();

    @SuppressWarnings("unchecked")
    public static <T> Nothing<T> doNothing() {
        return DO_NOTHING;
    }
}
