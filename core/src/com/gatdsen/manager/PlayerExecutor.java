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
import java.util.concurrent.*;

public final class PlayerExecutor {

    private static final int BOT_EXECUTE_GRACE_PERIODE = 100;
    public static final int BOT_EXECUTE_INIT_TIMEOUT = 1000;
    public static final int BOT_EXECUTE_TURN_TIMEOUT = 500 + BOT_EXECUTE_GRACE_PERIODE;
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

    private boolean isDisqualified = false;

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
        if (PlayerType.fromPlayer(player) == PlayerType.BOT) {
            String[] illegalImports = playerClassAnalyzer.getIllegalImports();
            if (illegalImports.length > 0) {
                String reason = "Bot \"" + player.getName() + "\" has been disqualified for using the following illegal package or class imports: \"" + String.join("\", \"", illegalImports) + "\"";
                System.err.println(reason);
                return new DisqualificationPenalty(reason);
            }
        }
        StaticGameState staticState = new StaticGameState(state, playerIndex);
        Future<?> future = executor.execute(() -> {
            Thread.currentThread().setName("Init_Thread_Player_" + player.getName());
            if (PlayerType.fromPlayer(player) == PlayerType.BOT) {
                ((Bot) player).setRandomSeed(seed);
            }
            player.init(staticState);
        });
        long timeout = PlayerType.fromPlayer(player) == PlayerType.HUMAN ? HUMAN_EXECUTE_INIT_TIMEOUT : BOT_EXECUTE_INIT_TIMEOUT;
        long startTime = System.currentTimeMillis();
        try {
            if (isDebug) {
                future.get();
            } else {
                future.get(timeout, TimeUnit.MILLISECONDS);
            }
        } catch (ExecutionException | InterruptedException | TimeoutException ignored) {
        }
        return handlePlayerFuture(future, System.currentTimeMillis() - startTime, timeout);
    }

    public Future<?> executeTurn(GameState state, Command.CommandHandler commandHandler) {
        StaticGameState staticState = new StaticGameState(state, playerIndex);
        Controller controller = new Controller(
                PlayerType.fromPlayer(player) == PlayerType.HUMAN ? HUMAN_CONTROLLER_USES : BOT_CONTROLLER_USES
        );
        Future<?> future = executor.execute(() -> {
            Thread.currentThread().setName("Run_Thread_Player_" + player.getName());
            player.executeTurn(staticState, controller);
        });
        return executor.execute(() -> {
            Thread.currentThread().setName("Future_Executor_Player_" + player.getName());
            long timeout = 0;
            switch (PlayerType.fromPlayer(player)) {
                case HUMAN:
                    inputGenerator.activateTurn((HumanPlayer) player, playerIndex);
                    timeout = HUMAN_EXECUTE_TURN_TIMEOUT;
                    break;
                case BOT:
                    timeout = 2 * BOT_EXECUTE_TURN_TIMEOUT;
                    break;
            }
            long startTime = System.currentTimeMillis();
            long endTime = waitWhileHandlingCommands(startTime + timeout, future, controller.commands, commandHandler);
            Penalty penalty = handlePlayerFuture(future, endTime - startTime, PlayerType.fromPlayer(player) == PlayerType.HUMAN ? timeout : BOT_EXECUTE_TURN_TIMEOUT);
            if (penalty == null) {
                controller.endTurn();
            } else {
                controller.endTurn(penalty);
            }
            Command command;
            // Falls in der waitWhileHandlingCommands()-Methode nicht alle Befehle abgearbeitet wurden, holen wir das
            // hier nach und führen alle in der Controller-Warteschlange verbliebenen Befehle aus.
            while ((command = controller.commands.poll()) != null) {
                commandHandler.handle(command);
            }
        });
    }

    /**
     * Hilfsmethode, die wartet bis entweder der Future erfüllt ist ({@link Future#isDone()} gibt true zurück) oder
     * {@link System#currentTimeMillis()} größer als die übergebene Maximalzeit {@code untilTime} ist, falls diese
     * Instanz sich nicht im Debug-Modus befindet.
     * Währenddessen werden alle Befehle, die in der Warteschlange {@code commands} liegen, mit dem übergebenen
     * {@link Command.CommandHandler} abgearbeitet.
     * Es wird die Systemzeit, als die Schleife verlassen wurde, zurückgegeben.
     * @param untilTime Die maximale Wartezeit
     * @param future Der Future, der auf Erfüllung wartet
     * @param commands Die Warteschlange, die abgearbeitet werden soll
     * @param commandHandler Der Handler, der die Befehle abarbeiten soll
     * @return Die Systemzeit als die Schleife verlassen wurde
     */
    private long waitWhileHandlingCommands(long untilTime, Future<?> future, BlockingQueue<Command> commands, Command.CommandHandler commandHandler) {
        long currentTime = System.currentTimeMillis();
        // Ausführen der Schleife, solange der Future noch nicht fertig ist und entweder Debug-Modus aktiv ist oder die
        // aktuelle Zeit kleiner oder gleich der maximalen Zeit ist.
        while (!future.isDone() && (isDebug || (currentTime = System.currentTimeMillis()) <= untilTime)) {
            Command command = null;
            try {
                if (isDebug) {
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
        return currentTime;
    }

    private Penalty handlePlayerFuture(Future<?> future, long turnDuration, long allowedTimeout) {
        Penalty penalty = null;
        switch (PlayerType.fromPlayer(player)) {
            case HUMAN:
                handleHumanPlayerFuture(future);
                break;
            case BOT:
                penalty = handleBotPlayerFuture(future, turnDuration, allowedTimeout);
                break;
        }
        return penalty;
    }

    private void handleHumanPlayerFuture(Future<?> future) {
        if (future.isDone()) {
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
        } else {
            future.cancel(true);
            System.out.println("HumanPlayer computation surpassed timeout");
        }
    }

    private void handleHumanPlayer() throws CancellationException, InterruptedException, ExecutionException {

    }

    private Penalty handleBotPlayerFuture(Future<?> future, long turnDuration, long allowedTimeout) {
        if (future.isDone()) {
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
                return new MissTurnsPenalty(reason);
            }
            if (!isDebug && turnDuration > allowedTimeout) {
                String reason = "Bot \"" + player.getName() + "\" has to miss the next turn for surpassing computation timeout by taking " + (turnDuration - allowedTimeout) + "ms longer than allowed!";
                return new MissTurnsPenalty(reason);
            }
        } else {
            future.cancel(true);
            isDisqualified = true;
            String reason = "Bot \"" + player.getName() + "\" has to miss the next turn for surpassing computation timeout by taking more than " + (2 * allowedTimeout) + "ms!";
            System.err.println(reason);
            return new DisqualificationPenalty(reason);
        }
        return null;
    }

    public void dispose() {
        ResourcePool.getInstance().releaseThreadExecutor(executor, !isDisqualified);
    }
}
