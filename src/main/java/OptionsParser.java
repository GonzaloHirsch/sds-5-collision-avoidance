import org.apache.commons.cli.*;

public class OptionsParser {
    protected static RunOptions option;
    protected static NumericalOptions numericalOption;
    protected static SimulationOptions simulationOption;
    protected static Double delta;
    protected static Double totalTime;
    protected static Integer timeMultiplicator;
    protected static String dynamicFile;
    protected static String staticFile;
    protected static double blastoffTime = 0.0;
    protected static double initialVelocity = Constants.SHIP_INITIAL_VELOCITY;

    private static final String PARAM_DT = "dt";
    private static final String PARAM_TF = "tf";
    private static final String PARAM_TM = "tm";
    private static final String PARAM_RA = "ra";
    private static final String PARAM_RN = "rn";
    private static final String PARAM_RS = "rs";
    private static final String PARAM_DF = "df";
    private static final String PARAM_SF = "sf";
    private static final String PARAM_BT = "bt";
    private static final String PARAM_V0 = "v0";

    /**
     * Generates the options for the help.
     *
     * @return Options object with the options
     */
    private static Options GenerateOptions() {
        Options options = new Options();
        options.addOption(PARAM_DT, "delta", true, "Delta of time to be used");
        options.addOption(PARAM_TF, "total_time", true, "Total time to be considered");
        options.addOption(PARAM_TM, "time_multiplicator", true, "Time multiplicator for output");
        options.addOption(PARAM_RA, "run_analytical", false, "Run the analytical solution");
        options.addOption(PARAM_RN, "run_numerical", true, "Run the numerical solution");
        options.addOption(PARAM_RS, "run_simulation", true, "Run the simulation");
        options.addOption(PARAM_DF, "dynamic_file", true, "Path to the file with the dynamic values.");
        options.addOption(PARAM_SF, "static_file", true, "Path to the file with the static values.");
        options.addOption(PARAM_BT, "blastoff_time", true, "Time from the start of the simulation blastoff of the ship will occur");
        options.addOption(PARAM_V0, "initial_velocity", true, "Initial velocity of the ship.");
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

            // Parsing run options
            int runOptionsCount = 0;
            if (cmd.hasOption(PARAM_RA)) runOptionsCount++;
            if (cmd.hasOption(PARAM_RN)) runOptionsCount++;
            if (cmd.hasOption(PARAM_RS)) runOptionsCount++;

            if (runOptionsCount != 1){
                System.out.println("Only 1 run option must be specified");
                System.exit(1);
            } else {
                if (cmd.hasOption(PARAM_RA)) option = RunOptions.RUN_ANALYTICAL;
                else if (cmd.hasOption(PARAM_RN)){
                    option = RunOptions.RUN_NUMERICAL;
                    numericalOption = NumericalOptions.FromValue(cmd.getOptionValue(PARAM_RN));
                    if (numericalOption == null){
                        System.out.println("Invalid numerical option selected");
                        System.exit(1);
                    }
                }
                else if (cmd.hasOption(PARAM_RS)) option = RunOptions.RUN_SIMULATION;
            }

            // Checking if the time amount is present
            if (!cmd.hasOption(PARAM_DT)){
                System.out.println("A delta time must be specified");
                System.exit(1);
            }
            // Retrieving the amount of "time" to iterate with
            delta = Double.parseDouble(cmd.getOptionValue(PARAM_DT));

            // Checking if the total time is present
            if (!cmd.hasOption(PARAM_TF)){
                System.out.println("A total time must be specified");
                System.exit(1);
            }
            // Retrieving the total time to be iterated
            totalTime = Double.parseDouble(cmd.getOptionValue(PARAM_TF));

            // Checking if the time multiplicator is present
            if (!cmd.hasOption(PARAM_TM)){
                System.out.println("A time multiplicator must be specified");
                System.exit(1);
            }
            // Retrieving the total time to be iterated
            timeMultiplicator = Integer.parseInt(cmd.getOptionValue(PARAM_TM));

            if (cmd.hasOption(PARAM_RS)){
                // Checking if the files were present
                if (!cmd.hasOption(PARAM_SF) | !cmd.hasOption(PARAM_DF)){
                    System.out.println("The dynamic and static file path are needed");
                    System.exit(1);
                }

                // Parsing the file paths
                staticFile = cmd.getOptionValue(PARAM_SF);
                dynamicFile = cmd.getOptionValue(PARAM_DF);

                // Getting the simulation option
                simulationOption = SimulationOptions.FromValue(cmd.getOptionValue(PARAM_RS));
                if (simulationOption == null){
                    System.out.println("Invalid simulation option selected");
                    System.exit(1);
                }

                // Blast off time
                if (cmd.hasOption(PARAM_BT)) {
                    blastoffTime = Double.parseDouble(cmd.getOptionValue(PARAM_BT));
                }

                // Initial velocity
                if (cmd.hasOption(PARAM_V0)) {
                    initialVelocity = Double.parseDouble(cmd.getOptionValue(PARAM_V0));
                }
            }
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
        formatter.printHelp("app.Main", options);
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
