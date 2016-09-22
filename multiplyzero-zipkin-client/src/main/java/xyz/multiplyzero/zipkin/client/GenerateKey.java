package xyz.multiplyzero.zipkin.client;

import java.util.Random;

public class GenerateKey {
    private static Random RANDOM = new Random();

    public static Long generateKey() {
        return RANDOM.nextLong();
    }
}
