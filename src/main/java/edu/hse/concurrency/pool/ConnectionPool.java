package edu.hse.concurrency.pool;

import java.time.Duration;

public interface ConnectionPool {

    PooledConnection borrow(Duration timeout)
            throws InterruptedException, PoolTimeoutException, ConnectionCreationException;

    void resize(int maxSize);

    PoolConfig config();

    PoolStats stats();

    void shutdown(Duration grace) throws InterruptedException;
}
