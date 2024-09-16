package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.uniroma1.di.tmancini.teaching.ai.search.AstarExplorer;
import it.uniroma1.di.tmancini.teaching.ai.search.BFSExplorer;
import it.uniroma1.di.tmancini.teaching.ai.search.BestFirstGreedyExplorer;
import it.uniroma1.di.tmancini.teaching.ai.search.DFSExplorer;
import it.uniroma1.di.tmancini.teaching.ai.search.MinCostExplorer;
import it.uniroma1.di.tmancini.teaching.ai.search.Problem;
import it.uniroma1.di.tmancini.teaching.ai.search.Action;
import it.uniroma1.di.tmancini.teaching.ai.search.SearchStateExplorer;
import picocli.CommandLine;

/**
 * Cargo is the main class that represents the cargo problem to be solved by
 * different
 * search algorithms (e.g., BFS, DFS, A*, etc.). It includes methods to
 * initialize the problem,
 * define the initial and goal states, and run the selected search algorithms.
 */
public class Cargo extends Problem implements Callable<Integer> {

        public static boolean debug = false;

        /**
         * Enum defining possible heuristics for the A* and Best First Greedy
         * algorithms.
         */
        public static enum Heuristics {
                UNMET_GOALS
        }

        @CommandLine.Option(names = { "--algos",
                        "--algorithms" }, required = true, split = ",", description = "The algorithms to use, as a double quoted comma-separated list. Valid values are"
                                        +
                                        "{BFS, DFS, MINCOST, A*:<heuristics>, BFG:<heuristics>}, where" +
                                        "<heuristics> can be UNMET_GOALS or OTHER")
        private String[] algos;

        @CommandLine.Option(names = { "-v",
                        "--verbosity" }, defaultValue = "0", description = "The verbosity level of the output, as a positive integer number. "
                                        +
                                        "0 corresponds to 'standard', while higher numbers correspond to higher verbosity.")
        private int vlevel;

        @CommandLine.Option(names = { "-f",
                        "--file" }, defaultValue = "NULL", description = "The file presenting the input data (planes, airports, cargoes) and the initial state")
        private String file_path;

        @CommandLine.Option(names = { "-o",
                        "--output" }, defaultValue = "0", description = "Output only the stats for the a given run")

        private int output_stats;

        @CommandLine.Option(names = { "-s",
                        "--seed" }, defaultValue = "0", description = "A seed to randomize exploration of the search tree")

        private long seed;

        private Cargo.Heuristics h;
        private String[] planes;
        private String[] cargoes;
        private String[] airports;

        private ArrayList<String> at;
        private ArrayList<String> goal;
        private ArrayList<Integer> goal_fluents;

        private HashMap<Integer, String> object_number_map = new HashMap<Integer, String>();
        private int state_size;
        private char[] initial_state;

        private static ArrayList<String> predicates_verbose = new ArrayList<>();
        private HashMap<String, Integer> predicates_with_index_objects = new HashMap<>();

        /**
         * Initializes the cargo problem by reading input from the provided file,
         * setting up the
         * initial and goal states, and generating the available actions.
         * 
         * @param file_path The path of the file containing the problem description.
         */
        public void instaniate_problem(String file_path) {
                process_input(file_path);
                initialize_objects_map(planes, airports, cargoes);
                initialize_state_index_to_fluent_map();
                set_initial_state();
                set_goal_state();
                initialize_fly_actions();
                initialize_load_and_unload_actions();
                CargoState.setRandomSeed(seed);
        }

        /**
         * Sets the goal state by mapping the goal fluents from the input file to their
         * corresponding
         * indices in the predicates_with_index_objects map.
         */
        private void set_goal_state() {
                goal_fluents = goal.stream().map(fluent -> predicates_with_index_objects.get(fluent))
                                .collect(Collectors.toCollection(ArrayList::new));
        }

        /**
         * Processes the input file and extracts the planes, airports, cargoes, initial
         * state,
         * and goal state.
         * 
         * @param filepath The path of the input file containing the problem details.
         */
        private void process_input(String filepath) {
                CargoFileParser cfp = new CargoFileParser("data/" + filepath);
                planes = cfp.getPlanes();
                airports = cfp.getAirports();
                cargoes = cfp.getCargoes();
                at = cfp.getInitial_state();
                goal = cfp.getGoal_state();
        }

        /**
         * Sets the initial state by filling the state array with 'T' for true fluents
         * and 'F'
         * for false fluents based on the parsed initial state.
         */
        private void set_initial_state() {
                state_size = calculate_state_size(planes.length, airports.length, cargoes.length);
                this.initial_state = new char[state_size];
                Arrays.fill(this.initial_state, 'F');
                at.stream().forEach(value -> this.initial_state[predicates_with_index_objects.get(value)] = 'T');
        }

        /**
         * Initializes fly actions for every pair of airports and for every plane. Adds
         * both directions of flights between pairs of airports.
         */
        public void initialize_fly_actions() {
                for (int i = 0; i < planes.length * airports.length - 1; i++) {
                        if (i > 0 && i - 1 % airports.length == 0) {
                                i++;
                        }
                        CargoAction.CargoActionFactory.addFlyCargoAction(i, i + 1);
                        CargoAction.CargoActionFactory.addFlyCargoAction(i + 1, i);
                }
        }

        /**
         * Initializes load and unload actions for each cargo at the corresponding
         * airports and planes.
         */
        public void initialize_load_and_unload_actions() {
                int cargo_in_offset = planes.length * airports.length;
                int cargo_in_fluents_size = cargoes.length * planes.length;
                int at_cargo_airport_offset = cargo_in_offset + cargo_in_fluents_size;

                int airport_offset = 0;
                int cargoCounter = 0;
                int cargo_airport_offset = 0;

                for (int cargoIndex = cargo_in_offset; cargoIndex < cargo_in_offset
                                + cargo_in_fluents_size; cargoIndex++) {

                        for (int airportIndex = airport_offset; airportIndex < airport_offset
                                        + airports.length; airportIndex++) {

                                int cargoAtAirportIndex = at_cargo_airport_offset + (airportIndex - airport_offset)
                                                + cargo_airport_offset;

                                CargoAction.CargoActionFactory.addLoadAction(airportIndex, cargoAtAirportIndex,
                                                cargoIndex);
                                CargoAction.CargoActionFactory.addUnloadAction(airportIndex, cargoIndex,
                                                cargoAtAirportIndex);
                        }

                        cargoCounter++;
                        if (cargoCounter < planes.length) {
                                airport_offset += airports.length;
                        } else {
                                cargoCounter = 0;
                                airport_offset = 0;
                                cargo_airport_offset += airports.length;
                        }
                }
        }

        /**
         *
         * Initializes the state index to fluent map by associating each predicate with
         * a unique index ( e.g. AT(CARGO_1, JKF) -> 2, IN(CARGO_2, PLANE_1) -> 3
         * The number defines thee inexd of the this particular fluent within the state
         * representation ( AT(CARGO_1, JFK ) is assoicated with the third position in
         * the state
         */
        private void initialize_state_index_to_fluent_map() {
                for (int i = 0; i < planes.length; i++) {
                        for (int j = 0; j < airports.length; j++) {
                                predicates_with_index_objects.put(planes[i] + airports[j], predicates_verbose.size());
                                predicates_verbose.add("At(" + planes[i] + "," + airports[j] + ")");
                        }
                }
                for (int i = 0; i < cargoes.length; i++) {
                        for (int j = 0; j < planes.length; j++) {
                                predicates_with_index_objects.put(cargoes[i] + planes[j], predicates_verbose.size());
                                predicates_verbose.add("In(" + cargoes[i] + "," + planes[j] + ")");
                        }
                }
                for (int i = 0; i < cargoes.length; i++) {
                        for (int j = 0; j < airports.length; j++) {
                                predicates_with_index_objects.put(cargoes[i] + airports[j], predicates_verbose.size());
                                predicates_verbose.add("At(" + cargoes[i] + "," + airports[j] + ")");
                        }
                }
        }

        /**
         * Calculates the total state size based on the number of planes, airports, and
         * cargoes.
         * 
         * @param planes   The number of planes.
         * @param airports The number of airports.
         * @param cargoes  The number of cargoes.
         * @return The total size of the state.
         */
        private int calculate_state_size(int planes, int airports, int cargoes) {
                return cargoes * (planes + airports) + planes * airports;
        }

        /**
         * Initializes the objects map which assigns a unique index to each object
         * (planes, airports, cargoes).
         * 
         * @param planes   An array of planes.
         * @param airports An array of airports.
         * @param cargoes  An array of cargoes.
         */
        private void initialize_objects_map(String[] planes, String[] airports, String[] cargoes) {
                List<String> allObjects = Stream.of(planes, airports, cargoes).flatMap(Arrays::stream)
                                .collect(Collectors.toList());

                for (int i = 0; i < allObjects.size(); i++) {
                        object_number_map.put(i, allObjects.get(i));
                }
        }

        /**
         * Default constructor for Cargo class, which initializes the problem with the
         * name "Cargo".
         */
        public Cargo() {
                super("Cargo");
        }

        /**
         * The main method for the Cargo class. This method reads the input, initializes
         * the problem,
         * and runs the specified search algorithms to find the solution.
         * 
         * @return Exit status of the program.
         */
        @Override
        public Integer call() {
                try {
                        instaniate_problem(this.file_path);
                        CargoState initialState = new CargoState(this, initial_state);
                        System.out.println("[INFO]  Initial state: " + initialState);

                        SearchStateExplorer explorer;
                        for (String algo : algos) {
                                List<String> algoAndSetting = getAlgorithmAndHeuristics(algo);
                                String algorithm = algoAndSetting.get(0);
                                String setting = null;
                                if (algoAndSetting.size() > 1)
                                        setting = algoAndSetting.get(1);

                                switch (algorithm) {
                                        case "bfs":
                                                explorer = new BFSExplorer(this);
                                                break;
                                        case "dfs":
                                                explorer = new DFSExplorer(this);
                                                break;
                                        case "mincost":
                                                explorer = new MinCostExplorer(this);
                                                break;
                                        case "a*":
                                                explorer = new AstarExplorer(this);
                                                this.clearHeuristics();
                                                if (algoAndSetting.size() > 1) {
                                                        this.setHeuristics(Cargo.Heuristics.valueOf(setting));
                                                }
                                                break;
                                        case "bfg":
                                                explorer = new BestFirstGreedyExplorer(this);
                                                this.clearHeuristics();
                                                if (algoAndSetting.size() > 1) {
                                                        this.setHeuristics(Cargo.Heuristics.valueOf(setting));
                                                }
                                                break;
                                        default:
                                                throw new IllegalStateException(
                                                                "\n[ERROR] Unknown algorithm: " + algorithm);
                                }

                                explorer.setVerbosity(SearchStateExplorer.VERBOSITY.values()[vlevel]);

                                if (output_stats < 1) {
                                        System.out.println("\n\n\n===================\n\nAlgorithm " + explorer +
                                                        (setting != null ? " (" + setting + ")" : "") + " started");

                                }

                                List<Action> result = explorer.run(initialState);

                                if (output_stats < 1) {
                                        System.out.println("Algorithm " + explorer + " terminated.");
                                }

                                explorer.outputStats();

                                if (output_stats > 0) {
                                        CargoFileParser.write_stats_to_output_file(algorithm,
                                                        this.getHeuristics(),
                                                        explorer.getDurationMsec(),
                                                        this.file_path,
                                                        this.seed);
                                }

                                if (output_stats > 0) {

                                        System.out.println("Initial state:\n" + initialState);
                                }
                                if (result != null) {
                                        if (output_stats < 1) {
                                                System.out.println(
                                                                "\n\n\nSolution found by algorithm " + explorer + " ("
                                                                                + result.size() + " actions):\n");
                                                int i = 0;
                                                for (Action a : result) {
                                                        System.out.println(" [" + i + "]" + a);
                                                        i++;
                                                }
                                                if (algorithm.equals("dfs") || algorithm.equals("bfg")) {
                                                        System.out.println(
                                                                        "\n\nAlgorithm " + explorer + " terminated.");
                                                        explorer.outputStats();
                                                }
                                        }
                                } else
                                        System.out.println("\n\n\nNo solution found by algorithm " + explorer);
                        }
                        return 0;
                } catch (IllegalArgumentException e) {
                        System.err.println(e.getMessage());
                        return 1;
                } catch (IllegalStateException e) {
                        System.err.println(e.getMessage());
                        return 2;
                }
        }

        /**
         * Parses the algorithm and heuristics from the given algorithm string.
         * 
         * @param algo The algorithm string (e.g., "A*:UNMET_GOALS").
         * @return A list containing the algorithm name and optional heuristic.
         */
        private List<String> getAlgorithmAndHeuristics(String algo) {
                List<String> algoAndSetting = Arrays.asList(algo.split(":"));
                List<String> result = new ArrayList<>();
                result.add(algoAndSetting.get(0).trim().toLowerCase());
                if (algoAndSetting.size() > 1)
                        result.add(algoAndSetting.get(1).trim());
                return result;
        }

        public Heuristics getHeuristics() {
                return h;
        }

        public void setHeuristics(Heuristics h) {
                this.h = h;
        }

        public void clearHeuristics() {
                this.h = null;
        }

        /**
         * Prints the header information to the console before execution begins.
         */
        private static void printOutputHeader() {
                System.out.println("\n======================================================" +
                                "=\n=\t\tSearchStateExplorer\t\t      =\n" +
                                "=\t\t  Example: Cargo\t\t      =\n" +
                                "= Computer Science Dept - Sapienza University of Rome =\n" +
                                "=======================================================\n");
        }

        /**
         * The main entry point for the Cargo problem execution. It parses the
         * command-line arguments,
         * initializes the problem, and runs the specified search algorithms.
         * 
         * @param args Command-line arguments.
         */
        public static void main(String[] args) {
                printOutputHeader();
                int exitCode = new CommandLine(new Cargo()).execute(args);

                System.out.printf("%nExecution completed. Exiting %s errors.%n", exitCode > 0 ? "with" : "without");
                System.exit(exitCode);
        }

        public String[] getAlgos() {
                return algos;
        }

        public void setAlgos(String[] algos) {
                this.algos = algos;
        }

        public int getVlevel() {
                return vlevel;
        }

        public void setVlevel(int vlevel) {
                this.vlevel = vlevel;
        }

        public Cargo.Heuristics getH() {
                return h;
        }

        public void setH(Cargo.Heuristics h) {
                this.h = h;
        }

        public String[] getCargoes() {
                return cargoes;
        }

        public void setCargoes(String[] cargoes) {
                this.cargoes = cargoes;
        }

        public String[] getPlanes() {
                return planes;
        }

        public void setPlanes(String[] planes) {
                this.planes = planes;
        }

        public String[] getAirports() {
                return airports;
        }

        public void setAirports(String[] airports) {
                this.airports = airports;
        }

        /**
         * Returns the fluent at the given index as a string.
         * 
         * @param index The index of the fluent.
         * @return The fluent as a string.
         */
        public static String getFluentByIndex(int index) {
                return predicates_verbose.get(index);
        }

        public ArrayList<String> get_predicates_verbose() {
                return predicates_verbose;
        }

        public HashMap<String, Integer> get_predicates_with_index_objects() {
                return this.predicates_with_index_objects;
        }

        /**
         * Extracts and returns the list of objects from the given proposition string.
         * 
         * @param proposition The proposition string (e.g., "At(Plane1, JFK)").
         * @return The objectsextracted from the proposition [ Plane1, JFK ].
         */
        public static List<String> getObjectsFromProposition(String proposition) {
                String parts_string = proposition.replaceAll("(At|In)\\(", "").replaceAll("[()\\s]", "");
                String[] parts_splitted = parts_string.split(",");
                return Arrays.asList(parts_splitted);
        }

        public ArrayList<String> get_initial_state_fluents() {
                ArrayList<String> fluents = new ArrayList<>();
                for (int i = 0; i < initial_state.length; i++) {
                        if (initial_state[i] == 'T') {
                                fluents.add(getFluentByIndex(i));
                        } else {
                                fluents.add("!" + getFluentByIndex(i));
                        }
                }
                return fluents;
        }

        /**
         * Returns the list of goal fluents (integers representing predicates).
         * 
         * @return ArrayList of goal fluents (integers).
         */
        public ArrayList<Integer> getGoal_fluents() {
                return goal_fluents;
        }

        public ArrayList<String> get_goal_fluents_as_strings() {
                ArrayList<String> fluents = new ArrayList<>();
                for (Integer i : getGoal_fluents()) {
                        fluents.add(getFluentByIndex(i));
                }
                return fluents;
        }

        public boolean isGoal(Map<String, String> state) {
                return true;
        }

        public static boolean hasHeuristics(String test) {
                return true;
        }
}
