package firemage.thegame.v2;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class FiniteExecution extends ExecutionV2 {

    private final FieldRunner fieldRunner;

    private Future<Optional<Boolean>> task = null;


    @Override
    public Optional<Boolean> evaluate() throws TheGameException {

        try {
            if(task.isDone() &! task.isCancelled()) {
                if (task.get().isPresent()) {
                    return task.get();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
        return Optional.empty();
    }

    @Override
    public void schedule(ExecutorService executorService) {
        task = executorService.submit(fieldRunner);
    }

    @Override
    public void cancel() throws TheGameException {
        task.cancel(true);
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public ExecutionV2 splitExecutions() throws TheGameException {
        ChainedExecution chainedExecution = new ChainedExecution();

        List<int[][]> possibleTurns = AlgorithmV2.getAllTurns(fieldRunner.getField(), Util.playerToInt(fieldRunner.getStartPlayer()));

        if (possibleTurns == null)
            throw new TheGameException("You cannot split the execution into more threads, because one player may win");

        possibleTurns.forEach(f -> chainedExecution.addExecution(
                new FiniteExecution(
                        new FieldRunner(f, fieldRunner.getStartPlayer().other(), fieldRunner.getInitialDepth() + 1, fieldRunner.isPredictWin())
                )
        ));
        return chainedExecution;
    }
}
