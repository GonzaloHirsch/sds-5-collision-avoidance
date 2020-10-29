package app;

import org.apache.commons.lang3.tuple.ImmutablePair;
import pca.PredictiveCollisionAvoidance;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class Main {
    private static final String SIMULATION_FILE = "./parsable_files/output.txt";

    public static void main(String[] args) {
        long startTime = Instant.now().toEpochMilli();

        // Parsing the options
        OptionsParser.ParseOptions(args);

        // FIXME: PUT CORRECT PARTICLE LIST
        PredictiveCollisionAvoidance pca = new PredictiveCollisionAvoidance(OptionsParser.dt, OptionsParser.dt2, null);

        // Running the simulation
        List<ImmutablePair<Double, double[][]>> results = pca.simulate();

        // Generating the output
        GenerateOutputFile(results);

        long endTime = Instant.now().toEpochMilli();

        long total = endTime - startTime;

        System.out.format("Total Time %d millis\n", total);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                    OUTPUT
    /////////////////////////////////////////////////////////////////////////////////////

    private static void GenerateOutputFile(List<ImmutablePair<Double, double[][]>> results){
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

