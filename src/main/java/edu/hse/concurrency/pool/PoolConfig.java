package edu.hse.concurrency.pool;

import java.time.Duration;
import java.util.Objects;

public record PoolConfig(int minIdle, int maxSize, Duration idleTimeout, boolean fair) {

    public static final int MAX_POOL_SIZE = 1000;

    public PoolConfig {
        Objects.requireNonNull(idleTimeout, "idleTimeout");
        if (maxSize < 1 || maxSize > MAX_POOL_SIZE) {
            throw new IllegalArgumentException(
                    "maxSize должен быть от 1 до " + MAX_POOL_SIZE + ", получено " + maxSize);
        }
        if (minIdle < 0 || minIdle > maxSize) {
            throw new IllegalArgumentException(
                    "minIdle должен быть от 0 до maxSize = " + maxSize + ", получено " + minIdle);
        }
        if (idleTimeout.isNegative() || idleTimeout.isZero()) {
            throw new IllegalArgumentException("idleTimeout должен быть положительным: " + idleTimeout);
        }
    }

    public PoolConfig withMaxSize(int maxSize) {
        return new PoolConfig(minIdle, maxSize, idleTimeout, fair);
    }
}
