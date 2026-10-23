package edu.hse.concurrency.pool;

/**
 * Создаёт физические соединения. Создание медленное и может упасть.
 */
@FunctionalInterface
public interface ConnectionFactory {

    RawConnection create() throws Exception;
}
