package firemage.thegame.v2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class GameConfiguration {
    private int[][] field;
    private Player startingPlayer;
}
