package xyz.multiplyzero.promise.callback;

public interface FailedCallback<DATA> extends Callback {
    public DATA failed(DATA data, Exception e);

}
