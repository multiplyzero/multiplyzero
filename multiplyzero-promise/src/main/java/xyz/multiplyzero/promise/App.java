package xyz.multiplyzero.promise;

import xyz.multiplyzero.promise.api.Promise;
import xyz.multiplyzero.promise.factory.PromiseFactory;

public class App {
    public static void main(String[] args) {
        String str = "abcd";
        Promise<String> promise = PromiseFactory.create(str);
        promise.then(e -> e + 123).then(e -> e + 456).then(e -> e + 789)
                .when(e -> e.contains("123"), e -> e + "_success", e -> {
                    throw new RuntimeException(e);
                }).then(e -> e + "adf").then(e -> e + 123).failed((data, e) -> {
                    System.err.println(data);
                    e.printStackTrace();
                    return data;
                });
        promise.run(data -> {
            System.err.println(data + "-----" + Thread.currentThread().getName());
        }).run(data -> {
            System.err.println(data + "-----" + Thread.currentThread().getName());
        }).run(data -> {
            System.err.println(data + "-----" + Thread.currentThread().getName());

        }).then(e -> e.substring(0, 6)).start();

        System.err.println(promise.get());

        Long productId = 123456789L;
        PromiseFactory.create(productId).run(e -> {
        });

    }
}
