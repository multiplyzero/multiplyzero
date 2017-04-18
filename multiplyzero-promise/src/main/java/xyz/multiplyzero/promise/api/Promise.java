package xyz.multiplyzero.promise.api;

import java.util.concurrent.Executor;
import java.util.function.Function;

import xyz.multiplyzero.promise.callback.DoneCallback;
import xyz.multiplyzero.promise.callback.FailedCallback;
import xyz.multiplyzero.promise.callback.RunCallback;
import xyz.multiplyzero.promise.callback.WhenCallback;

public interface Promise<DATA> {

    public DATA get();

    public Promise<DATA> when(WhenCallback<DATA> when, DoneCallback<DATA> success, DoneCallback<DATA> failed);

    public Promise<DATA> then(DoneCallback<DATA> then);

    public Promise<DATA> run(RunCallback<DATA> run);

    public Promise<DATA> run(Executor executor, RunCallback<DATA> run);

    public Promise<DATA> newThread();

    public <AFTER_MAP> Promise<AFTER_MAP> map(Function<? super DATA, ? extends AFTER_MAP> mapper);

    public void start();

    Promise<DATA> failed(FailedCallback<DATA> failedCallback);

}
