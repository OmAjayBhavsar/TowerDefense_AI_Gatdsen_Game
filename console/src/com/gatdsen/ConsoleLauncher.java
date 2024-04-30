package com.gatdsen;

import com.gatdsen.manager.Manager;
import com.gatdsen.manager.run.Run;
import com.gatdsen.manager.run.RunConfig;
import org.apache.commons.cli.CommandLine;

public final class ConsoleLauncher extends Launcher {

    public static void main(String[] args) {
        System.out.println("Running GaTDsen Version: " + ConsoleLauncher.class.getPackage().getImplementationVersion());
        CommandLine params = getParamsFromArgs(args);
        if (params == null) {
            return;
        }
        RunConfig runConfig = parseRunConfig(params);
        if (runConfig == null) {
            return;
        }
        if (runConfig.gui) {
            throw new RuntimeException("This jar version does not support running the game with GUI.");
        }
        Manager.setSystemReservedProcessorCount(1);
        Manager manager = Manager.getManager();
        Run run = manager.startRun(runConfig);
        if (run == null) {
            printHelp();
            return;
        }
        Object lock = new Object();
        synchronized (lock) {
            run.addCompletionListener((tmp) -> {
                synchronized (lock) {
                    lock.notify();
                }
            });
            try {
                lock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        printResults(run, params.getOptionValue("k", ""));
        manager.dispose();
    }
}