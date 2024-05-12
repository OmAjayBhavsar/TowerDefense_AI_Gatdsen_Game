package com.gatdsen.manager.game;

import com.gatdsen.manager.CompletionHandler;
import com.gatdsen.manager.player.data.PlayerInformation;
import com.gatdsen.manager.replay.Replay;
import com.gatdsen.manager.replay.ReplayException;
import com.gatdsen.manager.replay.ReplayRetriever;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.action.ActionLog;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This is more a workaround, ReplayGame and Game should really be implementing a common Interface
 */
public class ReplayGame extends Executable {

    private Replay replay;
    private Thread executionThread;

    public ReplayGame(GameConfig config) {
        super(config);
    }

    @Override
    public void start() throws ReplayException {
        synchronized (schedulingLock) {
            if (getStatus() == Status.ABORTED) {
                return;
            }
            setStatus(Status.ACTIVE);
            replay = ReplayRetriever.getInstance().loadReplay(config.gameMode.getMap());
            //Init the Log Processor
            if (config.gui) {
                animationLogProcessor.init(
                        replay.getGameResults().getInitialState().copy(),
                        Arrays.stream(replay.getGameResults().getPlayerInformation()).map(PlayerInformation::getName).toArray(String[]::new),
                        getSkins()
                );
            }
            //Run the Game
            executionThread = new Thread(this::run);
            executionThread.setName("Replay_Execution_Thread");
            executionThread.setUncaughtExceptionHandler(this::crashHandler);
            executionThread.start();
        }
    }

    private void run() {
        if (config.gui) {
            Iterator<ActionLog> actionLogs = replay.getGameResults().getActionLogs().iterator();
            while (!pendingShutdown && actionLogs.hasNext()) {
                synchronized (schedulingLock) {
                    if (getStatus() == Status.PAUSED) {
                        try {
                            schedulingLock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                animationLogProcessor.animate(actionLogs.next());
                animationLogProcessor.awaitNotification();
            }
        }
        if (pendingShutdown) {
            return;
        }
        setStatus(Status.COMPLETED);
        for (CompletionHandler<Executable> completionListener : completionListeners) {
            completionListener.onComplete(this);
        }
    }

    private String[][] getSkins() {
        return replay.getGameResults().getSkins();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (executionThread != null) {
            executionThread.interrupt();
        }
        executionThread = null;
    }

    public boolean shouldSaveReplay() {
        return false;
    }

    @Override
    public GameResults getGameResults() {
        return replay.getGameResults();
    }
}
