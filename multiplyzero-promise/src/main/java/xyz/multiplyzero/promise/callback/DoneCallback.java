package xyz.multiplyzero.promise.callback;

public interface DoneCallback<DATA> extends Callback {
    public DATA done(DATA data);
}
