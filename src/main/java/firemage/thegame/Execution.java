package firemage.thegame;

import firemage.thegame.concurrent.AbortableCountDownLatch;
import firemage.thegame.concurrent.ThreadState;

import java.util.concurrent.TimeUnit;

public class Execution {
    private AbortableCountDownLatch latch;
    private Field whiteTurn;

    public Execution(AbortableCountDownLatch latch, Field whiteTurn) {
        this.latch = latch;
        this.whiteTurn = whiteTurn;
    }

    public ThreadState await(int msToWait) throws InterruptedException {
        try {
            if (latch.await(msToWait, TimeUnit.MILLISECONDS)) {
                System.out.println("Finished opening:");
                System.out.println(TheGame.arrayToString(whiteTurn.getState(), 1));
                System.out.println("--> White wins");
                return ThreadState.WHITE_WINS;
            } else {
                return ThreadState.NOT_FINISHED;
            }
        } catch (AbortableCountDownLatch.AbortedException e) {
            System.out.println("Finished opening:");
            System.out.println(TheGame.arrayToString(whiteTurn.getState(), 1));
            System.out.println("--> Black wins\n");
            return ThreadState.BLACK_WINS;
        }
    }
}
