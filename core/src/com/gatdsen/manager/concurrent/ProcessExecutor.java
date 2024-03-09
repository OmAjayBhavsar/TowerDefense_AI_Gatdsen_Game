package com.gatdsen.manager.concurrent;

import com.gatdsen.networking.BotProcessLauncher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public final class ProcessExecutor {

    public static final String GAME_PROCESS_REFERENCE_NAME_FORMAT = "GameProcessCommunicator_%d";
    public static final String PLAYER_PROCESS_REFERENCE_NAME_FORMAT = "PlayerProcessCommunicator_%d";

    public final Process process;
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

    private static Process createProcess(String registryHost, int registryPort) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.inheritIO();
        File currentJar;
        try {
            currentJar = new File(ProcessExecutor.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        builder.command(
                // Starten der JVM in der main() vom BotProcessLauncher
                "java", "-cp", currentJar.getPath(), BotProcessLauncher.class.getName(),
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

    public void dispose() {
        communicator.dispose();
        try {
            process.waitFor();
        } catch (InterruptedException ignored) {
            // Wenn wir beim Warten auf das Ende des Prozesses unterbrochen worden, starten wir die Terminierung dieses
            // Prozesses manuell, bevor wir diese Methode verlassen.
            process.destroy();
        }
    }
}
