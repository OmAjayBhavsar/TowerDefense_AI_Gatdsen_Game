package com.gatdsen.manager.concurrent;

import java.util.concurrent.*;

/**
 * Diese Klasse repräsentiert einen ThreadPool mit einer festen Anzahl von Threads, die Aufgaben ausführen können.
 * Die Anzahl der Threads wird bei der Erstellung festgelegt und kann nicht mehr verändert werden.
 * Dazu wird intern ein {@link ExecutorService} verwendet, der die Threads verwaltet.
 */
public final class ThreadExecutor extends Resource {

    /** Die Anzahl der Threads, die dieser ThreadExecutor verwaltet */
    private final int threadCount;
    /** Der ExecutorService, der die Threads verwaltet */
    private final ExecutorService executor;

    /**
     * @param threadCount Die Anzahl der Threads, die dieser ThreadExecutor verwalten soll. Dazu wird ein ThreadPool mit
     *                    fester Größe mittels {@link Executors#newFixedThreadPool(int)} erstellt.
     */
    public ThreadExecutor(int threadCount) {
        this.threadCount = threadCount;
        executor = Executors.newFixedThreadPool(threadCount);
    }

    /**
     * @return Gibt die Anzahl der Threads zurück, die dieser ThreadExecutor verwaltet.
     */
    public int getThreadCount() {
        return threadCount;
    }

    /**
     * Startet eine neue Aufgabe in einem der Threads dieses ThreadExecutors.
     * @param callable Der {@link Callable}, der die Aufgabe repräsentiert und ausgeführt werden soll.
     * @return Ein {@link Future} Objekt, das die Fertigstellung und das Ergebnis des Callables repräsentiert.
     */
    public <T> Future<T> execute(Callable<T> callable) {
        return executor.submit(callable);
    }

    /**
     * Startet eine neue Aufgabe in einem der Threads dieses ThreadExecutors.
     * @param runnable Der {@link Runnable}, der die Aufgabe repräsentiert und ausgeführt werden soll.
     * @return Ein {@link Future} Objekt, das die Fertigstellung der Aufgabe repräsentiert.
     */
    public Future<?> execute(Runnable runnable) {
        return executor.submit(runnable);
    }

    /**
     * Startet eine neue Aufgabe in einem der Threads dieses ThreadExecutors. <br>
     * Anders als {@link #execute(Runnable)} gibt diese Methode ein CompletableFuture zurück, welche mehr
     * Funktionalität bieten, wie bspw. die Angabe von Callbacks, die bei Fertigstellung der Aufgabe ausgeführt werden
     * sollen. <br>
     * Da der Ansatz mit CompletableFutures zwar mächtiger ist, aber minimal höheren Overhead hat, wurde diese Methode
     * zusätzlich zu {@link #execute(Runnable)} hinzugefügt, um die Wahl zwischen beiden Ansätzen zu ermöglichen und
     * keinen vorzuschreiben.
     * @param runnable Der {@link Runnable}, der die Aufgabe repräsentiert und ausgeführt werden soll.
     * @return Ein {@link CompletableFuture} Objekt, das die Fertigstellung der Aufgabe repräsentiert.
     */
    public CompletableFuture<?> executeCompletable(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executor);
    }

    /**
     * Beendet diesen ThreadExecutor, indem versucht wird alle laufenden Aufgaben abzubrechen.
     * Es können keine neuen Aufgaben mehr gestartet werden.
     */
    public void dispose() {
        if (isDisposed()) {
            return;
        }
        // ExecutorService.shutdownNow() sorgt dafür, dass keine neuen Aufgaben mehr gestartet werden können und
        // versucht alle laufenden Threads mittels Interrupts zu beenden.
        // Wir verwenden es statt ExecutorService.shutdown(), da dieses die bereits laufenden Aufgaben zu Ende laufen
        // lassen würde, wir aber beim Schließen unseres ThreadExecutors alle Aufgaben abbrechen wollen.
        // ExecutorService.shutdownNow() wartet dabei nicht bis alle Threads beendet werden konnten. Wir wollen darauf
        // aber auch nicht explizit warten, da wir keine Garantie haben, dass jeder der Threads überhaupt durch ein
        // Interrupt unterbrochen werden können und diese nicht einfach ignorieren und potenziell für immer
        // weiterlaufen würden.
        executor.shutdownNow();
        setDisposed();
    }
}
