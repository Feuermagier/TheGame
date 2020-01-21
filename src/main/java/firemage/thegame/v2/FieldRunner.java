package firemage.thegame.v2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Getter
public class FieldRunner implements Callable<Optional<Boolean>> {

    private final int[][] field;

    private final Player startPlayer;

    private final int initialDepth;

    private final boolean predictWin;


    @Override
    public Optional<Boolean> call() {

        synchronized (FieldRunner.class) {
            System.out.println(Thread.currentThread().getName() + " started its work on");
            System.out.println(Util.arrayToString(field, 1));
            System.out.println("It's " + startPlayer + "'s turn.\n");
        }
        int result = AlgorithmV2.executeTurn(field, Util.playerToInt(startPlayer), initialDepth, predictWin);
        if (result == 1) {
            System.out.println(Thread.currentThread().getName() + " finished (Result: true)\n");
            return Optional.of(true);
        } else if (result == 0) {
            System.out.println(Thread.currentThread().getName() + " finished (Result: false)\n");
            return Optional.of(false);
        } else {
            System.out.println(Thread.currentThread().getName() + " has been cancelled\n");
            return Optional.empty();
        }
    }
}
