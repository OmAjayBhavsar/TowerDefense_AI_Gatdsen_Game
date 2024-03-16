package com.gatdsen;

import com.gatdsen.manager.player.data.BotInformation;
import com.gatdsen.manager.player.data.PlayerInformation;
import com.gatdsen.manager.run.Run;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.manager.run.RunConfig;
import com.gatdsen.manager.player.handler.ProcessPlayerHandler;
import com.gatdsen.manager.run.RunResults;
import com.gatdsen.simulation.GameState.GameMode;
import org.apache.commons.cli.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
                .desc("(Required for -n) String name of the map without extension").build());
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
                        "  1 - Campaign\n" +
                        "  2 - Exam Admission\n" +
                        "  3 - Tournament: Phase 1\n" +
                        "  4 - Tournament: Phase 2").build());
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
                .longOpt("replay")
                .desc("Saves replay and results of the matches (WIP)").build());
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
        runConfig.mapName = params.getOptionValue("m", null);
        if (params.hasOption("p")) {
            runConfig.playerFactories = new ArrayList<>(List.of(PlayerHandlerFactory.getPlayerFactories(params.getOptionValue("p").trim().split("\\s+"))));
        }
        if (params.hasOption("r")) {
            runConfig.replay = true;
        }
        int gameMode = Integer.parseInt(params.getOptionValue("g", "0"));
        GameMode[] gameModes = GameMode.values();
        if (gameMode < 0 || gameMode >= gameModes.length) {
            System.err.println("Valid GameModes range from 0 to " + gameModes.length);
            printHelp();
            return null;
        }
        runConfig.gameMode = gameModes[gameMode];
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
        switch (run.getGameMode()) {
            case Campaign:
                if (scores[0] > 0) {
                    builder.append("passed");
                } else {
                    builder.append("failed");
                }
                break;
            case Exam_Admission:
                StringBuilder scoreBuilder = new StringBuilder();
                appendResults(scoreBuilder, playerInformation, scores);
                System.out.println(scoreBuilder);
                if (scores[0] >= 420) {
                    builder.append("passed");
                } else {
                    builder.append("failed");
                }
                break;
            case Replay:
                if (!results.getConfig().gui) {
                    builder.append("\nStarted a replay without GUI, so only the results will be printed: \n");
                }
            case Normal:
            case Tournament_Phase_1:
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
