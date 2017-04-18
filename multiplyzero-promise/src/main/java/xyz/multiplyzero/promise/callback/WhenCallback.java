package xyz.multiplyzero.promise.callback;

public interface WhenCallback<PARAM> extends Callback {
    Boolean when(PARAM t);
}
