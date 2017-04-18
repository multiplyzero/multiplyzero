package xyz.multiplyzero.promise.factory;

import xyz.multiplyzero.promise.api.DefaultPromise;
import xyz.multiplyzero.promise.api.Promise;

public class PromiseFactory {
    public static final <DATA> Promise<DATA> create(DATA data) {
        return new DefaultPromise<>(data);
    }
}
