package com.gatdsen.manager;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Diese Klasse testet grob die Funktionalität der {@link Timer} Klasse.
 */
public final class TimerTest {

    private final long duration = 100;
    private final long timeout = 50;

    @Test
    public void testElapsedTime() {
        Timer timer = new Timer(duration);
        long elapsed = timer.getElapsedNanoSeconds();
        Assert.assertTrue(
                "Elapsed time in milliseconds should not be negative but got: " + elapsed + "ns",
                elapsed >= 0
                );
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            Assert.fail("Thread.sleep() was interrupted: " + e);
        }
        elapsed = timer.getElapsedTime(TimeUnit.MILLISECONDS);
        Assert.assertTrue(
                "Elapsed time in milliseconds should be at least " + timeout + "ms after waiting this amount of time but got: " + elapsed + "ms",
                elapsed >= timeout
        );
    }

    @Test
    public void testRemainingTime() {
        Timer timer = new Timer(duration);
        long remaining = timer.getRemainingTime(TimeUnit.MILLISECONDS);
        Assert.assertTrue(
                "Remaining time in milliseconds should not be greater than the initial duration of " + duration + "ms but got: " + remaining + "ms",
                remaining <= duration
        );
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            Assert.fail("Thread.sleep() was interrupted: " + e);
        }
        remaining = timer.getRemainingTime(TimeUnit.MILLISECONDS);
        Assert.assertTrue(
                "Remaining time in milliseconds should not be greater than the initial duration of " + duration + "ms minus the timeout of " + timeout + "ms but got: " + remaining + "ms",
                remaining <= (duration - timeout)
        );
    }

    @Test
    public void testRemainingTimeUntilDisqualification() {
        Timer timer = new Timer(duration);
        long remaining = timer.getRemainingTimeUntilDisqualification(TimeUnit.MILLISECONDS);
        Assert.assertTrue(
                "Remaining time until disqualification in milliseconds should not be greater than two times the initial duration of " + duration + "ms but got: " + remaining + "ms",
                remaining <= 2 * duration
        );
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            Assert.fail("Thread.sleep() was interrupted: " + e);
        }
        remaining = timer.getRemainingTimeUntilDisqualification(TimeUnit.MILLISECONDS);
        Assert.assertTrue(
                "Remaining time until disqualification in milliseconds should not be greater than two times the initial duration of " + duration + "ms minus the timeout of " + timeout + "ms but got: " + remaining + "ms",
                remaining <= (2 * duration - timeout)
        );
    }

    @Test
    public void testRemainingTimeUntilTimeout() {
        Timer timer = new Timer(duration);
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Assert.fail("Thread.sleep() was interrupted: " + e);
        }
        long remaining = timer.getRemainingNanoSeconds();
        Assert.assertTrue(
                "Remaining time in milliseconds should not be greater than 0ms after waiting for the timer duration of " + duration + "ms but got: " + remaining + "ms",
                remaining <= 0
        );
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Assert.fail("Thread.sleep() was interrupted: " + e);
        }
        remaining = timer.getRemainingNanoSecondsUntilDisqualification();
        Assert.assertTrue(
                "Remaining time until disqualification in milliseconds should not be greater than 0ms after waiting for two times for the timer duration of " + duration + "ms but got: " + remaining + "ms",
                remaining <= 0
        );
    }
}
