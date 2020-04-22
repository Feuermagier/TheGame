package firemage.thegame.v2;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public abstract class ExecutionV2 {


    public abstract Optional<Boolean> evaluate() throws TheGameException;

    public abstract void schedule(ExecutorService executorService);

    public abstract void cancel() throws TheGameException;

    public abstract int getFieldCount();

    public abstract ExecutionV2 splitExecutions() throws TheGameException;
}
