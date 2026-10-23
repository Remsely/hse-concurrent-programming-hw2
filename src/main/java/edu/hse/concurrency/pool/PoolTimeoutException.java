package edu.hse.concurrency.pool;

import java.time.Duration;

public class PoolTimeoutException extends Exception {

    public PoolTimeoutException(Duration timeout) {
        super("Соединение не получено за " + timeout);
    }
}
