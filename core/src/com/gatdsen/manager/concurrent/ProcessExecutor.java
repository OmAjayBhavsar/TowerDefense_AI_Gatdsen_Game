package com.gatdsen.manager.concurrent;

import com.gatdsen.networking.BotProcessLauncher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Diese Klasse repräsentiert einen Prozess, der auf der gleichen Maschine wie das Hauptprogramm ausgeführt wird und
 * Aufgaben ausführen kann.
 */
public final class ProcessExecutor extends Resource {

    public static final String GAME_PROCESS_REFERENCE_NAME_FORMAT = "GameProcessCommunicator_%d";
    public static final String PLAYER_PROCESS_REFERENCE_NAME_FORMAT = "PlayerProcessCommunicator_%d";

    /** Der Prozess, der von diesem Executor gestartet wurde */
    private final Process process;
    /** Der Kommunikator, der für die Kommunikation mit dem Prozess verwendet wird */
    public final RMICommunicator communicator;

    public ProcessExecutor() {
        RMIRegistry registry = ResourcePool.getInstance().requestRegistry();
        process = createProcess(registry.host, registry.port);
        communicator = new RMICommunicator(
                registry,
                String.format(GAME_PROCESS_REFERENCE_NAME_FORMAT, process.pid()),
                String.format(PLAYER_PROCESS_REFERENCE_NAME_FORMAT, process.pid())
        );
    }

    /**
     * Erstellt einen neuen Prozess, der auf der gleichen Maschine wie das Hauptprogramm ausgeführt wird und Aufgaben
     * ausführen kann.
     * @param registryHost Der Host der Remote Object Registry
     * @param registryPort Der Port der Remote Object Registry
     * @return Der Prozess, der gestartet wurde
     */
    private static Process createProcess(String registryHost, int registryPort) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.inheritIO();
        File currentJar;
        try {
            currentJar = new File(ProcessExecutor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        // -Djava.rmi.server.logCalls=true
        String logCalls = System.getProperty("java.rmi.server.logCalls");
        if (logCalls != null) {
            builder.environment().put("java.rmi.server.logCalls", logCalls);
        }
        builder.command(
                // Starten der JVM in der main() vom BotProcessLauncher
                "java",
                //"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005",
                "-cp", currentJar.getPath(), BotProcessLauncher.class.getName(),
                // Angabe des Hosts der Remote Object Registry
                "-host", registryHost,
                // Angabe des Ports der Remote Object Registry
                "-port", String.valueOf(registryPort)
        );
        try {
            return builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Beendet diesen ProcessExecutor, indem dem Kommunikator das Signal zum Beenden gegeben wird:
     * {@link RMICommunicator#dispose()}. Der Prozess beendet sich anschließend selbst.
     */
    public void dispose() {
        if (!isDisposed()) {
            process.onExit().thenRun(this::setDisposed);
        }
        if (!communicator.isDisposed()) {
            communicator.dispose();
        }
    }

    /**
     * Erzwingt das Beenden dieses ProcessExecutors, indem der Prozess mit {@link Process#destroyForcibly()} terminiert
     * wird.
     */
    public void forceDispose() {
        if (isDisposed()) {
            return;
        }
        process.onExit().thenRun(this::setDisposed);
        process.destroyForcibly();
    }
}
