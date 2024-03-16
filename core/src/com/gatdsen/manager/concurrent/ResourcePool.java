package com.gatdsen.manager.concurrent;

import java.util.*;

/**
 * Diese Klasse dient zur zentralen Verwaltung von Ressourcen, die von verschiedenen Teilen des Programms benötigt
 * werden.
 * Dazu gehören aktuell Threads (in Form von {@link java.util.concurrent.ExecutorService} Instanzen), Prozessen, sowie
 * RMI-Registries und RMI-Kommunikatoren.
 */
public final class ResourcePool {

    private static final ResourcePool instance = new ResourcePool();

    /**  */
    private final Map<Integer, Queue<ThreadExecutor>> availableThreadExecutors = new HashMap<>();
    private final Set<ThreadExecutor> usedThreadExecutors = new HashSet<>();
    private final Queue<ProcessExecutor> availableProcessExecutors = new LinkedList<>();
    private final Set<ProcessExecutor> usedProcessExecutors = new HashSet<>();
    private final Map<String, RMIRegistry> registries = new HashMap<>();
    private final Set<Resource> unfinishedResources = new HashSet<>();

    public static ResourcePool getInstance() {
        return instance;
    }

    /**
     * Gibt einen {@link ThreadExecutor} zurück, der die angegebene Anzahl an Threads verwaltet. Es verwendet dazu
     * entweder einen bereits erstellten und nun verfügbaren Executor, oder erstellt einen neuen.
     * @param threadCount Die Anzahl an Threads, die der Executor verwalten soll.
     * @return Ein ThreadExecutor, der die angegebene Anzahl an Threads verwaltet.
     */
    public ThreadExecutor requestThreadExecutor(int threadCount) {
        synchronized (availableThreadExecutors) {
            Queue<ThreadExecutor> executors = availableThreadExecutors.computeIfAbsent(
                    threadCount,
                    k -> new LinkedList<>()
            );
            ThreadExecutor executor = executors.poll();
            if (executor == null) {
                executor = new ThreadExecutor(threadCount);
            }
            usedThreadExecutors.add(executor);
            return executor;
        }
    }

    /**
     * Gibt einen {@link ThreadExecutor} wieder zurück, nachdem dieser von {@link #requestThreadExecutor(int)} erhalten
     * wurde.
     * @param executor Der Executor, der zurückgegeben werden soll.
     * @param reusable Gibt an, ob der Executor wiederverwendet werden kann. Wenn dieser Wert auf false gesetzt ist,
     *                 wird der Executor beendet, statt wieder vergeben zu werden.
     */
    public void releaseThreadExecutor(ThreadExecutor executor, boolean reusable) {
        synchronized (availableThreadExecutors) {
            // Den Rückgabewert des Sets, ob der Executor überhaupt enthalten war, können wir ignorieren.
            // Selbst wenn die Methode mit einem völlig unbekannten Executor aufgerufen wird, können wir ihn einfach als
            // verfügbar anbieten.
            usedThreadExecutors.remove(executor);
            if (reusable) {
                availableThreadExecutors.computeIfAbsent(
                        executor.getThreadCount(),
                        k -> new LinkedList<>()
                ).add(executor);
            } else {
                disposeResource(executor);
            }
        }
    }

    /**
     * Gibt einen {@link ProcessExecutor} zurück. Es verwendet dazu entweder einen bereits erstellten und nun
     * verfügbaren Executor, oder erstellt einen neuen.
     * @return Ein ProcessExecutor, der einen Prozess verwaltet.
     */
    public ProcessExecutor requestProcessExecutor() {
        synchronized (availableProcessExecutors) {
            ProcessExecutor executor = availableProcessExecutors.poll();
            if (executor == null) {
                executor = new ProcessExecutor();
            }
            usedProcessExecutors.add(executor);
            return executor;
        }
    }

    /**
     * Gibt einen {@link ProcessExecutor} wieder zurück, nachdem dieser von {@link #requestProcessExecutor()} erhalten
     * wurde.
     * @param executor Der Executor, der zurückgegeben werden soll.
     * @param reusable Gibt an, ob der Executor wiederverwendet werden kann. Wenn dieser Wert auf false gesetzt ist,
     *                 wird der Executor beendet, statt wieder vergeben zu werden.
     */
    public void releaseProcessExecutor(ProcessExecutor executor, boolean reusable) {
        synchronized (availableProcessExecutors) {
            // Den Rückgabewert des Sets, ob der Executor überhaupt enthalten war, können wir ignorieren.
            // Selbst wenn die Methode mit einem völlig unbekannten Executor aufgerufen wird, können wir ihn einfach als
            // verfügbar anbieten.
            usedProcessExecutors.remove(executor);
            if (reusable) {
                availableProcessExecutors.add(executor);
            } else {
                disposeResource(executor);
            }
        }
    }

    /**
     * Gibt ein {@link RMIRegistry} zurück, das auf dem Standard-Host und -Port läuft.
     * @return Das Registry, welches auf dem Standard-Host und -Port läuft
     */
    public RMIRegistry requestRegistry() {
        return requestRegistry(null, null);
    }

    /**
     * Gibt ein {@link RMIRegistry} zurück, das auf dem angegebenen Host läuft.
     * @param host Der Host, an dem das Registry läuft
     * @param port Der Port, an dem das Registry läuft
     * @return Das Registry, welches auf dem angegebenen Host und Port läuft
     */
    public RMIRegistry requestRegistry(String host, Integer port) {
        String key = RMIRegistry.getHostDetailsKey(host, port);
        RMIRegistry registry = registries.get(key);
        // Wenn die Variable null ist, dann existiert noch kein Registry für den angegebenen Host und Port
        if (registry == null) {
            registry = new RMIRegistry(host, port);
            registries.put(key, registry);
        }
        return registry;
    }

    /**
     * Beendet den ResourcePool und alle darin enthaltenen Ressourcen. Bevor diese Methode aufgerufen wird, sollten alle
     * Ressourcen, die der ResourcePool verwaltet, wieder freigegeben werden.
     * @see Resource#dispose()
     */
    public void dispose() {
        for (Queue<ThreadExecutor> executors : availableThreadExecutors.values()) {
            for (ThreadExecutor executor : executors) {
                disposeResource(executor);
            }
        }
        availableThreadExecutors.clear();
        for (ThreadExecutor executor : usedThreadExecutors) {
            System.err.println("ResourcePool.dispose(): Stopping ThreadExecutor that is still in use.");
            disposeResource(executor);
        }
        usedThreadExecutors.clear();
        for (ProcessExecutor executor : availableProcessExecutors) {
            disposeResource(executor);
        }
        availableProcessExecutors.clear();
        for (ProcessExecutor executor : usedProcessExecutors) {
            System.err.println("ResourcePool.dispose(): Stopping ProcessExecutor that is still in use.");
            disposeResource(executor);
        }
        usedProcessExecutors.clear();
        for (RMIRegistry registry : registries.values()) {
            disposeResource(registry);
        }
        registries.clear();
        for (Resource resource : unfinishedResources) {
            if (resource.isDisposed()) {
                continue;
            }
            try {
                resource.waitForDispose();
            } catch (InterruptedException ignored) {
                resource.forceDispose();
            }
        }
        unfinishedResources.clear();
    }

    /**
     * Hilfsmethode, die eine Resource mittels {@link Resource#dispose()} beendet. Wenn die Resource nicht direkt
     * beendet werden kann, wird sie in die Liste der unvollendeten Resources aufgenommen.
     * @param resource Die zu beendende Resource
     */
    private void disposeResource(Resource resource) {
        resource.dispose();
        if (!resource.isDisposed()) {
            unfinishedResources.add(resource);
        }
    }
}
