package edu.hse.concurrency.pool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Проверяет только базовую, однопоточную работоспособность контракта {@link ConnectionPool}.
 * Поведение под конкурентной нагрузкой (ожидание, честность, медленное создание, вытеснение,
 * shutdown с ожидающими потоками и т.д.) проверяется отдельными скрытыми тестами и здесь не затрагивается.
 */
@Timeout(5)
class ConnectionPoolSmokeTest {

    private static final Duration SHORT = Duration.ofMillis(50);

    private FakeConnectionFactory factory;
    private ConnectionPool pool;

    @BeforeEach
    void setUp() {
        factory = new FakeConnectionFactory();
        pool = new ConnectionPoolImpl(new PoolConfig(0, 2, Duration.ofMinutes(1), false), factory);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        pool.shutdown(Duration.ZERO);
    }

    @Test
    void constructorCreatesNothing() {
        assertTrue(factory.created.isEmpty());
        assertEquals(new PoolStats(0, 0, 0, 0, 0), pool.stats());
    }

    @Test
    void borrowCreatesConnectionAndCloseReturnsIt() throws Exception {
        PooledConnection connection = pool.borrow(SHORT);

        assertEquals(1, factory.created.size());
        assertSame(factory.created.getFirst(), connection.raw());
        assertEquals(new PoolStats(1, 0, 1, 0, 0), pool.stats());

        connection.close();

        assertEquals(new PoolStats(1, 1, 0, 0, 0), pool.stats());
        assertFalse(factory.created.getFirst().closed);
    }

    @Test
    void returnedConnectionIsReused() throws Exception {
        PooledConnection first = pool.borrow(SHORT);
        RawConnection raw = first.raw();
        first.close();

        PooledConnection second = pool.borrow(SHORT);

        assertSame(raw, second.raw());
        assertNotSame(first, second);
        assertEquals(1, factory.created.size());
    }

    @Test
    void staleHandleIsHarmless() throws Exception {
        PooledConnection first = pool.borrow(SHORT);
        first.close();
        PooledConnection second = pool.borrow(SHORT);

        assertDoesNotThrow(first::close);
        assertThrows(IllegalStateException.class, first::raw);
        assertEquals(new PoolStats(1, 0, 1, 0, 0), pool.stats());
        assertDoesNotThrow(second::raw);
    }

    @Test
    void borrowTimesOutWhenPoolIsExhausted() throws Exception {
        pool.borrow(SHORT);
        pool.borrow(SHORT);

        assertThrows(PoolTimeoutException.class, () -> pool.borrow(SHORT));
        assertThrows(PoolTimeoutException.class, () -> pool.borrow(Duration.ZERO));
        assertEquals(2, factory.created.size());
    }

    @Test
    void failedCreationFreesTheSlot() throws Exception {
        factory.failNext = true;

        ConnectionCreationException e = assertThrows(ConnectionCreationException.class, () -> pool.borrow(SHORT));

        assertInstanceOf(IllegalStateException.class, e.getCause());
        assertEquals(new PoolStats(0, 0, 0, 0, 0), pool.stats());
        pool.borrow(SHORT);
        pool.borrow(SHORT);
        assertEquals(2, pool.stats().borrowed());
    }

    @Test
    void resizeChangesConfig() throws Exception {
        pool.borrow(SHORT);
        pool.borrow(SHORT);

        pool.resize(3);

        assertEquals(3, pool.config().maxSize());
        assertDoesNotThrow(() -> pool.borrow(SHORT));
    }

    @Test
    void shrinkClosesIdleConnectionsAboveLimit() throws Exception {
        PooledConnection a = pool.borrow(SHORT);
        PooledConnection b = pool.borrow(SHORT);
        a.close();
        b.close();

        pool.resize(1);

        assertEquals(new PoolStats(1, 1, 0, 0, 0), pool.stats());
        assertEquals(1, factory.created.stream().filter(c -> c.closed).count());
    }

    @Test
    void shutdownClosesConnectionsAndRejectsBorrow() throws Exception {
        pool.borrow(SHORT).close();

        pool.shutdown(Duration.ZERO);

        assertTrue(factory.created.getFirst().closed);
        assertThrows(IllegalStateException.class, () -> pool.borrow(SHORT));
        assertThrows(IllegalStateException.class, () -> pool.resize(3));
        assertDoesNotThrow(pool::stats);
        assertDoesNotThrow(() -> pool.shutdown(Duration.ZERO));
    }

    @Test
    void borrowedConnectionIsForceClosedAfterGrace() throws Exception {
        PooledConnection connection = pool.borrow(SHORT);

        pool.shutdown(Duration.ofMillis(200));

        assertTrue(factory.created.getFirst().closed);
        assertThrows(IllegalStateException.class, connection::raw);
        assertDoesNotThrow(connection::close);
    }

    @Test
    void invalidArgumentsAreRejected() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> pool.borrow(Duration.ofMillis(-1)));
        assertThrows(IllegalArgumentException.class, () -> pool.resize(0));
        assertThrows(NullPointerException.class, () -> pool.borrow(null));
        assertThrows(NullPointerException.class, () -> new ConnectionPoolImpl(null, factory));
        assertThrows(IllegalArgumentException.class, () -> new PoolConfig(3, 2, Duration.ofMinutes(1), false));

        ConnectionPool withMinIdle = new ConnectionPoolImpl(new PoolConfig(2, 4, Duration.ofMinutes(1), true), factory);
        assertThrows(IllegalArgumentException.class, () -> withMinIdle.resize(1));
        withMinIdle.shutdown(Duration.ZERO);

        assertEquals(new PoolStats(0, 0, 0, 0, 0), pool.stats());
    }
}
