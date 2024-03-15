package com.gatdsen.manager.run;


import com.gatdsen.manager.game.Executable;
import com.gatdsen.manager.game.Game;
import com.gatdsen.manager.game.GameResults;
import com.gatdsen.manager.Manager;
import com.gatdsen.manager.player.data.PlayerInformation;
import com.gatdsen.manager.player.handler.LocalPlayerHandlerFactory;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.simulation.GameState;

import java.util.*;

public class ParallelMultiGameRun extends Run {


    int completed = 0;

    int gameCount = 0;

    private final Map<Game, Integer[]> playerIndices = new HashMap<>();

    protected ParallelMultiGameRun(Manager manager, RunConfig runConfig) {
        super(manager, runConfig);
        if (runConfig.gameMode == GameState.GameMode.Exam_Admission) {

            //ToDo this is the config for the exam admission

            if (runConfig.playerFactories.size() != 1) {
                System.err.println("Exam Admission only accepts exactly 1 player");
                complete();
                return;
            }

            runConfig.playerFactories.add(LocalPlayerHandlerFactory.IDLE_BOT);
            getPlayerFactories().clear();
            getPlayerFactories().addAll(runConfig.playerFactories);
            runConfig.mapName = "MangoMap";
        }
        ArrayList<Integer> indices = new ArrayList<>();
        int playerCount = runConfig.playerFactories.size();
        for (int i = 0; i < playerCount; i++) {
            indices.add(i);
        }
        results.setPlayerInformation(new PlayerInformation[playerCount]);
        results.setScores(new float[playerCount]);
        List<List<Integer>> listOfMatchUps = subsetK(indices, playerCount);
        List<List<Integer>> permListOfMatchUps = new ArrayList<>();
        for (List<Integer> matchUp : listOfMatchUps) {
            permListOfMatchUps.addAll(permutations(matchUp));
        }

        List<Game> games = new ArrayList<>();
        Game lastGame = null;
        Game firstGame = null;
        for (List<Integer> matchUp : permListOfMatchUps
        ) {
            RunConfig curConfig = runConfig.copy();
            List<PlayerHandlerFactory> playerFactories = new ArrayList<>();
            for (Integer index : matchUp) {
                playerFactories.add(runConfig.playerFactories.get(index));
            }
            curConfig.playerFactories = playerFactories;
            Game curGame = new Game(curConfig.asGameConfig());
            curGame.addCompletionListener(this::onGameCompletion);
            if (runConfig.gui) {
                if (lastGame != null) {
                    lastGame.addCompletionListener(g -> {
                        manager.schedule(curGame);
                    });
                } else firstGame = curGame;
                lastGame = curGame;
                getGames().add(curGame);
            }
            playerIndices.put(curGame, matchUp.toArray(new Integer[0]));
            gameCount++;
            games.add(curGame);
        }

        System.out.println("Running Multigame of size " + games.size());

        if (!runConfig.gui) {
            for (Game game : games) {
                addGame(game);
            }
        } else {
            manager.schedule(firstGame);
        }

    }

    public void onGameCompletion(Executable exec) {
        Integer[] matchup = playerIndices.get((Game) exec);
        synchronized (results) {
            GameResults gameResults = exec.getGameResults();
            PlayerInformation[] playerInformation = gameResults.getPlayerInformation();
            float[] scores = gameResults.getScores();
            for (int i = 0; i < scores.length; i++) {
                int index = matchup[i];
                results.setPlayerInformation(index, playerInformation[i]);
                results.setScore(index, results.getScore(index) + scores[i]);
            }
            if ((completed*100)/gameCount < (completed*100 + 100)/gameCount)
                System.out.printf("MultiGameRun(%d)-Completion: %d %% \n", hashCode(),(completed*100)/gameCount);
            completed++;
        }
        System.out.println();
        if (completed == gameCount) {
            float[] scores = results.getScores();
            for (int j = 0; j < scores.length; j++) {
                scores[j] /= gameCount;
            }
            results.setScores(scores);
            complete();
        }
    }

    protected static <T> List<List<T>> subsetK(List<T> list, int subSetSize) {
        ArrayList<List<T>> results = new ArrayList<>();

        int listSize = list.size();
        if (subSetSize > listSize) return results;
        if (subSetSize <= 0) return results;
        if (subSetSize == listSize) {
            results.add(list);
            return results;
        }

        T head = list.remove(0);

        List<List<T>> results1 = subsetK(new ArrayList<>(list), subSetSize);
        results.addAll(results1);
        if (subSetSize == 1) {
            List<T> elemList = new ArrayList<>();
            elemList.add(head);
            results.add(elemList);
        } else {
            List<List<T>> results2 = subsetK(list, subSetSize - 1);
            for (List<T> cur :
                    results2) {
                cur.add(head);
                results.add(cur);
            }
        }
        return results;
    }

    protected static <T> List<List<T>> permutations(List<T> list) {
        int size = 1;
        ArrayList<List<T>> results = new ArrayList<>();
        ListIterator<T> iterator = list.listIterator();
        int i = 1;
        while (iterator.hasNext()) {
            size *= i;
            T next = iterator.next();
            ArrayList<List<T>> newResults = new ArrayList<>(size);
            if (size == 1) {
                ArrayList<T> perm = new ArrayList<>();
                perm.add(next);
                newResults.add(perm);
            } else {
                for (List<T> result : results) {
                    for (int j = 0; j <= result.size(); j++) {
                        newResults.add(insertInCopy(result, next, j));
                    }
                }
            }
            results = newResults;
            i++;
        }
        return results;
    }

    protected static <T> ArrayList<T> insertInCopy(List<T> list, T element, int index) {
        ArrayList<T> copy = new ArrayList<>(list.size() + 1);
        ListIterator<T> iter = list.listIterator();
        int i = 0;
        while (iter.hasNext() && i < index) {
            copy.add(iter.next());
            i++;
        }
        copy.add(element);
        while (iter.hasNext()) {
            copy.add(iter.next());
        }
        return copy;
    }

    @Override
    public String toString() {
        return "ParallelMultiGameRun{" +
                "super=" + super.toString() +
                ", completed=" + completed +
                ", gameCount=" + gameCount +
                ", playerIndices=" + playerIndices +
                '}';
    }
}
