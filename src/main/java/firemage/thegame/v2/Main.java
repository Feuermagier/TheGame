package firemage.thegame.v2;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.IOException;

public class Main {

    @Parameter(names = "-parallel", description = "Run in parallel mode for faster execution")
    private boolean parallel = false;

    @Parameter(names = "-file", description = "Name of the game configuration file", required = true)
    private String filename;

    @Parameter(names = "-cores", description = "Cores to use in parallel mode. Default is one less than available virtual cores")
    private int cores = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);

    public static void main(String[] args) {
        Main main = new Main();
        JCommander commander = new JCommander(main);
        try {
            commander.parse(args);
            main.run();
        } catch(ParameterException ex) {
            commander.usage();
        }
    }

    private void run() {
        try {

            GameConfiguration config = FileIO.parseField(filename);
            System.out.println("Starting player is " + config.getStartingPlayer());
            System.out.println("Field to analyze:");
            System.out.println(Util.arrayToString(config.getField(), 0) + "\n\n");

            if (parallel) {
                System.out.println("Running in parallel mode using " + cores + " cores.");
            } else {
                System.out.println("Running in serial mode.");
            }
            System.out.println("Working...\n\n");

            Statistics statistics = new GameRunnerV2().runGame(config.getField(), config.getStartingPlayer(), parallel, cores);
            System.out.println("\n\n--------------------------------------------------------\n");
            System.out.println(statistics.getWonPlayer() + " wins!\n\n");
            System.out.println("Duration: " + statistics.getDurationMicroseconds() + "µs");
            System.out.println("Used " + statistics.getCoresUsed() + " core(s) out of " + Runtime.getRuntime().availableProcessors() + " core(s)");

        } catch (InterruptedException e) {
            System.err.println("Error: The thread has been interrupted. Exiting...");
            e.printStackTrace();
            System.exit(100);
        } catch (TheGameException e) {
            System.err.println("Error: The field cannot be analyzed with the given parameters:");
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (ConfigurationException e) {
            System.err.println("You provided an incorrect configuration file:");
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("File error:");
            System.err.println(e.getMessage());
        }

        System.exit(0); // Cut off all threads
    }
}
