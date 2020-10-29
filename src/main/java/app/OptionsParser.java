package app;

import org.apache.commons.cli.*;

public class OptionsParser {
    protected static Double dt;
    protected static Double dt2;
    protected static String dynamicFile;
    protected static String staticFile;

    private static final String PARAM_DT = "dt";
    private static final String PARAM_DT2 = "dt2";
    private static final String PARAM_SF = "sf";
    private static final String PARAM_DF = "df";

    /**
     * Generates the options for the help.
     *
     * @return Options object with the options
     */
    private static Options GenerateOptions() {
        Options options = new Options();
        options.addOption(PARAM_DT, "delta", true, "Delta of time to be used.");
        options.addOption(PARAM_DT2, "delta2", true, "Delta 2 of time to be used.");
        options.addOption(PARAM_SF, "static_file", true, "Path to the file with the static values.");
        options.addOption(PARAM_DF, "dynamic_file", true, "Path to the file with the dynamic values.");
        return options;
    }

    public static void ParseOptions(String[] args) {
        // Generating the options
        Options options = GenerateOptions();

        // Creating the parser
        CommandLineParser parser = new DefaultParser();

        try {
            // Parsing the options
            CommandLine cmd = parser.parse(options, args);

            // Checking if the time amount is present
            if (!cmd.hasOption(PARAM_DT)){
                System.out.println("A delta time must be specified");
                System.exit(1);
            }
            // Retrieving the amount of "time" to iterate with
            dt = Double.parseDouble(cmd.getOptionValue(PARAM_DT));

            // Checking if the time amount is present
            if (!cmd.hasOption(PARAM_DT2)){
                System.out.println("A delta 2 time must be specified");
                System.exit(1);
            }
            // Retrieving the amount of "time" to iterate with
            dt2 = Double.parseDouble(cmd.getOptionValue(PARAM_DT2));

            // Checking if the files were present
            if (!cmd.hasOption(PARAM_SF) | !cmd.hasOption(PARAM_DF)){
                System.out.println("The dynamic and static file path are needed");
                System.exit(1);
            }

            // Parsing the file paths
            staticFile = cmd.getOptionValue(PARAM_SF);
            dynamicFile = cmd.getOptionValue(PARAM_DF);
        } catch (ParseException e) {
            System.out.println("Unknown command used");

            // Display the help again
            help(options);
        }
    }

    /**
     * Prints the help for the system to the standard output, given the options
     *
     * @param options Options to be printed as help
     */
    private static void help(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("app.app.Main", options);
        System.exit(0);
    }

    public enum RunOptions{
        RUN_ANALYTICAL,
        RUN_NUMERICAL,
        RUN_SIMULATION
    }

    public enum NumericalOptions{
        RUN_GEAR("g"),
        RUN_VERLET("v"),
        RUN_BEEMAN("b");

        private final String value;

        private NumericalOptions(String s){
            this.value = s;
        }

        public static NumericalOptions FromValue(String s){
            s = s.toLowerCase();
            for (NumericalOptions opt : NumericalOptions.values()){
                if (opt.value.equals(s)){
                    return opt;
                }
            }
            return null;
        }
    }

    public enum SimulationOptions{
        RUN_WITH_SHIP("ws"),
        RUN_NO_SHIP("ns");

        private final String value;

        private SimulationOptions(String s){
            this.value = s;
        }

        public static SimulationOptions FromValue(String s){
            s = s.toLowerCase();
            for (SimulationOptions opt : SimulationOptions.values()){
                if (opt.value.equals(s)){
                    return opt;
                }
            }
            return null;
        }
    }
}
