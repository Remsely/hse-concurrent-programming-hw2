package edu.hse.concurrency.pool;

/**
 * Снимок состояния пула.
 *
 * @param total    соединений в пуле: {@code idle + borrowed + creating}
 * @param idle     свободных соединений
 * @param borrowed выданных соединений
 * @param creating соединений, которые создаются прямо сейчас
 * @param waiting  потоков, которые ждут в {@code borrow} и ещё ничего не получили
 */
public record PoolStats(int total, int idle, int borrowed, int creating, int waiting) {
}
