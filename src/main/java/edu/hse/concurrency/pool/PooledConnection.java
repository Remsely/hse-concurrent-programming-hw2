package edu.hse.concurrency.pool;

/**
 * Соединение, выданное пулом. {@link #close()} возвращает его в пул.
 */
public interface PooledConnection extends AutoCloseable {

    /**
     * Физическое соединение.
     *
     * @throws IllegalStateException если соединение уже возвращено или закрыто пулом
     */
    RawConnection raw();

    /**
     * Возвращает соединение в пул. Повторный вызов ничего не делает.
     */
    @Override
    void close();
}
