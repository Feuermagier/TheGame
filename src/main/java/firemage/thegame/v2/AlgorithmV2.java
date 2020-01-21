package firemage.thegame.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlgorithmV2 {

    public static final int WINS = 1;
    public static final int LOSES = 0;
    public static final int CANCELLED = -1;

    /* package-private */ static int executeTurn(int[][] field, int player, int depth, boolean predictWin) {

        if (Thread.interrupted()) {
            return CANCELLED;
        }

        int targetRow = (1 - player) / 2 * (field.length-1);

        // Advanced win condition
        if (predictWin) {
            for(int y = 0; y < field[0].length; y++) {
                if(field[targetRow + player][y] == player) {
                    if(y - 1 >= 0 && field[targetRow][y - 1] == -player) {
                        return WINS;
                    } else if(field[targetRow][y] == 0) {
                        return WINS;
                    } else if(y + 1 < field[0].length && field[targetRow][y + 1] == -player) {
                        return WINS;
                    }
                }
            }
        }

        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[0].length; y++) {
                if (field[x][y] == player) {
                    // Try all movements
                    for (int dir = -1; dir <= 1; dir++) {
                        int xNew = x - player;
                        int yNew = y + dir;

                        // Check if the new position is inside the field boundaries
                        if (xNew >= 0 && xNew < field.length && yNew >= 0 && yNew < field[0].length) {

                            // Check if the turn is legal
                            int moveAllowed = Math.abs(dir) + (field[xNew][yNew] * player);  // Zero if you move straight and the target position is empty
                            // or you move to the right/left and the target position is occupied by an enemy

                            if (moveAllowed == 0) {

                                // Check if a win condition is reached
                                if (xNew == targetRow) {
                                    return WINS;
                                }

                                // Store previous positions
                                int prevNewPos = field[xNew][yNew];

                                // Set new positions
                                field[x][y] = 0;
                                field[xNew][yNew] = player;

                                // Check if the enemy cannot win after this turn, then return true: If you execute this turn, you will win
                                int result = executeTurn(field, -player, depth + 1, predictWin);
                                if (result == 0) {
                                    // Revert changes
                                    field[x][y] = player;
                                    field[xNew][yNew] = prevNewPos;
                                    return WINS;
                                } else if (result == CANCELLED) {
                                    return CANCELLED;
                                }

                                // Revert changes
                                field[x][y] = player;
                                field[xNew][yNew] = prevNewPos;
                            }
                        }
                    }
                }
            }
        }
        return LOSES;
    }


    public static List<int[][]> getAllTurns(int[][] field, int player) {
        List<int[][]> fields = new ArrayList<>();
        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[0].length; y++) {
                if (field[x][y] == player) {
                    // Try all movements
                    for (int dir = -1; dir <= 1; dir++) {
                        int xNew = x - player;
                        int yNew = y + dir;

                        // Check if the new position is inside the field boundaries
                        if (xNew >= 0 && xNew < field.length && yNew >= 0 && yNew < field[0].length) {

                            // Check if the turn is legal
                            int moveAllowed = Math.abs(dir) + (field[xNew][yNew] * player);  // Zero if you move straight and the target position is empty
                            // or you move to the right/left and the target position is occupied by an enemy

                            if (moveAllowed == 0) {

                                // Check if a win condition is reached
                                if (xNew == (1 - player) / 2 * (field.length-1)) {
                                    return null;
                                }

                                // Create new field
                                int[][] newField = Arrays.stream(field).map(int[]::clone).toArray(int[][]::new);

                                // Set new positions
                                newField[x][y] = 0;
                                newField[xNew][yNew] = player;

                                fields.add(newField);
                            }
                        }
                    }
                }
            }
        }
        return fields;
    }
}
