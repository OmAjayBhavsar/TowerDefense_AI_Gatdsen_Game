package com.gatdsen.manager;

import java.util.concurrent.TimeUnit;

public final class Timer {

    private final long startTime;
    private final long duration;

    public Timer(long durationNanoSeconds) {
        this.duration = durationNanoSeconds;
        startTime = System.nanoTime();
    }

    public long getElapsedNanoSeconds() {
        return System.nanoTime() - startTime;
    }

    public long getElapsedTime(TimeUnit unit) {
        return unit.convert(getElapsedNanoSeconds(), TimeUnit.NANOSECONDS);
    }

    public long getRemainingNanoSeconds() {
        return duration - getElapsedNanoSeconds();
    }

    public long getRemainingTime(TimeUnit unit) {
        return unit.convert(getRemainingNanoSeconds(), TimeUnit.NANOSECONDS);
    }
}
