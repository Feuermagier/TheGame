package firemage.thegame.v1;

import firemage.thegame.concurrent.AbortableCountDownLatch;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TheGame {

    private static final int CPU_CORE_LEFTOVER = 1;
    private static final int LATCH_WAIT_MS = 100;

    public static final int WHITE = 1;
    public static final int BLACK = -1;

    private static long turnCount = 0;
    private static long gameCount = 0;

    public boolean runFromState(Field field, int depth) {

        List<Field> whiteTurns = getPossibleTurns(field, WHITE);
        if (isWinPredictable(field, WHITE)) {
            //System.out.println(tabs(depth) + "White will win");
            turnCount += depth + 1;
            gameCount++;
            return true;
        }
        boolean onePositiveTurn = false;
        for (Field whiteTurn : whiteTurns) {
            /////////////
            //System.out.println(tabs(depth+1) + "Depth: " + (depth+1) + ", White's turn");
            //System.out.println(arrayToString(whiteTurn.getState(), depth+1));
            /////////////
            GameState state = isWinConditionReached(WHITE, whiteTurn);
            if (state == GameState.WIN) {
                //System.out.println(tabs(depth+1) + "White wins");
                gameCount++;
                turnCount += depth + 1;
                return true;
            } else if (state == GameState.NONE) {
                if (isWinPredictable(whiteTurn, BLACK)) {
                    //System.out.println(tabs(depth+1) + "Black will win (--> can escape)");
                    turnCount += depth + 2;
                    gameCount++;
                    continue;
                }
                boolean canBlackEscape = false;
                for (Field blackTurn : getPossibleTurns(whiteTurn, BLACK)) {
                    ////////////
                    //System.out.println(tabs(depth+2) + "Depth: " + (depth+2) + ", Black's turn");
                    //System.out.println(arrayToString(blackTurn.getState(), depth+2));
                    ////////////
                    GameState blackState = isWinConditionReached(BLACK, blackTurn);
                    if(blackState == GameState.WIN) {
                        //System.out.println(tabs(depth+2) + "Black wins");
                        gameCount++;
                        turnCount += depth + 2;
                        canBlackEscape = true;
                    } else if(blackState == GameState.NONE && !runFromState(blackTurn, depth + 2)) {
                        //System.out.println(tabs(depth+1) + "Black can escape");
                        canBlackEscape = true;
                        break;
                    }
                }
                if(!canBlackEscape) {
                    onePositiveTurn = true;
                    //System.out.println(tabs(depth+1) + "White wins");
                    break;
                }
            }
        }

        return onePositiveTurn;
    }

    public Field determineTurn(Field field) {
        List<Field> possibleTurns = getPossibleTurns(field, WHITE);
        gameCount = 0;
        turnCount = 0;
        for (Field whiteTurn : possibleTurns) {
            boolean blackWins = false;
            for (Field blackTurn : getPossibleTurns(whiteTurn, BLACK)) {
                if (!runFromState(blackTurn, 2)) {
                    blackWins = true;
                    break;
                }
            }
            if (!blackWins) {
                System.out.println(tabs(1) + "Simulated ~" + turnCount + " turns");
                return whiteTurn;
            }
        }
        return null;
    }

    private boolean setupThreads(Field field) throws InterruptedException {
        int virtualCores = Runtime.getRuntime().availableProcessors();
        System.out.println(virtualCores + " virtual cores available");
        System.out.println("Going to use a maximum of " + (virtualCores - CPU_CORE_LEFTOVER) + " threads for calculation\n");
        System.out.println("----------------------------------------------");
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - CPU_CORE_LEFTOVER);
        try {
            List<Field> whiteTurns = getPossibleTurns(field, WHITE);
            List<Execution> executions = new ArrayList<>();
            for(Field whiteTurn : whiteTurns) {
                turnCount++;
                List<Field> blackTurns = getPossibleTurns(whiteTurn, BLACK);
                AbortableCountDownLatch latch = new AbortableCountDownLatch(blackTurns.size());
                blackTurns.forEach(blackTurn -> {
                    turnCount++;
                    executorService.submit(() -> {
                        synchronized (executorService) {
                            System.out.println(Thread.currentThread().getName() + " started its work on");
                            System.out.println(arrayToString(blackTurn.getState(), 1) + "\n");
                        }
                        long startTime = System.nanoTime();
                        if(runFromState(blackTurn, 2))
                            latch.countDown();
                        else
                            latch.abort();
                        System.out.println(Thread.currentThread().getName() + " finished after " + ((System.nanoTime()-startTime)/1000) + "μs. Looking for new work...\n");
                    });
                });
                executions.add(new Execution(latch, whiteTurn));
            }

            ThreadState state = ThreadState.NOT_FINISHED;
            while (state == ThreadState.NOT_FINISHED) {
                List<Execution> finishedExecutions = new ArrayList<>();
                for(Execution execution : executions) {
                    ThreadState subState = execution.await(LATCH_WAIT_MS);
                    if (subState == ThreadState.BLACK_WINS) {
                        finishedExecutions.add(execution);
                    } else if (subState == ThreadState.WHITE_WINS) {
                        finishedExecutions.add(execution);
                        state = ThreadState.WHITE_WINS;
                        break;
                    }
                }
                executions.removeAll(finishedExecutions);

                if(executions.size() == 0) {
                    return false;
                }
            }
            return true;
        } finally {
            executorService.shutdownNow();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int[][] field = {{-1, -1, -1}, {-1, -1, -1}, {0, 0, 0}, {0, 0, 0}, {1, 1, 1}, {1, 1, 1}};
        System.out.println("Game to analyze: ");
        System.out.println(arrayToString(field, 1));
        System.out.println("\nWorking...\n\n");
        long startTime = System.nanoTime();
        //boolean player = new TheGame().runFromState(new Field(field), 0);
        boolean player = new TheGame().setupThreads(new Field(field));
        long endTime = System.nanoTime();
        System.out.println("\n\n--------------------------------------------------------");
        System.out.println(player ? "White wins!" : "Black wins!");
        System.out.println("\n\nMetrics:\n");
        System.out.println("\tSimulation took " + (endTime - startTime)/1000 + "μs");
        System.out.println("\t~" + turnCount + " turns simulated");
        System.out.println("\t~" + gameCount + " games simulated");
        System.out.println("\tA game took an average of " + (turnCount/gameCount) + " turns");
        System.exit(0); // Cut off all threads
    }

    public static GameState isWinConditionReached(int player, Field field) {
        if (player == WHITE) {
            if (field.findPawnIndexInRow(0, WHITE).size() >= 1)
                return GameState.WIN;
        } else {
            if (field.findPawnIndexInRow(field.getMaxX(), BLACK).size() >= 1)
                return GameState.WIN;
        }
        return GameState.NONE;
    }

    private boolean isWinPredictable(Field field, int player) {
        if (player == WHITE) {
            List<Integer> pawnsInSecondRow = field.findPawnIndexInRow(1, player);
            if (pawnsInSecondRow.size() >= 1) {
                for (int y : pawnsInSecondRow) {
                    if (field.pawnAt(0, y) == 0)
                        return true;
                    if (field.pawnAt(0, y-1) == BLACK)
                        return true;
                    if (field.pawnAt(0, y+1) == BLACK)
                        return true;
                }
            }
        } else {
            List<Integer> pawnsInSecondRow = field.findPawnIndexInRow(field.getMaxX()-1, player);
            if (pawnsInSecondRow.size() >= 1) {
                for (int y : pawnsInSecondRow) {
                    if (field.pawnAt(field.getMaxX(), y) == 0)
                        return true;
                    if (field.pawnAt(field.getMaxX(), y-1) == WHITE)
                        return true;
                    if (field.pawnAt(field.getMaxX(), y+1) == WHITE)
                        return true;
                }
            }
        }
        return false;
    }

    public List<Field> getPossibleTurns(Field field, int player) {
        List<Field> turns = new LinkedList<>();

        for(int[] pos : field.findPositionsByPlayer(player)) {
            Field turn = field.executeTurnV2(pos[0], pos[1], Direction.STRAIGHT, player);
            if (turn != null) turns.add(turn);
            turn = field.executeTurnV2(pos[0], pos[1], Direction.RIGHT, player);
            if (turn != null) turns.add(turn);
            turn = field.executeTurnV2(pos[0], pos[1], Direction.LEFT, player);
            if (turn != null) turns.add(turn);
        }
        return turns;
    }

    public static String tabs(int times) {
        return String.join("", Collections.nCopies(times, "|\t"));
    }

    public static String arrayToString(int[][] array, int tabs) {
        StringBuilder str = new StringBuilder();
        for(int[] row : array) {
            str.append(tabs(tabs)).append(Arrays.toString(row)).append("\n");
        }
        return str.substring(0, str.length()-1).replace("-1", "S").replace("1", "W");
    }
}
