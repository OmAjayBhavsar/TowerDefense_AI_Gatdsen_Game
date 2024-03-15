package com.gatdsen.manager;

import java.util.concurrent.TimeUnit;

/**
 * Eine simple Klasse, die genutzt werden kann, um die Zeit, die seit der Initialisierung vergangen ist, sowie die
 * bis zu einem bestimmten Zeitpunkt verbleibende Zeit zu berechnen.
 * Diese Klasse verwendet intern {@link System#nanoTime()} und bietet daher das gleiche Level an Präzision an
 * Nanosekunden, wie die Methode {@link System#nanoTime()}.
 */
public final class Timer {

    /** Die Zeit, zu der der Timer initialisiert wurde, in Nanosekunden */
    private final long startTime;
    /** Die Dauer des Timers in Nanosekunden */
    private final long duration;

    /**
     * @param duration Die Dauer des Timers in Millisekunden
     */
    Timer(long duration) {
        this(duration, TimeUnit.MILLISECONDS);
    }

    /**
     * @param duration Die Dauer des Timers
     * @param unit Die Einheit der Dauer
     */
    Timer(long duration, TimeUnit unit) {
        this.duration = unit.toNanos(duration);
        startTime = System.nanoTime();
    }

    /**
     * @return Die Zeit, die seit der Initialisierung des Timers vergangen ist, in Nanosekunden
     */
    public long getElapsedNanoSeconds() {
        return System.nanoTime() - startTime;
    }

    /**
     *
     * @param unit
     * @return
     */
    public long getElapsedTime(TimeUnit unit) {
        return unit.convert(getElapsedNanoSeconds(), TimeUnit.NANOSECONDS);
    }

    public long getRemainingNanoSeconds() {
        return duration - getElapsedNanoSeconds();
    }

    public long getRemainingTime(TimeUnit unit) {
        return unit.convert(getRemainingNanoSeconds(), TimeUnit.NANOSECONDS);
    }

    public long getRemainingNanoSecondsUntilDisqualification() {
        return (2 * duration) - getElapsedNanoSeconds();
    }

    public long getRemainingTimeUntilDisqualification(TimeUnit unit) {
        return unit.convert(getRemainingNanoSecondsUntilDisqualification(), TimeUnit.NANOSECONDS);
    }
}
