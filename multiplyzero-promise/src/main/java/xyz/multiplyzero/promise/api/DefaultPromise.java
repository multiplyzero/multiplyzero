package xyz.multiplyzero.promise.api;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.function.Function;

import xyz.multiplyzero.promise.callback.Callback;
import xyz.multiplyzero.promise.callback.DoneCallback;
import xyz.multiplyzero.promise.callback.FailedCallback;
import xyz.multiplyzero.promise.callback.RunCallback;
import xyz.multiplyzero.promise.callback.WhenCallback;
import xyz.multiplyzero.promise.factory.PromiseFactory;

public class DefaultPromise<DATA> implements Promise<DATA> {

    public DefaultPromise(DATA data) {
        this.data = data;
    }

    private Queue<Callback> callbackQueue = new ConcurrentLinkedQueue<>();

    private DATA data;

    @Override
    public Promise<DATA> when(WhenCallback<DATA> when, DoneCallback<DATA> success, DoneCallback<DATA> failed) {
        callbackQueue.add(when);
        callbackQueue.add(success);
        callbackQueue.add(failed);
        return this;
    }

    @Override
    public Promise<DATA> then(DoneCallback<DATA> then) {
        callbackQueue.add(then);
        return this;
    }

    @Override
    public Promise<DATA> failed(FailedCallback<DATA> failedCallback) {
        callbackQueue.add(failedCallback);
        return this;
    }

    @Override
    public Promise<DATA> run(Executor executor, RunCallback<DATA> run) {
        executor.execute(() -> {
            run.run(data);
        });
        return this;
    }

    @Override
    public Promise<DATA> run(RunCallback<DATA> run) {
        new Thread(() -> {
            run.run(data);
        }).start();
        return this;
    }

    @Override
    public <AFTER_MAP> Promise<AFTER_MAP> map(Function<? super DATA, ? extends AFTER_MAP> mapper) {
        AFTER_MAP afterMap = mapper.apply(data);
        return PromiseFactory.create(afterMap);
    }

    private void whenDone(WhenCallback<DATA> when, DoneCallback<DATA> success, DoneCallback<DATA> failed) {
        Boolean b = when.when(data);
        if (b != null && b) {
            if (success != null) {
                data = success.done(data);
            }
        } else {
            if (failed != null) {
                data = failed.done(data);
            }
        }
    }

    @Override
    public void start() {
        while (callbackQueue.iterator().hasNext()) {
            Callback callback = callbackQueue.poll();
            try {
                if (callback instanceof WhenCallback) {
                    WhenCallback<DATA> when = (WhenCallback<DATA>) callback;
                    DoneCallback<DATA> success = (DoneCallback<DATA>) callbackQueue.poll();
                    DoneCallback<DATA> failed = (DoneCallback<DATA>) callbackQueue.poll();
                    whenDone(when, success, failed);
                } else if (callback instanceof DoneCallback) {
                    DoneCallback<DATA> doneCallback = (DoneCallback<DATA>) callback;
                    data = doneCallback.done(data);
                }
            } catch (Exception e) {
                while (((callback = callbackQueue.poll()) == null) || !(callback instanceof FailedCallback)) {
                }
                if (callback != null) {
                    FailedCallback<DATA> failed = (FailedCallback) callback;
                    data = failed.failed(data, e);
                }
            }
        }
    }

    @Override
    public DATA get() {
        return this.data;
    }

    @Override
    public Promise<DATA> newThread() {
        return PromiseFactory.create(data);
    }

}
