import org.apache.commons.lang3.tuple.ImmutablePair;
import oscillator.OscillatorSimulation;
import solar_system.SolarSystemSimulation;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final String BEEMAN_FILE = "./parsable_files/beeman.txt";
    private static final String VERLET_FILE = "./parsable_files/verlet.txt";
    private static final String GEAR_FILE = "./parsable_files/gear.txt";
    private static final String ANALYTIC_FILE = "./parsable_files/analytic.txt";
    private static final String SIMULATION_FILE = "./parsable_files/output.txt";

    public static void main(String[] args) {
        long startTime = Instant.now().toEpochMilli();

        // Parsing the options
        OptionsParser.ParseOptions(args);

        // Determine what to run
        switch (OptionsParser.option){
            case RUN_ANALYTICAL:
                runAnalytic(OptionsParser.totalTime, OptionsParser.delta, OptionsParser.timeMultiplicator);
                break;
            case RUN_NUMERICAL:
                runNumerical(OptionsParser.numericalOption, OptionsParser.totalTime, OptionsParser.delta, OptionsParser.timeMultiplicator);
                break;
            case RUN_SIMULATION:
                try {
                    // Parsing the initial configuration
                    app.ConfigurationParser.ParseConfiguration(OptionsParser.staticFile, OptionsParser.dynamicFile);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                    System.exit(1);
                }
                runSimulation(OptionsParser.simulationOption, OptionsParser.totalTime, OptionsParser.delta, OptionsParser.timeMultiplicator, OptionsParser.blastoffTime, OptionsParser.initialVelocity);
                break;
        }

        long endTime = Instant.now().toEpochMilli();

        long total = endTime - startTime;

        System.out.format("Total Time %d millis\n", total);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                    SIMULATIONS
    /////////////////////////////////////////////////////////////////////////////////////

    private static void runAnalytic(double tf, double dt, int tm){
        System.out.print("Running ANALYTICAL solution... ");
        OscillatorSimulation os = new OscillatorSimulation(tf, dt, tm);
        double[][] results = os.runAnalytical();
        GenerateOutputFileForOscillator(results, ANALYTIC_FILE);
    }

    private static void runNumerical(OptionsParser.NumericalOptions option, double tf, double dt, int tm){
        OscillatorSimulation os = new OscillatorSimulation(tf, dt, tm);
        double[][] results;

        switch (option){
            case RUN_GEAR:
                System.out.print("Running GEAR PREDICTOR CORRECTOR solution... ");
                results = os.runGearPredictorCorrector();
                GenerateOutputFileForOscillator(results, GEAR_FILE);
                break;
            case RUN_VERLET:
                System.out.print("Running VERLET solution... ");
                results = os.runVerlet();
                GenerateOutputFileForOscillator(results, VERLET_FILE);
                break;
            case RUN_BEEMAN:
                System.out.print("Running BEEMAN solution... ");
                results = os.runBeeman();
                GenerateOutputFileForOscillator(results, BEEMAN_FILE);
                break;
        }
    }

    private static void runSimulation(OptionsParser.SimulationOptions option, double tf, double dt, int tm, double blastoffTime, double v0){
        SolarSystemSimulation sss = new SolarSystemSimulation(tf, dt, tm, app.ConfigurationParser.particles.get(0), app.ConfigurationParser.particles.get(1), app.ConfigurationParser.particles.get(2), app.ConfigurationParser.particles.get(3), blastoffTime, v0);

        // Simulating the system
        List<ImmutablePair<Double, double[][]>> results = Collections.emptyList();

        switch (option){
            case RUN_WITH_SHIP:
                results = sss.simulateSpaceshipTraveling();
                break;
            case RUN_NO_SHIP:
                results = sss.simulateSolarSystem();
                break;
        }

        // Generating the output
        GenerateOutputFileForSolarSystem(results);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                    OUTPUT
    /////////////////////////////////////////////////////////////////////////////////////

    private static void GenerateOutputFileForOscillator(double[][] results, String filename) {
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(filename, true));

            for (double[] result : results) {
                // Adding the time
                bf.append(String.format("%f ", result[0]));

                // Adding the position
                String line = result[1] + "\n";
                try {
                    bf.append(line);
                } catch (IOException e) {
                    System.out.println("Error writing to the output file");
                }
            }

            bf.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error writing to the output file");
        }
    }

    private static void GenerateOutputFileForSolarSystem(List<ImmutablePair<Double, double[][]>> results){
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(Main.SIMULATION_FILE, false));

            for (ImmutablePair<Double, double[][]> pair : results){
                // Adding the time
                bf.append(String.format("%f\n", pair.left));

                for (double[] result : pair.right) {
                    // Adding the position
                    String line = result[0] + " " + result[1] + " " + result[2] + " " + result[3] + "\n";
                    try {
                        bf.append(line);
                    } catch (IOException e) {
                        System.out.println("Error writing to the output file");
                    }
                }
            }

            bf.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error writing to the output file");
        }
    }
}

