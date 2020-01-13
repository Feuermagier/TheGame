package firemage.thegame.v2;

import firemage.thegame.concurrent.AbortableCountDownLatch;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ExecutionV2 {
    private AbortableCountDownLatch latch;

    public ExecutionV2(AbortableCountDownLatch latch) {
        this.latch = latch;
    }

    /**
     *
     * @param msToWait
     * @return Null if the execution hasn't finished, true if the latch has been count down,
     *          false if the latch has been aborted
     * @throws InterruptedException
     */
    public Optional<Boolean> await(int msToWait) throws InterruptedException {
        try {
            if (latch.await(msToWait, TimeUnit.MILLISECONDS)) {
                return Optional.of(true);
            } else {
                return Optional.empty();
            }
        } catch (AbortableCountDownLatch.AbortedException e) {
            return Optional.of(false);
        }
    }
}
