package firemage.thegame.v2;

import java.util.Arrays;
import java.util.Collections;

public final class Util {
    public static String tabs(int times) {
        return String.join("", Collections.nCopies(times, "|\t"));
    }

    public static String arrayToString(int[][] array, int tabs) {
        StringBuilder str = new StringBuilder();
        for(int[] row : array) {
            str.append(tabs(tabs)).append(Arrays.toString(row)).append("\n");
        }
        return str.substring(0, str.length()-1).replace("-1", "B").replace("1", "W");
    }

    public static int playerToInt(Player player) {
        return player == Player.WHITE ? 1 : -1;
    }

    public static Player intToPlayer(int player) {
        return player == 1 ? Player.WHITE : Player.BLACK;
    }
}
