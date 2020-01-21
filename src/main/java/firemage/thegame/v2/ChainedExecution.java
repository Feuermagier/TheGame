package firemage.thegame.v2;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public class ChainedExecution extends ExecutionV2 {

    private List<ExecutionV2> executions = new ArrayList<>();

    public void addExecution(ExecutionV2 execution) {
        this.executions.add(execution);
    }

    public Optional<Boolean> evaluate() throws TheGameException {

        List<ExecutionV2> finishedExecutions = new ArrayList<>();
        for (ExecutionV2 execution : executions) {
            Optional<Boolean> singleResult = execution.evaluate();
            if (singleResult.isPresent()) {
                if (!singleResult.get()) {
                    cancel();
                    return Optional.of(true);
                } else {
                    finishedExecutions.add(execution);
                }
            }
        }

        executions.removeAll(finishedExecutions);
        if(executions.isEmpty()) {
            cancel();
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void schedule(ExecutorService executorService) {
        executions.forEach(e -> e.schedule(executorService));
    }

    @Override
    public void cancel() throws TheGameException {
        for(ExecutionV2 execution : executions) {
            execution.cancel();
        }
    }

    @Override
    public int getFieldCount() {
        return executions.stream().mapToInt(ExecutionV2::getFieldCount).sum();
    }

    @Override
    public ExecutionV2 splitExecutions() throws TheGameException {
        List<ExecutionV2> newExecutions = new ArrayList<>();
        for (ExecutionV2 exe : executions) {
            newExecutions.add(exe.splitExecutions());
        }
        this.executions = newExecutions;

        return this;
    }
}
