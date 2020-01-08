package firemage.thegame.concurrent;

import firemage.thegame.Direction;
import firemage.thegame.Field;
import firemage.thegame.GameState;
import firemage.thegame.TheGame;

import java.util.Scanner;

public class UI {

    public static void main(String[] args) {
        new UI().runGame();
    }

    private void runGame() {
        Scanner scanner = new Scanner(System.in);

        int[][] field = {{-1, -1, -1}, {-1, -1, -1}, {0, 0, 0}, {0, 0, 0}, {1, 1, 1}, {1, 1, 1}};
        Field currentField = new Field(field);
        TheGame gameSimulator = new TheGame();
        int currentPlayer = TheGame.WHITE;

        while (TheGame.isWinConditionReached(currentPlayer*-1, currentField) != GameState.WIN) {
            System.out.println(currentPlayer == TheGame.WHITE ? "White's turn" : "Black's turn");
             if (currentPlayer == TheGame.WHITE) {
                 System.out.println("Calculating (optimal) turn...");
                 Field newField = gameSimulator.determineTurn(currentField);
                 if (newField == null) {
                     currentPlayer *= -1;
                     System.out.println("The computer loses...");
                     break;
                 } else {
                     currentField = newField;
                 }
             } else {
                 if (gameSimulator.getPossibleTurns(currentField, currentPlayer).isEmpty()) {
                     currentPlayer *= -1;
                     System.out.println("You can't move");
                     break;
                 }
                 while (true) {
                     System.out.println("Please enter your turn in the format <x> <y> <L/S/R>");
                     String cmd = scanner.nextLine();
                     String[] parts = cmd.split(" ");

                     if (parts.length != 3) continue;

                     Direction dir;
                     switch (parts[2]) {
                         case "L":
                             dir = Direction.LEFT;
                             break;
                         case "R":
                             dir = Direction.RIGHT;
                             break;
                         case "S":
                             dir = Direction.STRAIGHT;
                             break;
                         default:
                             continue;
                     }
                     try {
                         int x = Integer.parseInt(parts[0]);
                         int y = Integer.parseInt(parts[1]);
                         if (x < 0 ||y < 0 || x > currentField.getMaxX() || y > currentField.getMaxY())
                             continue;
                         Field newField = currentField.executeTurnV2(x, y, dir, currentPlayer);
                         if (newField != null) {
                             currentField = newField;
                             break;
                         }
                     } catch (NumberFormatException ex) {
                         System.out.println("not a valid number");
                     }
                 }
             }
            System.out.println(TheGame.arrayToString(currentField.getState(), 1) + "\n");
            currentPlayer *= -1;
        }
        System.out.println(currentPlayer == TheGame.WHITE ? "Black wins" : "White wins");
    }
}
