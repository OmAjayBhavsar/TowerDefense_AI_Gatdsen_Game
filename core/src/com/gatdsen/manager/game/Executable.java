package com.gatdsen.manager.game;

import com.gatdsen.manager.AnimationLogProcessor;
import com.gatdsen.manager.CompletionHandler;
import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.Manager;

import java.util.ArrayList;

public abstract class Executable {

    public static final int REQUIRED_THREAD_COUNT = 2;

    public enum Status {
        INITIALIZED,
        SCHEDULED,
        ACTIVE,
        PAUSED,
        COMPLETED,
        ABORTED
    }

    public final Object schedulingLock = new Object();

    private Status status = Status.INITIALIZED;

    protected final ArrayList<CompletionHandler<Executable>> completionListeners = new ArrayList<>();

    protected final InputProcessor inputGenerator;

    protected final AnimationLogProcessor animationLogProcessor;

    protected final boolean gui;
    protected boolean pendingShutdown = false;

    protected GameConfig config;
    protected final boolean saveReplay;


    protected Executable(GameConfig config) {
        this.config = config;
        gui = config.gui;
        saveReplay = config.replay;
        animationLogProcessor = config.animationLogProcessor;
        inputGenerator = config.inputProcessor;
    }


    public abstract void start() throws Exception;

    protected void setStatus(Status newStatus) {
        status = newStatus;
    }



    public void addCompletionListener(CompletionHandler<Executable> handler) {
        completionListeners.add(handler);
        if (status == Status.COMPLETED) handler.onComplete(this);
    }

    public void dispose() {
        pendingShutdown = true;
    }

    public Status getStatus() {
        return status;
    }

    public void schedule() {
        synchronized (schedulingLock) {
            if (status == Status.INITIALIZED)
                setStatus(Status.SCHEDULED);
        }
    }

    protected void crashHandler(Thread thread, Throwable throwable) {
        System.err.println("Error in game: " + this);
        System.err.println("Error in thread: " + thread);
        throwable.printStackTrace();
        System.err.println("Game crashed during execution\nIf you see this message, please forward all console logs to wettbewerb@acagamics.de");
        Manager.getManager().stop(this);
    }

    public void pause() {
        synchronized (schedulingLock) {
            if (status == Status.ACTIVE)
                setStatus(Status.PAUSED);
        }
    }

    public void resume() {
        synchronized (schedulingLock) {
            if (status != Status.PAUSED) return;
            setStatus(Status.ACTIVE);
            schedulingLock.notify();
        }
    }

    public void abort() {
        synchronized (schedulingLock) {
            setStatus(Status.ABORTED);
            dispose();
        }
    }

    public abstract boolean shouldSaveReplay();

    public abstract GameResults getGameResults();

    @Override
    public String toString() {
        return "Executable{" +
                "status=" + status +
                ", completionListeners=" + completionListeners +
                ", inputGenerator=" + inputGenerator +
                ", animationLogProcessor=" + animationLogProcessor +
                ", gui=" + gui +
                ", pendingShutdown=" + pendingShutdown +
                ", config=" + config +
                '}';
    }
}
