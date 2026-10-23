package edu.hse.concurrency.pool;

import java.time.Duration;

public class ConnectionPoolImpl implements ConnectionPool {

    public ConnectionPoolImpl(PoolConfig config, ConnectionFactory factory) {
        // TODO
    }

    @Override
    public PooledConnection borrow(Duration timeout)
            throws InterruptedException, PoolTimeoutException, ConnectionCreationException {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void resize(int maxSize) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public PoolConfig config() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public PoolStats stats() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void shutdown(Duration grace) throws InterruptedException {
        throw new UnsupportedOperationException("TODO");
    }
}
