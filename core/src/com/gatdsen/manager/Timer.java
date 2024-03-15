package com.gatdsen.manager;

import java.util.concurrent.TimeUnit;

/**
 * Eine simple Klasse, die genutzt werden kann, um die Zeit, die seit Rundenbeginn vergangen ist, oder die bis zum
 * Rundenende verbliebene Zeit zu erhalten. <br>
 * Diese Klasse verwendet intern {@link System#nanoTime()} und bietet daher das gleiche Level an Präzision an
 * Nanosekunden, wie die Methode {@link System#nanoTime()}.
 */
public final class Timer {

    /** Die Dauer des Timers in Nanosekunden */
    private final long duration;
    /** Die Startzeit des Timers in Nanosekunden */
    private final long startTime;

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
     * @return Gibt die Zeit, die seit Rundenbeginn vergangen ist, in Nanosekunden zurück
     */
    public long getElapsedNanoSeconds() {
        return System.nanoTime() - startTime;
    }

    /**
     * @param unit Die Einheit, in der die vergangene Zeit zurückgegeben werden soll
     * @return Gibt die Zeit, die seit Rundenbeginn vergangen ist, in der angegebenen Einheit zurück
     */
    public long getElapsedTime(TimeUnit unit) {
        return unit.convert(getElapsedNanoSeconds(), TimeUnit.NANOSECONDS);
    }

    /**
     * Gibt die verbleibende Zeit bis zum Rundenende in Nanosekunden zurück. <br>
     * Sollte diese Zeit abgelaufen sein, muss man damit rechnen, dass man aufgrund der längeren Berechnungszeit eine
     * Runde aussetzen muss.
     * @return Die verbleibende Zeit bis zum Rundenende in Nanosekunden
     */
    public long getRemainingNanoSeconds() {
        return duration - getElapsedNanoSeconds();
    }

    /**
     * Gibt die verbleibende Zeit bis zum Rundenende in der angegebenen Einheit zurück. <br>
     * Sollte diese Zeit abgelaufen sein, muss man damit rechnen, dass man aufgrund der längeren Berechnungszeit eine
     * Runde aussetzen muss.
     * @param unit Die Einheit, in der die verbleibende Zeit zurückgegeben werden soll
     * @return Die verbleibende Zeit bis zum Rundenende in der angegebenen Einheit
     */
    public long getRemainingTime(TimeUnit unit) {
        return unit.convert(getRemainingNanoSeconds(), TimeUnit.NANOSECONDS);
    }

    /**
     * Gibt die verbleibende Zeit bis zum endgültigen Rundenende in Nanosekunden zurück. <br>
     * Diese Methode beschreibt eine doppelt so lange Zeitperiode wie es {@link #getRemainingNanoSeconds()} tut. <br>
     * Sollte auch diese Zeit abgelaufen sein, muss man damit rechnen, dass man aufgrund der längeren
     * Berechnungszeit disqualifiziert wird.
     * @return Die verbleibende Zeit bis zum endgültigen Rundenende in Nanosekunden
     */
    public long getRemainingNanoSecondsUntilDisqualification() {
        return (2 * duration) - getElapsedNanoSeconds();
    }

    /**
     * Gibt die verbleibende Zeit bis zum endgültigen Rundenende in der angegebenen Einheit zurück. <br>
     * Diese Methode beschreibt eine doppelt so lange Zeitperiode wie es {@link #getRemainingTime(TimeUnit)} tut. <br>
     * Sollte auch diese Zeit abgelaufen sein, muss man damit rechnen, dass man aufgrund der längeren
     * Berechnungszeit disqualifiziert wird.
     * @return Die verbleibende Zeit bis zum endgültigen Rundenende in der angegebenen Einheit
     */
    public long getRemainingTimeUntilDisqualification(TimeUnit unit) {
        return unit.convert(getRemainingNanoSecondsUntilDisqualification(), TimeUnit.NANOSECONDS);
    }
}
