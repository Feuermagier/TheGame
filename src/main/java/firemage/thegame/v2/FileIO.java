package firemage.thegame.v2;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FileIO {

    public static GameConfiguration parseField(String fileName) throws IOException, ConfigurationException {
        Scanner scanner = new Scanner(new File(fileName));
        scanner.useDelimiter(",");

        try {
            // Parse initial data
            String playerInput = scanner.next();
            Player startingPlayer;
            switch (playerInput.toLowerCase()) {
                case "w":
                case "white":
                    startingPlayer = Player.WHITE;
                    break;
                case "b":
                case "black":
                    startingPlayer = Player.BLACK;
                    break;
                default:
                    throw new ConfigurationException("You specified an unknown player");
            }

            int xDim = scanner.nextInt();
            int yDim = scanner.nextInt();

            // Parse field data
            int[][] field = new int[xDim][yDim];
            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    String pos = scanner.next().toLowerCase();
                    switch (pos) {
                        case "w":
                            field[x][y] = 1;
                            break;
                        case "b":
                            field[x][y] = -1;
                            break;
                        case "0":
                            field[x][y] = 0;
                            break;
                        default:
                            throw new ConfigurationException("Your file contains an invalid position index");
                    }
                }
            }
            if (scanner.hasNext()) {
                throw new ConfigurationException("Your file contains position data that is not required");
            }
            return new GameConfiguration(field, startingPlayer);

        } catch (InputMismatchException ex) {
            throw new ConfigurationException("Your file contains a non-number character at an position where a number is excpected", ex);
        } catch (NoSuchElementException ex) {
            throw new ConfigurationException("Your file contains to less position data", ex);
        } finally {
            scanner.close();
        }
    }
}
