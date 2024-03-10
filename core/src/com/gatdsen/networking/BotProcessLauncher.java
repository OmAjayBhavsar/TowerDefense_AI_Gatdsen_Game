package com.gatdsen.networking;

import com.gatdsen.manager.concurrent.ProcessExecutor;
import com.gatdsen.manager.concurrent.RMIRegistry;
import com.gatdsen.manager.concurrent.ResourcePool;
import com.gatdsen.networking.rmi.PlayerProcessCommunicator;
import com.gatdsen.networking.rmi.ProcessCommunicator;
import org.apache.commons.cli.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Diese Klasse enthält die main-Methode, welche zum Start eines Bot-Prozesses verwendet wird.
 */
public class BotProcessLauncher {

    private static final Options cliOptions = new Options();
    static {
        cliOptions.addOption(Option
                .builder("?")
                .longOpt("help")
                .desc("Prints this list").build());

        cliOptions.addOption(Option
                .builder("host")
                .hasArg()
                .desc("Host of the Java RMI Remote Registry").build());

        cliOptions.addOption(Option
                .builder("port")
                .hasArg()
                .desc("Port of the Java RMI Remote Registry").build());
    }

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine params;
        try {
            for (String arg : args
            ) {
                if (arg.equals("-?") || arg.equals("--help")) {
                    printHelp();
                    return;
                }
            }

            params = parser.parse(cliOptions, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp();
            return;
        }

        String host = null;
        if (params.hasOption("host")) {
            host = params.getOptionValue("host").trim();
        }
        Integer port = null;
        if (params.hasOption("port")) {
            try {
                port = Integer.parseInt(params.getOptionValue("port").trim());
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + params.getOptionValue("port").trim());
                printHelp();
                return;
            }
        }

        RMIRegistry registry = ResourcePool.getInstance().requestRegistry(host, port);
        String localReferenceName = String.format(
                ProcessExecutor.PLAYER_PROCESS_REFERENCE_NAME_FORMAT,
                ProcessHandle.current().pid()
        );
        PlayerProcessCommunicator localCommunicatorObject = new PlayerProcessCommunicator(registry, localReferenceName);
        try {
            registry.rebind(localReferenceName, UnicastRemoteObject.exportObject(localCommunicatorObject, 0));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        String remoteReferenceName = String.format(
                ProcessExecutor.GAME_PROCESS_REFERENCE_NAME_FORMAT,
                ProcessHandle.current().pid()
        );
        ProcessCommunicator gameCommunicatorStub = null;
        long timeout = 5_000;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime <= timeout) {
            try {
                gameCommunicatorStub = (ProcessCommunicator) registry.lookup(remoteReferenceName);
                break;
            } catch (NotBoundException ignored) {
            } catch (RemoteException e) {
                throw new RuntimeException("The connection with the Remote Object Registry at host \"" + (host == null ? "localhost" : host) + "\" and port \"" + port + "\" failed.", e);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (gameCommunicatorStub == null) {
            throw new RuntimeException("There was no Remote Reference bound under the name \"" + remoteReferenceName + "\" at the Remote Object Registry at host \"" + (host == null ? "localhost" : host) + "\" and port \"" + port + "\".");
        }
        localCommunicatorObject.setRemoteCommunicatorStub(gameCommunicatorStub);
    }

    private static void printHelp() {
        String header = "\n\n";
        String footer = "\nPlease report issues at wettbewerb@acagamics.de";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar example-1.0.0.jar", header, cliOptions, footer, true);
    }
}
