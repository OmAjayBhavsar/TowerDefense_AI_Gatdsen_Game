package com.gatdsen;

import com.gatdsen.manager.player.data.BotInformation;
import com.gatdsen.manager.player.data.PlayerInformation;
import com.gatdsen.manager.run.Run;
import com.gatdsen.manager.run.RunConfig;
import com.gatdsen.manager.player.handler.ProcessPlayerHandler;
import com.gatdsen.manager.run.RunResults;
import com.gatdsen.simulation.gamemode.GameModeFactory;
import org.apache.commons.cli.*;

import java.io.File;
import java.net.URISyntaxException;

public abstract class Launcher {

    protected static final Options cliOptions = new Options();

    static {
        cliOptions.addOption(Option
                .builder("?")
                .longOpt("help")
                .desc("Prints this list").build());
        cliOptions.addOption(Option
                .builder("m")
                .longOpt("map")
                .hasArg()
                .desc("String name of the map without extension").build());
        cliOptions.addOption(Option
                .builder("p")
                .longOpt("players")
                .hasArg()
                .desc("(Required for -n) Names of the bots class files without extension in format \"Bot1 Bot2 Bot3\" \n Attention: Case-sensitive!").build());
        cliOptions.addOption(Option
                .builder("g")
                .longOpt("gamemode")
                .hasArg()
                .type(Number.class)
                .desc("GameMode to be played (Default: 0)\n" +
                        "  0 - Normal\n" +
                        "  1 - Exam Admission\n" +
                        "  2 - Tournament: Phase 1\n" +
                        "  xy - Campaign week x task y\n").build());
        cliOptions.addOption(Option.builder("n")
                .longOpt("nogui")
                .desc("Runs the simulation without animation").build());
        cliOptions.addOption(Option
                .builder("k")
                .longOpt("key")
                .hasArg()
                .desc("When printing results, they will be encased by the given key, ensuring authenticity").build());
        cliOptions.addOption(Option
                .builder("r")
                .longOpt("record")
                .desc("Saves replay and results of the matches").build());
        cliOptions.addOption(Option
                .builder("replay")
                .hasArg()
                .desc("Playes ").build());
    }

    protected static CommandLine getParamsFromArgs(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine params;
        try {
            params = parser.parse(cliOptions, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelp();
            return null;
        }
        return params;
    }

    protected static RunConfig parseRunConfig(CommandLine params) {
        RunConfig runConfig = new RunConfig();
        runConfig.gui = !params.hasOption("n");
        if (params.hasOption("r")) {
            runConfig.replay = true;
        }
        String gameModeIdentifier;
        if (params.hasOption("replay")) {
            gameModeIdentifier = "replay";
        } else {
            gameModeIdentifier = params.getOptionValue("g", "admission");
        }
        runConfig.gameMode = GameModeFactory.getInstance().getGameMode(gameModeIdentifier);
        if (runConfig.gameMode == null) {
            System.err.println("\"" + gameModeIdentifier + "\" is not a valid game mode.");
            printHelp();
            return null;
        }
        runConfig.gameMode.parseFromCommandArguments(params);
        return runConfig;
    }

    protected static void printHelp() {
        String header = "\n\n";
        String footer = "\nPlease report issues at wettbewerb@acagamics.de";

        HelpFormatter formatter = new HelpFormatter();
        File currentJar;
        try {
            currentJar = new File(ProcessPlayerHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        formatter.printHelp("java -jar " + currentJar.getName(), header, cliOptions, footer, true);
    }

    protected static void printResults(Run run, String key) {
        RunResults results = run.getResults();
        PlayerInformation[] playerInformation = results.getPlayerInformation();
        float[] scores = results.getScores();
        StringBuilder builder = new StringBuilder();
        if (key != null && !key.isEmpty()) {
            builder.append("<").append(key).append(">");
        }
        switch (run.getGameModeName()) {
            case "CampaignMode":
                if (scores[0] > 0) {
                    builder.append("passed");
                } else {
                    builder.append("failed");
                }
                break;
            case "ExamAdmissionMode":
                StringBuilder scoreBuilder = new StringBuilder();
                appendResults(scoreBuilder, playerInformation, scores);
                System.out.println(scoreBuilder);
                if (scores[0] >= 420) {
                    builder.append("passed");
                } else {
                    builder.append("failed");
                }
                break;
            case "ReplayMode":
                if (!results.getConfig().gui) {
                    builder.append("\nStarted a replay without GUI, so only the results will be printed: \n");
                }
            case "NormalMode":
            case "TournamentPhase1Mode":
            default:
                appendResults(builder, playerInformation, scores);
                builder.append("\n");
                break;
        }
        if (key != null && !key.isEmpty()) {
            builder.append("<").append(key).append(">");
        }
        System.out.println(builder);
    }

    private static void appendResults(StringBuilder builder, PlayerInformation[] playerInformation, float[] scores) {
        builder.append("\nScores:\n");
        for (int i = 0; i < playerInformation.length; i++) {
            if (playerInformation[i] instanceof BotInformation) {
                BotInformation botInformation = (BotInformation) playerInformation[i];
                builder.append(String.format("%-10s (%s, %d)", botInformation.getName(), botInformation.getStudentName(), botInformation.getMatrikel()));
            } else {
                builder.append(String.format("%-10s", playerInformation[i].getName()));
            }
            builder.append(String.format(" :  %-6f%n", scores[i]));
        }
    }
}
