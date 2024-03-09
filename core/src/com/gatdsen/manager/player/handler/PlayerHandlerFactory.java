package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.player.*;
import com.gatdsen.simulation.PlayerController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

public abstract class PlayerHandlerFactory {

    public abstract String getName();

    public abstract Future<PlayerHandler> createPlayerHandler(int playerIndex, PlayerController controller, InputProcessor inputProcessor);

    public static PlayerHandlerFactory[] getAvailablePlayerFactories() {
        List<PlayerHandlerFactory> factories = new ArrayList<>();
        factories.add(LocalPlayerHandlerFactory.HUMAN_PLAYER);
        factories.add(LocalPlayerHandlerFactory.IDLE_BOT);
        File botDir = new File("bots");
        if (botDir.exists()) {
            try (URLClassLoader loader = getURLClassLoader()) {
                for (File botFile : Objects.requireNonNull(botDir.listFiles(pathname -> pathname.getName().endsWith(".class")))) {
                    Class<?> nextClass = loadClass(loader, botFile);
                    if (nextClass == null || !Bot.class.isAssignableFrom(nextClass)) {
                        continue;
                    }
                    // Der folgende Cast ist sicher, da vorheriges if-Statement sicherstellt, dass nextClass eine
                    // Instanz von Bot (und damit eine Unterklasse von Player) ist.
                    @SuppressWarnings("unchecked")
                    Class<? extends Player> castedClass = (Class<? extends Player>) nextClass;
                    factories.add(new LocalPlayerHandlerFactory(
                            castedClass,
                            botFile.getName().replace(".class", ""))
                    );
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("Warning: No bot directory found at " + botDir.getAbsolutePath());
        }
        factories.add(new RemotePlayerHandlerFactory());
        return factories.toArray(new PlayerHandlerFactory[0]);
    }

    private static URLClassLoader getURLClassLoader() {
        try {
            return new URLClassLoader(new URL[]{new File(".").toURI().toURL()});
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> loadClass(ClassLoader loader, File file) {
        try {
            return loader.loadClass("bots." + file.getName().replace(".class", ""));
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find class for " + file.getName());
        } catch (UnsupportedClassVersionError e) {
            System.err.println("Class " + file.getName() + " is compiled for a newer Java version than the current one.");
        }
        return null;
    }

    public static PlayerHandlerFactory[] getPlayerFactories(String[] fileNames) {
        PlayerHandlerFactory[] factories = new PlayerHandlerFactory[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            factories[i] = getPlayerFactory(fileNames[i]);
        }
        return factories;
    }

    public static PlayerHandlerFactory getPlayerFactory(String fileName) {
        for (LocalPlayerHandlerFactory factory : LocalPlayerHandlerFactory.INTERNAL_PLAYERS) {
            if (factory.getFileName().equals(fileName)) {
                return factory;
            }
        }
        File botFile = new File("bots/" + fileName + ".class");
        if (botFile.exists()) {
            try (URLClassLoader loader = getURLClassLoader()) {
                Class<?> nextClass = loadClass(loader, botFile);
                if (nextClass == null || !Bot.class.isAssignableFrom(nextClass)) {
                    throw new RuntimeException("Couldn't find bots." + fileName + ".class");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (fileName.equals("Remote Player") || fileName.equals("RemotePlayer")) {
            return new RemotePlayerHandlerFactory();
        }
        throw new RuntimeException("Couldn't find bots." + fileName + ".class");
    }
}
