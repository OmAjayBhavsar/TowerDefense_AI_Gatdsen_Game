package com.gatdsen.manager.run;

import com.gatdsen.manager.*;
import com.gatdsen.manager.game.Executable;
import com.gatdsen.manager.game.Game;
import com.gatdsen.manager.game.GameResults;
import com.gatdsen.manager.player.data.PlayerInformation;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TournamentRun extends Run {

    AtomicInteger completed = new AtomicInteger(0);

    private class BracketNode {

        private final Object handlerLock = new Object();
        private final Object schedulingLock = new Object();

        private final List<CompletionHandler<BracketNode>> handlers = new ArrayList<>();


        Game game1;
        Game game2;
        Game game3;

        protected int p1 = -1;
        protected int p2 = -1;

        int winner = 0;
        private final RunConfig config;

        private int completed = 0;

        private BracketNode(RunConfig config) {
            this.config = config;
        }

        public void makeLeaf(int p1, int p2) {
            this.p1 = p1;
            this.p2 = p2;
            startGames();
        }

        BracketNode left;
        BracketNode right;

        public void setLeft(BracketNode left) {
            this.left = left;
            left.addCompletionListener(this::onLeftCompletion);
        }

        public void setLeft(int p1) {
            synchronized (schedulingLock) {
                this.p1 = p1;
                if (p2 > -1) startGames();
            }
        }

        protected void onLeftCompletion(BracketNode left) {
            setLeft(left.getWinner());
        }

        public void setRight(BracketNode right) {
            this.right = right;
            right.addCompletionListener(this::onRightCompletion);
        }

        public void setRight(int p2) {
            synchronized (schedulingLock) {
                this.p2 = p2;
                if (p1 > -1) startGames();
            }
        }

        protected void onRightCompletion(BracketNode right) {
            setRight(right.getWinner());
        }

        public int getWinner() {
            return winner > 0 ? p1 : p2;
        }

        public int getLooser() {
            return winner > 0 ? p2 : p1;
        }

        private void startGames() {
            config.playerFactories = new ArrayList<>();
            config.playerFactories.add(playerFactories.get(p1));
            config.playerFactories.add(playerFactories.get(p2));
            config.gameMode.setMap("lukeMap"); //ToDo make dynamic
            game1 = new Game(config.asGameConfig());
            game1.addCompletionListener(this::onGameComplete);
            config.gameMode.setMap("Gadsrena"); //ToDo make dynamic
            game2 = new Game(config.asGameConfig());
            game2.addCompletionListener(this::onGameComplete);
            config.gameMode.setMap("mondlandschaft"); //ToDo make dynamic
            game3 = new Game(config.asGameConfig());
            game3.addCompletionListener(this::onGameComplete);
            manager.schedule(game1);
            manager.schedule(game2);
            manager.schedule(game3);
        }

        void onGameComplete(Executable exec) {
            System.out.println("Completed: " + TournamentRun.this.completed.incrementAndGet());
            synchronized (handlerLock) {
                completed++;
                GameResults results = exec.getGameResults();
                winner = (int) (results.getScores()[0] - results.getScores()[1]);
                if (completed >= 3) {
                    System.out.printf("|%s-%s|%n", playerFactories.get(p1).getName(), playerFactories.get(p2).getName());
                    if (winner == 0)
                        System.err.printf("Warning no Winner in Best of 3 %s vs %s", config.playerFactories.get(0).getName(), config.playerFactories.get(1).getName());
                    for (CompletionHandler<BracketNode> handler : handlers) {
                        handler.onComplete(this);
                    }
                }
            }
        }

        void addCompletionListener(CompletionHandler<BracketNode> handler) {
            synchronized (handlerLock) {
                handlers.add(handler);
                if (completed >= 3) handler.onComplete(this);
            }
        }
    }

    private class LooserBracket extends BracketNode{
        private LooserBracket(RunConfig config) {
            super(config);
        }

        @Override
        protected void onLeftCompletion(BracketNode left) {
            setLeft(left.getLooser());
        }

        @Override
        protected void onRightCompletion(BracketNode right) {
            setRight(right.getLooser());
        }
    }



    private final List<PlayerHandlerFactory> playerFactories;

    int completedGames = 0;

    private BracketNode finalGame;
    private BracketNode redemptionFinal;

    private BracketNode winnerFinal;
    private BracketNode looserFinal;

    protected TournamentRun(Manager manager, RunConfig runConfig) {
        super(manager, runConfig);
        playerFactories = runConfig.playerFactories;

        int playerCount = playerFactories.size();
        results.setPlayerInformation(new PlayerInformation[playerCount]);
        results.setScores(new float[playerCount]);
        if (playerCount < 4) {
            System.err.println("A Tournament requires at least 4 players");
            return;
        }

        if ((playerCount & playerCount - 1) != 0) {
            System.err.printf("Tournament only supports a power of 2 for Number of players(=%d).", playerCount);
            return;
        }

        finalGame = new BracketNode(runConfig.copy());
        finalGame.addCompletionListener(this::onRootCompletion);

        List<BracketNode> winnerLeafGames = new ArrayList<>();
        List<BracketNode> looserHeadGames = new ArrayList<>();

        //
        int capacity = 2;

        winnerFinal = new BracketNode(runConfig.copy());

        winnerLeafGames.add(winnerFinal);


        while (capacity < playerCount) {
            ArrayList<BracketNode> newWinnerLeafGames = new ArrayList<>();
            List<BracketNode> looserLeafGames = new ArrayList<>();
            for (BracketNode cur: winnerLeafGames
                 ) {

                BracketNode b1 = new BracketNode(runConfig.copy());
                BracketNode b2 = new BracketNode(runConfig.copy());

                cur.setLeft(b1);
                cur.setRight(b2);

                newWinnerLeafGames.add(b1);
                newWinnerLeafGames.add(b2);

                LooserBracket looserLeaf = new LooserBracket(runConfig.copy());
                looserLeafGames.add(looserLeaf);
                looserLeaf.setLeft(b1);
                looserLeaf.setRight(b2);
            }

            looserHeadGames.add(makeTournament(looserLeafGames, runConfig));
            winnerLeafGames = newWinnerLeafGames;
            capacity*=2;
        }
        looserFinal = new BracketNode(runConfig.copy());
        winnerFinal.addCompletionListener(bracket -> looserFinal.setRight(bracket.getLooser()));
        BracketNode curTail = looserFinal;
        BracketNode next = null;

        assert looserHeadGames.size()>0;

        for (BracketNode cur: looserHeadGames) {
            if (next != null){
                BracketNode nextTail = new BracketNode(runConfig.copy());
                nextTail.setRight(next);
                curTail.setLeft(nextTail);
                curTail = nextTail;

            }
            next = cur;
        }
        curTail.setLeft(next);

        int pIndex = 0;

        for (BracketNode cur: winnerLeafGames) {
            cur.makeLeaf(pIndex++, pIndex++);
        }


        finalGame = new BracketNode(runConfig.copy());
        finalGame.setLeft(winnerFinal);
        finalGame.setRight(looserFinal);
        finalGame.addCompletionListener(this::onRootCompletion);

        redemptionFinal = new LooserBracket(runConfig.copy());
        redemptionFinal.setLeft(winnerFinal);
        redemptionFinal.setRight(looserFinal);
        redemptionFinal.addCompletionListener(this::onRootCompletion);

        assert pIndex == playerCount;

    }

    private BracketNode makeTournament(List<BracketNode> leafs, RunConfig runConfig){
        assert  ((leafs.size() & leafs.size() - 1) == 0);
        List<BracketNode> newNodes;
        while (leafs.size() > 1){
            newNodes = new ArrayList<>();
            BracketNode last = null;
            for (BracketNode cur: leafs) {
                if (last == null) {
                    last = cur;
                } else {
                    BracketNode next = new BracketNode(runConfig.copy());
                    newNodes.add(cur);
                    next.setLeft(last);
                    next.setRight(cur);
                    last = null;
                }
            }
            leafs = newNodes;
        }
        return leafs.get(0);
    }

    public synchronized void onRootCompletion(BracketNode node) {
        completedGames++;
        if (completedGames == 2) complete();
    }

    @Override
    protected void complete() {
        int winner = finalGame.getWinner();
        int second = finalGame.getLooser();
        results.setScore(redemptionFinal.getWinner(), 3f);

        if (results.getScore(redemptionFinal.getLooser()) == 0f) {
            results.setScore(redemptionFinal.getLooser(), 4f);
        }
        setLooserScore(finalGame.left.left, 5f);
        setLooserScore(finalGame.left.right, 5f);
        setLooserScore(finalGame.right.left, 5f);
        setLooserScore(finalGame.right.right, 5f);
        results.setScore(winner, 1f);
        results.setScore(second, 2f);

        ArrayList<BracketNode> curLayer = new ArrayList<>();
        ArrayList<BracketNode> nextLayer;
        curLayer.add(winnerFinal);

        System.out.println("Winner:" + playerFactories.get(winner).getName());
        System.out.printf("Final: " + printBracket(finalGame));
        System.out.println("3rd:" + playerFactories.get(redemptionFinal.getWinner()).getName());
        System.out.printf("Redemption: " + printBracket(redemptionFinal));
        System.out.println("MainBracket:");
        while (!curLayer.isEmpty()){
            nextLayer = new ArrayList<>();
            for (BracketNode cur : curLayer
            ) {
                System.out.print(printBracket(cur));
                if (cur.left != null) nextLayer.add(cur.left);
                if (cur.right != null) nextLayer.add(cur.right);
            }
            curLayer = nextLayer;
            System.out.println("");
        }
        curLayer.add(looserFinal);
        System.out.println("LooserBracket:");
        while (!curLayer.isEmpty()){
            nextLayer = new ArrayList<>();
            for (BracketNode cur : curLayer
            ) {
                System.out.print(printBracket(cur));
                if (cur.left != null) nextLayer.add(cur.left);
                if (cur.right != null) nextLayer.add(cur.right);
            }
            curLayer = nextLayer;
            System.out.println("");
        }

        super.complete();
    }

    private String printBracket(BracketNode node){
        int s1 = (node.winner + 3)/2;
        int s2 = 3-s1;
        return String.format("|%s-%s:%d-%d|", playerFactories.get(node.p1).getName(), playerFactories.get(node.p2).getName(), s1, s2);
    }

    private void setLooserScore(BracketNode node, float score) {
        if (node == null) return;
        results.setScore(node.getLooser(), score);
        setLooserScore(node.left, score + 1);
        setLooserScore(node.right, score + 1);
    }

    @Override
    public String toString() {
        return "TournamentRun{" +
                "super=" + super.toString() +
                ", players=" + playerFactories +
                ", completedGames=" + completedGames +
                ", finalGame=" + finalGame +
                ", looserBracket=" + redemptionFinal +
                '}';
    }
}
