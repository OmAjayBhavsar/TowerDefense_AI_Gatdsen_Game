package com.gatdsen.manager.concurrent;

import java.util.concurrent.*;

/**
 *
 */
public final class ThreadExecutor {

    private final int threadCount;
    private final ExecutorService executor;

    public ThreadExecutor(int threadCount) {
        this.threadCount = threadCount;
        executor = Executors.newFixedThreadPool(threadCount);
    }

    public int getThreadCount() {
        return threadCount;
    }

    public ExecutorService getExecutorService() {
        return executor;
    }

    public <T> Future<T> execute(Callable<T> callable) {
        return executor.submit(callable);
    }

    public Future<?> execute(Runnable runnable) {
        return executor.submit(runnable);
    }

    /**
     * Beendet diesen ThreadExecutor, indem versucht wird alle laufenden Aufgaben abzubrechen.
     * Es können keine neuen Aufgaben mehr gestartet werden.
     */
    public void dispose() {
        // ExecutorService.shutdownNow() sorgt dafür, dass keine neuen Aufgaben mehr gestartet werden können und
        // versucht alle laufenden Threads mittels Interrupts zu beenden.
        // Wir verwenden es statt ExecutorService.shutdown(), da dieses die bereits laufenden Aufgaben zu Ende laufen
        // lassen würde, wir aber beim Schließen unseres ThreadExecutors alle Aufgaben abbrechen wollen.
        // ExecutorService.shutdownNow() wartet dabei nicht bis alle Threads beendet werden konnten. Wir wollen darauf
        // aber auch nicht explizit warten, da wir keine Garantie haben, dass jeder der Threads überhaupt durch ein
        // Interrupt unterbrochen werden können und diese nicht einfach ignorieren und potenziell für immer
        // weiterlaufen würden.
        executor.shutdownNow();
    }
}
