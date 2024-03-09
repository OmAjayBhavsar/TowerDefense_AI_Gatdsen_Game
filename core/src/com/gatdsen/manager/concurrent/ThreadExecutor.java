package com.gatdsen.manager.concurrent;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 *
 */
public final class ThreadExecutor {

    private final int threadCount;
    private final ExecutorService executor;
    private final Set<Future<?>> runningTasks = new HashSet<>();

    public ThreadExecutor(int threadCount) {
        this.threadCount = threadCount;
        executor = Executors.newFixedThreadPool(threadCount);
    }

    public int getThreadCount() {
        return threadCount;
    }

    public <T> Future<T> execute(Callable<T> callable) {
        Future<T> future = executor.submit(callable);
        runningTasks.add(future);
        return future;
    }

    public Future<?> execute(Runnable runnable) {
        Future<?> future = executor.submit(runnable);
        runningTasks.add(future);
        return future;
    }

    /**
     * Überprüft, ob alle Aufgaben, die von diesem ThreadExecutor gestartet wurden, beendet sind.
     * @return true, wenn keine Aufgaben mehr laufen, ansonsten false
     */
    public boolean isIdling() {
        runningTasks.removeIf(Future::isDone);
        return runningTasks.isEmpty();
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
