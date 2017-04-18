package xyz.multiplyzero.promise.callback;

public interface RunCallback<DATA> extends Callback {
    public void run(DATA data);
}
