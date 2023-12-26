package com.gatdsen;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gatdsen.manager.*;
import com.gatdsen.manager.run.config.RunConfiguration;
import com.gatdsen.ui.GADS;
import org.apache.commons.cli.CommandLine;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public final class DesktopLauncher extends Launcher {

    public static void main(String[] args) {
        CommandLine params = getParamsFromArgs(args);
        if (params == null) {
            return;
        }
        RunConfiguration runConfig = parseRunConfiguration(params);
        if (runConfig == null) {
            return;
        }
        if (runConfig.gui) {
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setForegroundFPS(60);
            config.setTitle("Gadsen: Tower Defense");
            config.setWindowIcon(Files.FileType.Classpath, "icon/new_icon.png");
            new Lwjgl3Application(new GADS(runConfig), config);
        } else {
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
        }
    }
}
