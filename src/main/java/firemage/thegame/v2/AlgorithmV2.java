package firemage.thegame.v2;

public class AlgorithmV2 {
    /* package-private */ static boolean executeTurn(int[][] field, int player, int depth, boolean predictWin) {
        //System.out.println(Main.arrayToString(field, depth) + "\n");

        int targetRow = (1 - player) / 2 * (field.length-1);

        // Advanced win condition

        if (predictWin) {
            for(int y = 0; y < field[0].length; y++) {
                if(field[targetRow + player][y] == player) {
                    if(y - 1 >= 0 && field[targetRow][y - 1] == -player) {
                        return true;
                    } else if(field[targetRow][y] == 0) {
                        return true;
                    } else if(y + 1 < field[0].length && field[targetRow][y + 1] == -player) {
                        return true;
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
                                    return true;
                                }

                                // Store previous positions
                                int prevNewPos = field[xNew][yNew];

                                // Set new positions
                                field[x][y] = 0;
                                field[xNew][yNew] = player;

                                // Check if the enemy cannot win after this turn, then return true: If you execute this turn, you will win
                                if (!executeTurn(field, -player, depth + 1, predictWin)) {
                                    // Revert changes
                                    field[x][y] = player;
                                    field[xNew][yNew] = prevNewPos;
                                    return true;
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
        return false;
    }
}
