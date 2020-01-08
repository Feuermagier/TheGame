package firemage.thegame;

public class GamePosition {
    private int[][] field;
    private int player;

    public GamePosition(int[][] field, int player) {
        this.field = field;
        this.player = player;
    }

    public int[][] getField() {
        return field;
    }

    public int getPlayer() {
        return player;
    }
}
