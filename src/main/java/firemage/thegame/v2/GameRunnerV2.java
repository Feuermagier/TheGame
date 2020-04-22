package firemage.thegame.v2;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRunnerV2 {

    private static final int WHITE = 1;
    private static final int BLACK = -1;

    private static final int WAIT_MS = 100;

    public Statistics runGame(int[][] field, Player player, boolean parallel, int cores, boolean predictWin) throws InterruptedException, TheGameException {

        if (parallel) {
            return runGameParallel(field, player, cores, predictWin);
        } else {
            return runGameSerial(field, player, predictWin);
        }
    }

    private Statistics runGameSerial(int[][] field, Player player, boolean predictWin) {
        long beforeTime = System.nanoTime();
        int result = AlgorithmV2.executeTurn(field, player == Player.WHITE ? WHITE : BLACK, 0, predictWin);
        long afterTime = System.nanoTime();

        if (result == AlgorithmV2.CANCELLED) {
            throw new IllegalStateException("The main thread has been cancelled. Please contact your local provider to fix this irrational issue.");
        } else if (result == AlgorithmV2.WINS) {
            return new Statistics(player, (afterTime - beforeTime)/1000, 0, 0, 1);
        } else {
            return new Statistics(player.other(), (afterTime - beforeTime)/1000, 0, 0, 1);
        }
    }
    private Statistics runGameParallel(int[][] field, Player player, int cores, boolean predictWin) throws TheGameException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(cores);

        try {
            ExecutionV2 root = new FiniteExecution(new FieldRunner(field, player, 0, predictWin));

            int precalcCount = 0;
            while (root.getFieldCount() < cores) {
                root = root.splitExecutions();
                precalcCount++;
            }

            System.out.println("Pre-calculated " + precalcCount + " turns.");
            System.out.println("============================== " + root.getFieldCount() + " tasks scheduled ==============================\n\n");

            long startTime = System.nanoTime();
            root.schedule(executorService);

            while (!root.evaluate().isPresent()) {
                // Sleep time has to be long enough (min. ~200ms), otherwise strange things will happen
                Thread.sleep(200);
            }
            long endTime = System.nanoTime();
            boolean result = root.evaluate().get();

            return new Statistics(result ? player : player.other(), (endTime - startTime) / 1000, 0, 0, cores);

        } finally {
            executorService.shutdownNow();
        }
    }
}
