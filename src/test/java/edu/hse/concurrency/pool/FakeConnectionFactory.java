package edu.hse.concurrency.pool;

import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика для тестов: создаёт мгновенно, запоминает все созданные соединения.
 * Однопоточная — для конкурентных тестов напишите свою.
 */
class FakeConnectionFactory implements ConnectionFactory {

    final List<FakeConnection> created = new ArrayList<>();
    boolean failNext;

    @Override
    public RawConnection create() {
        if (failNext) {
            failNext = false;
            throw new IllegalStateException("база недоступна");
        }
        FakeConnection connection = new FakeConnection(created.size() + 1);
        created.add(connection);
        return connection;
    }

    static final class FakeConnection implements RawConnection {

        final int id;
        boolean closed;

        FakeConnection(int id) {
            this.id = id;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public String toString() {
            return "conn-" + id;
        }
    }
}
