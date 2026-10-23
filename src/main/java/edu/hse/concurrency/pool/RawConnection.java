package edu.hse.concurrency.pool;

/**
 * Физическое соединение с базой. Создаётся фабрикой, закрывается только пулом.
 */
public interface RawConnection extends AutoCloseable {

    /**
     * Закрывает физическое соединение. Может работать долго и бросить исключение.
     */
    @Override
    void close();
}
