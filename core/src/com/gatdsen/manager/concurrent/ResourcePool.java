package com.gatdsen.manager.concurrent;

import java.util.*;

public final class ResourcePool {

    private static final ResourcePool instance = new ResourcePool();

    private final Map<Integer, Queue<ThreadExecutor>> availableThreadExecutors = new HashMap<>();
    private final Set<ThreadExecutor> usedThreadExecutors = new HashSet<>();
    private final Queue<ProcessExecutor> availableProcessExecutors = new LinkedList<>();
    private final Set<ProcessExecutor> usedProcessExecutors = new HashSet<>();
    private final Map<String, RMIRegistry> registries = new HashMap<>();

    public static ResourcePool getInstance() {
        return instance;
    }

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

    public void releaseThreadExecutor(ThreadExecutor executor, boolean reusable) {
        if (!reusable) {
            executor.dispose();
            return;
        }
        synchronized (availableThreadExecutors) {
            // Den Rückgabewert des Sets, ob der Executor überhaupt enthalten war, können wir ignorieren.
            // Selbst wenn die Methode mit einem völlig unbekannten Executor aufgerufen wird, können wir ihn einfach als
            // verfügbar anbieten.
            usedThreadExecutors.remove(executor);
            availableThreadExecutors.computeIfAbsent(
                    executor.getThreadCount(),
                    k -> new LinkedList<>()
            ).add(executor);
        }
    }

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

    public void releaseProcessExecutor(ProcessExecutor executor, boolean reusable) {
        if (!reusable) {
            executor.dispose();
            return;
        }
        synchronized (availableProcessExecutors) {
            // Den Rückgabewert des Sets, ob der Executor überhaupt enthalten war, können wir ignorieren.
            // Selbst wenn die Methode mit einem völlig unbekannten Executor aufgerufen wird, können wir ihn einfach als
            // verfügbar anbieten.
            usedProcessExecutors.remove(executor);
            availableProcessExecutors.add(executor);
        }
    }

    public RMIRegistry requestRegistry() {
        return requestRegistry(null, null);
    }

    public RMIRegistry requestRegistry(String host) {
        return requestRegistry(host, null);
    }

    public RMIRegistry requestRegistry(Integer port) {
        return requestRegistry(null, port);
    }

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

    public void dispose() {
        for (Queue<ThreadExecutor> executors : availableThreadExecutors.values()) {
            for (ThreadExecutor executor : executors) {
                executor.dispose();
            }
        }
        availableThreadExecutors.clear();
        for (ThreadExecutor executor : usedThreadExecutors) {
            System.err.println("ResourcePool.dispose(): Disposing of a ThreadExecutor that is still in use.");
            executor.dispose();
        }
        usedThreadExecutors.clear();
        for (ProcessExecutor executor : availableProcessExecutors) {
            executor.dispose();
        }
        availableProcessExecutors.clear();
        for (ProcessExecutor executor : usedProcessExecutors) {
            System.err.println("ResourcePool.dispose(): Disposing of a ProcessExecutor that is still in use.");
            executor.dispose();
        }
        usedProcessExecutors.clear();
        registries.clear();
    }
}
