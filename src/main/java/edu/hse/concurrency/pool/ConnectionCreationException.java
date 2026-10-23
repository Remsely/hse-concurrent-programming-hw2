package edu.hse.concurrency.pool;

public class ConnectionCreationException extends Exception {

    public ConnectionCreationException(Throwable cause) {
        super("Не удалось создать соединение", cause);
    }
}
