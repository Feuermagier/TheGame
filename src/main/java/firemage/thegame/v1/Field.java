package firemage.thegame.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Field {
    private final int maxX;
    private final int maxY;
    private final int[][] state;


    public Field(int[][] initial) {
        this.state = Arrays.stream(initial).map(int[]::clone).toArray(int[][]::new);
        this.maxX = initial.length - 1;
        this.maxY = initial[0].length - 1;
    }

    public Field(Field field) {
        this.state = Arrays.stream(field.getState()).map(int[]::clone).toArray(int[][]::new);
        this.maxX = field.maxX;
        this.maxY = field.maxY;
    }

    public synchronized Field executeTurnV2(int x, int y, Direction dir, int player) {
        if (player == TheGame.WHITE) {
            if (x < 1) return null;
            int xNew = x - 1;
            int yNew = 0;
            switch(dir) {
                case STRAIGHT:
                    yNew = y;
                    if (state[xNew][yNew] != 0) return null;
                    break;
                case RIGHT:
                    yNew = y + 1;
                    if (yNew > maxY) return null;
                    if (state[xNew][yNew] != TheGame.BLACK) return null;
                    break;
                case LEFT:
                    yNew = y - 1;
                    if (yNew < 0) return null;
                    if (state[xNew][yNew] != TheGame.BLACK) return null;
            }

            Field newField = new Field(this);
            newField.state[x][y] = 0;
            newField.state[xNew][yNew] = TheGame.WHITE;

            return newField;
        } else {
            if (x >= maxX) return null;
            int xNew = x + 1;
            int yNew = 0;
            switch(dir) {
                case STRAIGHT:
                    yNew = y;
                    if (state[xNew][yNew] != 0) return null;
                    break;
                case RIGHT:
                    yNew = y + 1;
                    if (yNew > maxY) return null;
                    if (state[xNew][yNew] != TheGame.WHITE) return null;
                    break;
                case LEFT:
                    yNew = y - 1;
                    if (yNew < 0) return null;
                    if (state[xNew][yNew] != TheGame.WHITE) return null;
            }

            Field newField = new Field(this);
            newField.state[x][y] = 0;
            newField.state[xNew][yNew] = TheGame.BLACK;

            return newField;
        }
    }

    public List<Integer> findPawnIndexInRow(int row, int player) {
        List<Integer> result = new ArrayList<>(5);
        for (int i = 0; i <= maxY; i++) {
            if (state[row][i] == player)
                result.add(i);
        }
        return result;
    }

    public int pawnAt(int x, int y) {
        if( x < 0 || x > maxX || y < 0 || y > maxY)
            return -10;
        return state[x][y];
    }

    public List<int[]> findPositionsByPlayer(int player) {
        List<int[]> out = new LinkedList<>();
        for (int x=0; x <= maxX; x++) {
            for (int y=0; y <= maxY; y++) {
                if (state[x][y] == player) {
                    int[] pos = {x, y};
                    out.add(pos);
                }
            }
        }
        return out;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int[][] getState() {
        return state;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int[] row : state) {
            str.append("\t").append(Arrays.toString(row)).append("\n");
        }
        return str.toString();
    }

    public static int[][] cloneArray(int[][] src) {
        int length = src.length;
        int[][] target = new int[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }
}
