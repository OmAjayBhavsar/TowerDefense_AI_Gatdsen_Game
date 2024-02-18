package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.LocalPlayerHandler;
import com.gatdsen.manager.player.*;
import com.gatdsen.networking.ProcessPlayerHandler;

import java.lang.reflect.InvocationTargetException;

public final class LocalPlayerHandlerFactory extends PlayerHandlerFactory {

    public static final LocalPlayerHandlerFactory HUMAN_PLAYER = new LocalPlayerHandlerFactory(HumanPlayer.class, "HumanPlayer");
    public static final LocalPlayerHandlerFactory IDLE_BOT = new LocalPlayerHandlerFactory(IdleBot.class, "IdleBot");
    public static final LocalPlayerHandlerFactory[] INTERNAL_PLAYERS = {HUMAN_PLAYER, IDLE_BOT};

    private final Class<? extends Player> playerClass;
    private final String playerName;
    private final String fileName;

    public LocalPlayerHandlerFactory(Class<? extends Player> playerClass, String fileName) {
        this.playerClass = playerClass;
        this.fileName = fileName;
        try {
            Player playerInstance = playerClass.getConstructor(new Class[]{}).newInstance();
            playerName = playerInstance.getName();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Insufficient Privileges for instantiating bots", e);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<? extends Player> getPlayerClass() {
        return playerClass;
    }

    @Override
    public String getName() {
        return playerName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public PlayerHandler createPlayerHandler(InputProcessor inputProcessor, int gameId, int playerIndex) {
        if (Bot.class.isAssignableFrom(playerClass)) {
            return new ProcessPlayerHandler(playerClass, gameId, playerIndex);
        }
        return new LocalPlayerHandler(playerClass, playerIndex, inputProcessor);
    }
}
