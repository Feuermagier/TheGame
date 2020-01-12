package firemage.thegame.v2;

import firemage.thegame.concurrent.AbortableCountDownLatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRunnerV2 {

    private static final int WHITE = 1;
    private static final int BLACK = -1;
    private static final int EMPTY = 0;

    private static final int CPU_CORE_LEFTOVER = 1;
    private static final int LATCH_WAIT_MS = 100;

    public Statistics runGame(int[][] field, Player player, boolean parallel, int cores) throws InterruptedException, TheGameException {

        if (parallel) {
            return runGameParallel(field, player, cores);
        } else {
            return runGameSerial(field, player);
        }
    }

    private Statistics runGameSerial(int[][] field, Player player) {
        long beforeTime = System.nanoTime();
        boolean result = AlgorithmV2.executeTurn(field, player == Player.WHITE ? WHITE : BLACK, 0);
        long afterTime = System.nanoTime();
        return new Statistics(result ? player : player.other(), (afterTime - beforeTime)/1000, 0, 0, 1);
    }

    private Statistics runGameParallel(int[][] field, Player player, int core) throws TheGameException, InterruptedException {
        int intPlayer = player == Player.WHITE ? WHITE : BLACK;
        int coresToUse = Runtime.getRuntime().availableProcessors() - CPU_CORE_LEFTOVER;
        ExecutorService executorService = Executors.newFixedThreadPool(coresToUse);
        List<ExecutionV2> executions = new ArrayList<>();

        try {
            long startTime = System.nanoTime();

            List<int[][]> depthOneTurns = getAllTurns(field, intPlayer);
            if (depthOneTurns == null) {
                throw new TheGameException("One player can win in just one turn. Running in parallel mode is not possible.");
            }
            for (int[][] turn : depthOneTurns) {
                List<int[][]> depthTwoTurns = getAllTurns(turn, -intPlayer);
                if (depthTwoTurns == null) {
                    throw new TheGameException("One player can win in just one turn. Running in parallel mode is not possible.");
                }
                AbortableCountDownLatch latch = new AbortableCountDownLatch(depthTwoTurns.size());
                depthTwoTurns.forEach(t -> executorService.submit(() -> {
                    synchronized (executorService) {
                        System.out.println(Thread.currentThread().getName() + " started its work on");
                        System.out.println(Util.arrayToString(t, 1) + "\n");
                    }
                    long threadStartTime = System.nanoTime();
                    if (!AlgorithmV2.executeTurn(t, intPlayer, 2))
                        latch.countDown();
                    else
                        latch.abort();
                    System.out.println(Thread.currentThread().getName() + " finished after " + ((System.nanoTime() - threadStartTime) / 1000) + "μs. Looking for new work...\n");
                }));
                executions.add(new ExecutionV2(latch));
            }

            boolean finished = false;
            boolean wins = false;
            while (!finished) {
                List<ExecutionV2> finishedExecutions = new ArrayList<>();
                for (ExecutionV2 execution : executions) {
                    Optional<Boolean> state = execution.await(LATCH_WAIT_MS);
                    if (state.isPresent()) {
                        if (state.get())
                            finishedExecutions.add(execution);
                        else {
                            finishedExecutions.add(execution);
                            wins = true;
                            finished = true;
                            break;
                        }
                    }
                }
                executions.removeAll(finishedExecutions);

                if (executions.size() == 0) {
                    wins = false;
                    finished = true;
                }
            }
            long endTime = System.nanoTime();;

            return new Statistics(wins ? player : player.other(), (endTime - startTime) / 1000, 0, 0, coresToUse);

        } finally {
            executorService.shutdownNow();
        }
    }

    private List<int[][]> getAllTurns(int[][] field, int player) {
        List<int[][]> fields = new ArrayList<>();
        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[0].length; y++) {
                if (field[x][y] == player) {
                    // Try all movements
                    for (int dir = -1; dir <= 1; dir++) {
                        int xNew = x - player;
                        int yNew = y + dir;

                        // Check if the new position is inside the field boundaries
                        if (xNew >= 0 && xNew < field.length && yNew >= 0 && yNew < field[0].length) {

                            // Check if the turn is legal
                            int moveAllowed = Math.abs(dir) + (field[xNew][yNew] * player);  // Zero if you move straight and the target position is empty
                            // or you move to the right/left and the target position is occupied by an enemy

                            if (moveAllowed == 0) {

                                // Check if a win condition is reached
                                if (xNew == (1 - player) / 2 * (field.length-1)) {
                                    return null;
                                }

                                // Create new field
                                int[][] newField = Arrays.stream(field).map(int[]::clone).toArray(int[][]::new);

                                // Set new positions
                                newField[x][y] = EMPTY;
                                newField[xNew][yNew] = player;

                                fields.add(newField);
                            }
                        }
                    }
                }
            }
        }
        return fields;
    }
}
