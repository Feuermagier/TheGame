package firemage.thegame.v2;

import lombok.Data;
import lombok.Getter;

@Getter
public class Statistics {
    private Player wonPlayer;
    private long durationMicroseconds;
    private int simulatedTurns;
    private int simulatedGames;
    private int coresUsed;

    public Statistics(Player wonPlayer, long durationMicroseconds, int simulatedTurns, int simulatedGames, int coresUsed) {
        this.wonPlayer = wonPlayer;
        this.durationMicroseconds = durationMicroseconds;
        this.simulatedTurns = simulatedTurns;
        this.simulatedGames = simulatedGames;
        this.coresUsed = coresUsed;
    }
}
