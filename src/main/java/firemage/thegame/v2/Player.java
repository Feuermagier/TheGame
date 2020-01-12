package firemage.thegame.v2;

public enum Player {
    WHITE,
    BLACK;

    public static Player getByInt(int player) {
        if (player == 1)
            return WHITE;
        else if (player == -1)
            return BLACK;
        else
            throw new IllegalArgumentException("player must be 1 (White) or -1 (Black");
    }

    public Player other() {
        return this == WHITE ? BLACK : WHITE;
    }
}
