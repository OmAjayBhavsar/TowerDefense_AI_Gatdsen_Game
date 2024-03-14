package com.gatdsen.manager;

import com.gatdsen.manager.command.Command;
import com.gatdsen.manager.concurrent.ResourcePool;
import com.gatdsen.manager.concurrent.ThreadExecutor;
import com.gatdsen.manager.player.HumanPlayer;
import com.gatdsen.manager.player.analyzer.PlayerClassAnalyzer;
import com.gatdsen.manager.player.Bot;
import com.gatdsen.manager.player.Player;
import com.gatdsen.manager.player.data.PlayerInformation;
import com.gatdsen.manager.player.data.PlayerType;
import com.gatdsen.manager.player.data.penalty.DisqualificationPenalty;
import com.gatdsen.manager.player.data.penalty.MissTurnsPenalty;
import com.gatdsen.manager.player.data.penalty.Penalty;
import com.gatdsen.simulation.GameState;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public final class PlayerExecutor {

    public static final int BOT_EXECUTE_INIT_TIMEOUT = 1000;
    public static final int BOT_EXECUTE_TURN_TIMEOUT = 500;
    private static final int BOT_CONTROLLER_USES = 200;

    public static final int HUMAN_EXECUTE_INIT_TIMEOUT = 30000;
    public static final int HUMAN_EXECUTE_TURN_TIMEOUT = 60000;
    private static final int HUMAN_CONTROLLER_USES = 100000;

    private final ThreadExecutor executor;
    private final boolean isDebug;
    private final int playerIndex;
    private final PlayerClassAnalyzer playerClassAnalyzer;
    private final Player player;
    private final InputProcessor inputGenerator;

    private boolean hasReusableExecutor = true;

    public PlayerExecutor(boolean isDebug, int playerIndex, Class<? extends Player> playerClass) {
        this(isDebug, playerIndex, playerClass, null);
    }

    public PlayerExecutor(boolean isDebug, int playerIndex, Class<? extends Player> playerClass, InputProcessor inputGenerator) {
        executor = ResourcePool.getInstance().requestThreadExecutor(2);
        this.isDebug = isDebug;
        this.playerIndex = playerIndex;
        playerClassAnalyzer = new PlayerClassAnalyzer(playerClass);
        try {
            player = (Player) playerClass.getDeclaredConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        this.inputGenerator = inputGenerator;
    }

    public PlayerClassAnalyzer getPlayerClassAnalyzer() {
        return playerClassAnalyzer;
    }

    public PlayerInformation getPlayerInformation() {
        return PlayerInformation.fromPlayer(player);
    }

    public Penalty init(GameState state, long seed) {
        if (PlayerType.fromPlayer(player) == PlayerType.HUMAN) {
            initHumanPlayer(state);
            return null;
        } else {
            return initBot(state, seed);
        }
    }

    private void initHumanPlayer(GameState state) {
        StaticGameState staticState = new StaticGameState(state, playerIndex);
        Future<?> future = executor.execute(() -> {
            Thread.currentThread().setName("Init_Thread_Player_" + player.getName());
            player.init(staticState);
        });
        try {
            if (isDebug) {
                future.get();
            } else {
                future.get(2 * HUMAN_EXECUTE_INIT_TIMEOUT, TimeUnit.MILLISECONDS);
            }
        } catch(CancellationException e) {
            System.out.println("HumanPlayer turn execution was cancelled");
            e.printStackTrace(System.err);
        } catch (InterruptedException e) {
            future.cancel(true);
            System.out.println("HumanPlayer was interrupted");
            e.printStackTrace(System.err);
        } catch (ExecutionException e) {
            System.out.println("HumanPlayer failed with exception: " + e.getCause());
            e.printStackTrace(System.err);
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("HumanPlayer computation surpassed timeout");
        }
    }

    private Penalty initBot(GameState state, long seed) {
        String[] illegalImports = playerClassAnalyzer.getIllegalImports();
        if (illegalImports.length > 0) {
            String reason = "Bot \"" + player.getName() + "\" has been disqualified for using the following illegal package or class imports: \"" + String.join("\", \"", illegalImports) + "\"";
            System.err.println(reason);
            return new DisqualificationPenalty(reason);
        }
        StaticGameState staticState = new StaticGameState(state, playerIndex);
        Future<?> future = executor.execute(() -> {
            Thread.currentThread().setName("Init_Thread_Player_" + player.getName());
            ((Bot) player).setRandomSeed(seed);
            player.init(staticState);
        });
        long startTime = System.currentTimeMillis();
        try {
            if (isDebug) {
                future.get();
            } else {
                future.get(2 * BOT_EXECUTE_INIT_TIMEOUT, TimeUnit.MILLISECONDS);
            }
        } catch(CancellationException e) {
            e.printStackTrace(System.err);
            System.out.println("Bot \"" + player.getName() + "\" turn execution was cancelled");
        } catch (InterruptedException e) {
            future.cancel(true);
            e.printStackTrace(System.err);
            System.out.println("Bot \"" + player.getName() + "\" was interrupted");
        } catch (ExecutionException e) {
            e.getCause().printStackTrace(System.err);
            String reason = "Bot \"" + player.getName() + "\" has to miss the next turn for failing with exception: " + e.getCause();
            System.err.println(reason);
            return new MissTurnsPenalty(reason);
        } catch (TimeoutException e) {
            future.cancel(true);
            hasReusableExecutor = false;
            String reason = "Bot \"" + player.getName() + "\" has to miss the next turn for surpassing computation timeout by taking more than " + (2 * BOT_EXECUTE_INIT_TIMEOUT) + "ms!";
            System.err.println(reason);
            return new DisqualificationPenalty(reason);
        }
        long duration = System.currentTimeMillis() - startTime;
        if (!isDebug && duration > BOT_EXECUTE_INIT_TIMEOUT) {
            String reason = "Bot \"" + player.getName() + "\" has to miss the next turn for surpassing computation timeout by taking " + (duration - BOT_EXECUTE_INIT_TIMEOUT) + "ms longer than allowed!";
            return new MissTurnsPenalty(reason);
        }
        return null;
    }

    public Future<?> executeTurn(GameState state, Command.CommandHandler commandHandler) {
        if (PlayerType.fromPlayer(player) == PlayerType.HUMAN) {
            return executeHumanPlayerTurn(state, commandHandler);
        } else {
            return executeBotTurn(state, commandHandler);
        }
    }

    private Future<?> executeHumanPlayerTurn(GameState state, Command.CommandHandler commandHandler) {
        StaticGameState staticState = new StaticGameState(state, playerIndex);
        Controller controller = new Controller(HUMAN_CONTROLLER_USES);
        CompletableFuture<?> future = executor.executeCompletable(() -> {
            Thread.currentThread().setName("Run_Thread_Player_" + player.getName());
            player.executeTurn(staticState, controller);
        });
        return executor.execute(() -> {
            inputGenerator.activateTurn((HumanPlayer) player, playerIndex);
            Thread.currentThread().setName("Future_Executor_Player_" + player.getName());
            long endTime = waitForFutureWhileHandlingCommands(System.currentTimeMillis() + HUMAN_EXECUTE_TURN_TIMEOUT, future, controller.commands, commandHandler);
            if (endTime == -1) {
                future.cancel(true);
                System.out.println("HumanPlayer computation surpassed timeout");
            } else {
                assert future.isDone() : "Future should be done when waitForFutureWhileHandlingCommands() returns a valid end time!";
                try {
                    future.get();
                } catch(CancellationException e) {
                    System.out.println("HumanPlayer turn execution was cancelled");
                    e.printStackTrace(System.err);
                } catch (InterruptedException e) {
                    future.cancel(true);
                    System.out.println("HumanPlayer was interrupted");
                    e.printStackTrace(System.err);
                } catch (ExecutionException e) {
                    System.out.println("HumanPlayer failed with exception: " + e.getCause());
                    e.printStackTrace(System.err);
                }
            }
            controller.endTurn();
            // Falls in der waitWhileHandlingCommands()-Methode nicht alle Befehle abgearbeitet wurden, holen wir das
            // hier nach und verarbeiten die in der Controller-Warteschlange verbliebenen Befehle.
            List<Command> commands = new LinkedList<>();
            int count = controller.commands.drainTo(commands);
            if (count > 0) {
                commandHandler.handle(commands);
            }
        });
    }

    private Future<?> executeBotTurn(GameState state, Command.CommandHandler commandHandler) {
        StaticGameState staticState = new StaticGameState(state, playerIndex);
        Controller controller = new Controller(BOT_CONTROLLER_USES);
        CompletableFuture<?> future = executor.executeCompletable(() -> {
            Thread.currentThread().setName("Run_Thread_Player_" + player.getName());
            player.executeTurn(staticState, controller);
        });
        return executor.execute(() -> {
            Thread.currentThread().setName("Future_Executor_Player_" + player.getName());
            long startTime = System.currentTimeMillis();
            long endTime = waitForFutureWhileHandlingCommands(startTime + 2 * BOT_EXECUTE_TURN_TIMEOUT, future, controller.commands, commandHandler);
            Penalty penalty = null;
            if (endTime == -1) {
                future.cancel(true);
                hasReusableExecutor = false;
                String reason = "Bot \"" + player.getName() + "\" has to miss the next turn for surpassing computation timeout by taking more than " + (2 * BOT_EXECUTE_TURN_TIMEOUT) + "ms!";
                System.err.println(reason);
                penalty = new DisqualificationPenalty(reason);
            } else {
                assert future.isDone() : "Future should be done when waitForFutureWhileHandlingCommands() returns a valid end time!";
                try {
                    future.get();
                } catch(CancellationException e) {
                    e.printStackTrace(System.err);
                    System.out.println("Bot \"" + player.getName() + "\" turn execution was cancelled");
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                    System.out.println("Bot \"" + player.getName() + "\" was interrupted");
                } catch (ExecutionException e) {
                    e.getCause().printStackTrace(System.err);
                    String reason = "Bot \"" + player.getName() + "\" has to miss the next turn for failing with exception: " + e.getCause();
                    System.err.println(reason);
                    penalty = new MissTurnsPenalty(reason);
                }
                long turnDuration = endTime - startTime;
                if (!isDebug && turnDuration > BOT_EXECUTE_TURN_TIMEOUT) {
                    String reason = "Bot \"" + player.getName() + "\" has to miss the next turn for surpassing computation timeout by taking " + (turnDuration - BOT_EXECUTE_TURN_TIMEOUT) + "ms longer than allowed!";
                    penalty = new MissTurnsPenalty(reason);
                }
            }
            if (penalty == null) {
                controller.endTurn();
            } else {
                controller.endTurn(penalty);
            }
            // Falls in der waitWhileHandlingCommands()-Methode nicht alle Befehle abgearbeitet wurden, holen wir das
            // hier nach und verarbeiten die in der Controller-Warteschlange verbliebenen Befehle.
            List<Command> commands = new LinkedList<>();
            int count = controller.commands.drainTo(commands);
            if (count > 0) {
                commandHandler.handle(commands);
            }
        });
    }

    /**
     * Hilfsmethode, die wartet bis entweder der Future erfüllt ist oder {@link System#currentTimeMillis()} größer als
     * die übergebene Maximalzeit {@code untilTime} ist, falls diese Instanz sich nicht im Debug-Modus befindet.
     * Währenddessen werden alle Befehle, die in der Warteschlange {@code commands} liegen, mit dem übergebenen
     * {@link Command.CommandHandler} abgearbeitet.
     * @param untilTime Die maximale Wartezeit
     * @param future Der Future, der auf Erfüllung wartet
     * @param commands Die Warteschlange, die abgearbeitet werden soll
     * @param commandHandler Der Handler, der die Befehle abarbeiten soll
     * @return Die Zeit, zu der der Future erfüllt wurde oder -1, falls der Future über die maximale Wartezeit hinaus
     * noch nicht erfüllt wurde
     */
    private long waitForFutureWhileHandlingCommands(long untilTime, CompletableFuture<?> future, BlockingQueue<Command> commands, Command.CommandHandler commandHandler) {
        AtomicLong completionTime = new AtomicLong(-1);
        future.thenRun(() -> {
            completionTime.set(System.currentTimeMillis());
        });
        // TODO: Callback bei Beendigung des Future, könnte controller.endTurn() aufrufen, sodass die static time nicht
        //       überprüft werden müsste
        boolean useStaticTime = isDebug || PlayerType.fromPlayer(player) == PlayerType.HUMAN;
        long currentTime = System.currentTimeMillis();
        // Ausführen der Schleife, solange der Future noch nicht fertig ist und entweder Debug-Modus aktiv ist oder die
        // aktuelle Zeit kleiner oder gleich der maximalen Zeit ist.
        while (completionTime.get() == -1 && (isDebug || (currentTime = System.currentTimeMillis()) <= untilTime)) {
            Command command = null;
            try {
                if (useStaticTime) {
                    // Im Debug-Modus führen wir solange Befehle aus, bis der Future fertig ist, weshalb wir hier
                    // immer nur 100ms warten, um periodisch durch den Schleifenkopf unseren Future zu prüfen.
                    command = commands.poll(100, TimeUnit.MILLISECONDS);
                } else {
                    // Im normalen Modus warten wir auf Befehle, bis der Future fertig ist oder die maximale Zeit
                    // erreicht ist. Als maximale Wartezeit auf einen Befehl aus der Warteschlange verwenden wir die
                    // verbleibende Zeit in der Schleife.
                    command = commands.poll(untilTime - currentTime, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException ignored) {
                // Wir ignorieren die Unterbrechung
            }
            if (command == null) {
                continue;
            }
            commandHandler.handle(command);
        }
        return completionTime.get();
    }

    /**
     * Beendet diesen PlayerExecutor und wird genutzt, um die Ressourcen freizugeben, die zur Ausführung des Spielers
     * genutzt wurden.
     * @param reusable Gibt an, ob die Ressourcen wiederverwendet werden können
     */
    public void dispose(boolean reusable) {
        ResourcePool.getInstance().releaseThreadExecutor(executor, reusable && hasReusableExecutor);
    }
}
