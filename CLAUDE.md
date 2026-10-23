# CLAUDE.md

Этот файл содержит рекомендации для Claude Code (claude.ai/code) при работе с кодом в этом репозитории.

## Что это

Шаблон домашнего задания №2 «Пул соединений» курса «Параллельное программирование» НИУ ВШЭ. Условие — `README.md`
(контракт, ограничения, критерии). Репозиторий выдаётся студентам: в `ConnectionPoolImpl` должны оставаться заглушки
`TODO`, решение сюда не коммитится.

Курс и наброски всех ДЗ — в соседнем репозитории `../hse-concurrent-programming` (`docs/index.html`, массив `HW`).
Образец оформления — `../hse-concurrent-programming-hw1`: та же структура README, Gradle и smoke-теста.

## Команды

- `./gradlew test` — smoke-тест (на заглушке все тесты падают — это ожидаемо)
- `./gradlew test --tests 'edu.hse.concurrency.pool.ConnectionPoolSmokeTest.borrowTimesOutWhenPoolIsExhausted'` — один тест

## Устройство

- Пакет `edu.hse.concurrency.pool`. Выданные типы (`PoolConfig`, `PoolStats`, `ConnectionFactory`, `RawConnection`,
  `PooledConnection`, исключения, интерфейс `ConnectionPool`) — часть контракта: при правке синхронизировать с README.
- `ConnectionPoolSmokeTest` — только однопоточные проверки; всё конкурентное — в скрытых тестах (не в этом репозитории).
- Темы ДЗ — недели 4–6 курса: `ReentrantLock`/`Condition`, `Semaphore`, честность, таймауты, deadlock.
  Атомики, конкурентные коллекции и пулы потоков ещё не пройдены и запрещены.

## Примечания

- `.tclaude-docker.status` — служебный файл tclaude/Colima, к проекту не относится.
