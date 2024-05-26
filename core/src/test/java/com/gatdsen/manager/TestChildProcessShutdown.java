package com.gatdsen.manager;

import com.gatdsen.manager.concurrent.ProcessExecutor;
import com.gatdsen.manager.concurrent.ResourcePool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

public class TestChildProcessShutdown {

    private static ProcessBuilder processBuilder;
    private static File currentJar;

    //@BeforeClass
    public static void setupProcessBuilder() {
        processBuilder = new ProcessBuilder();
        processBuilder.inheritIO();
        try {
            currentJar = new File(ResourcePool.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }
    }

    private void setProcessBuilderMainParams(String param) {
        System.out.println(currentJar.getPath());
        processBuilder.command(
                "java", "-cp", currentJar.getPath(), TestChildProcessShutdown.class.getName(),
                param
        );
    }

    //@Test
    public void testChildProcessShutdown() {
        setProcessBuilderMainParams("exit");
        Process process;
        try {
            process = processBuilder.start();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            return;
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
            return;
        }
        process.children().forEach(child -> {
            if (child.isAlive()) {
                Assert.fail("Child process still running: " + child);
            }
        });
    }

    //@Test
    public void testChildProcessShutdown2() {
        setProcessBuilderMainParams("block");
        Process process;
        try {
            process = processBuilder.start();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            return;
        }
        process.destroy();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
            return;
        }
        process.children().forEach(child -> {
            if (child.isAlive()) {
                Assert.fail("Child process still running: " + child);
            }
        });
    }

    //@Test
    public void testChildProcessShutdown3() {
        setProcessBuilderMainParams("block");
        Process process;
        try {
            process = processBuilder.start();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
            return;
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
            return;
        }
        process.children().forEach(child -> {
            System.out.println("Child process: " + child);
        });
        process.destroyForcibly();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
            return;
        }
        process.children().forEach(child -> {
            if (child.isAlive()) {
                Assert.fail("Child process still running: " + child);
            }
            System.out.println("Child process exited: " + child);
        });
    }

    public static void main(String[] args) {
        ProcessExecutor executor = ResourcePool.getInstance().requestProcessExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ResourcePool.getInstance().dispose()));
        if (args.length == 1 && args[0].equals("exit")) {
            System.out.println("Exiting...");
            ResourcePool.getInstance().dispose();
            System.out.println("Exiting...");
        } else {
            try {
                executor.waitForDispose();
            } catch (InterruptedException ignored) {
            }
        }
    }
}
