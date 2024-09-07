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
import it.uniroma1.di.tmancini.teaching.ai.search.cargo.CargoAction.FlyCargoAction;
import it.uniroma1.di.tmancini.teaching.ai.search.cargo.CargoAction.LoadCargoAction;
import it.uniroma1.di.tmancini.teaching.ai.search.cargo.CargoAction.UnloadCargoAction;
import picocli.CommandLine;

public class Cargo extends Problem implements Callable<Integer> {

        public static boolean debug = false;

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
                        "--file" }, defaultValue = "NULL", description = "The file presenting the input data ( planes, airports, cargoes ) and the initial state")

        private String file_path;

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

        // Every
        private static ArrayList<String> predicates_verbose = new ArrayList<>();
        private HashMap<String, Integer> predicates_with_index_objects = new HashMap<>();

        public ArrayList<Integer> getGoal_fluents() {
                return goal_fluents;
        }

        /**
         * @TODO: Get Initial State from File private Map<List<Integer>, List<String>>
         *        predicates;
         */
        public void instaniate_problem() {

                process_input();

                initialize_objects_map(planes, airports, cargoes);
                initialize_state_index_to_fluent_map();

                set_initial_state();
                set_goal_state();

                initialize_fly_actions();
                initialize_load_and_unload_actions();

                if (debug) {
                        print_cargo_actions();
                }
        }

        private void set_goal_state() {

                goal_fluents = goal.stream().map(fluent -> predicates_with_index_objects.get(fluent))
                                .collect(Collectors.toCollection(ArrayList::new));
        }

        /**
         * @TODO: Get Problem From File input
         */
        private void process_input() {

                CargoFileParser cfp = new CargoFileParser("data/" + file_path);

                planes = cfp.getPlanes();
                airports = cfp.getAirports();
                cargoes = cfp.getCargoes();
                at = cfp.getInitial_state();
                goal = cfp.getGoal_state();

        }

        private void set_initial_state() {
                state_size = calculate_state_size(planes.length, airports.length, cargoes.length);

                this.initial_state = new char[state_size];
                Arrays.fill(this.initial_state, 'F');

                at.stream().forEach(value -> this.initial_state[predicates_with_index_objects.get(value)] = 'T');
        }

        // For every pairs of airports and for every plane we have a fly action
        public void initialize_fly_actions() {

                for (int i = 0; i < planes.length * airports.length - 1; i++) {

                        if (i > 0 && i - 1 % airports.length == 0) {
                                i++;
                        }
                        CargoAction.CargoActionFactory.addFlyCargoAction(i, i + 1);
                        CargoAction.CargoActionFactory.addFlyCargoAction(i + 1, i);
                }

        }

        public void initialize_load_and_unload_actions() {

                int cargo_in_offset = planes.length * airports.length;
                int cargo_in_fluents_size = cargoes.length * planes.length;
                int at_cargo_airport_offset = cargo_in_offset + cargo_in_fluents_size;

                // A plane can be in airport_offset airports; thus a cargo in that plane has to
                // conincdie with that many airports and cargoes at ariport
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
         */
        private void initialize_state_index_to_fluent_map() {

                for (int i = 0; i < planes.length; i++) {
                        for (int j = 0; j < airports.length; j++) {
                                predicates_with_index_objects.put(planes[i] + airports[j], predicates_verbose.size());
                                predicates_verbose.add("At(" + planes[i] + ", " + airports[j] + ") ");
                        }
                }

                for (int i = 0; i < cargoes.length; i++) {
                        for (int j = 0; j < planes.length; j++) {
                                predicates_with_index_objects.put(cargoes[i] + planes[j], predicates_verbose.size());
                                predicates_verbose.add("In(" + cargoes[i] + ", " + planes[j] + ") ");
                        }
                }

                for (int i = 0; i < cargoes.length; i++) {
                        for (int j = 0; j < airports.length; j++) {
                                predicates_with_index_objects.put(cargoes[i] + airports[j], predicates_verbose.size());
                                predicates_verbose.add("At(" + cargoes[i] + ", " + airports[j] + ") ");
                        }
                }

        }

        /**
         * @param planes
         * @param airports
         * @param cargoes
         * @return int
         */
        private int calculate_state_size(int planes, int airports, int cargoes) {
                return cargoes * (planes + airports) + planes * airports;
        }

        // Initializeze the Hashamp that assigns every obeject a unique interger
        // 0 -> JFK; 1 -> HEATHROW etcc.
        private void initialize_objects_map(String[] planes, String[] airports, String[] cargoes) {

                List<String> allObjects = Stream.of(planes, airports, cargoes)
                                .flatMap(Arrays::stream)
                                .collect(Collectors.toList());

                for (int i = 0; i < allObjects.size(); i++) {
                        object_number_map.put(i, allObjects.get(i));

                }
        }

        public boolean isGoal(Map<String, String> state) {
                return true;
        }

        public Cargo() {

                super("Cargo");

        }

        @Override
        public Integer call() {
                try {
                        instaniate_problem();

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

                                System.out.println("\n\n\n===================\n\nAlgorithm " + explorer +
                                                (setting != null ? " (" + setting + ")" : "") + " started");

                                // if (debug) {
                                // return 1;
                                // }

                                List<Action> result = explorer.run(initialState);

                                System.out.println("Algorithm " + explorer + " terminated.");
                                explorer.outputStats();

                                System.out.println("Initial state:\n" + initialState);
                                if (result != null) {
                                        System.out.println(
                                                        "\n\n\nSolution found by algorithm " + explorer + " ("
                                                                        + result.size() + " actions):\n");
                                        int i = 0;
                                        for (Action a : result) {
                                                System.out.println(" [" + i + "]" + a);
                                                i++;
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

        private List<String> getAlgorithmAndHeuristics(String algo) {
                List<String> algoAndSetting = Arrays.asList(algo.split(":"));
                List<String> result = new ArrayList<>();
                result.add(algoAndSetting.get(0).trim().toLowerCase());
                if (algoAndSetting.size() > 1)
                        result.add(algoAndSetting.get(1).trim());
                return result;
        }

        public static boolean hasHeuristics(String test) {
                return true;
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

        private static void printOutputHeader() {
                System.out.println("\n======================================================" +
                                "=\n=\t\tSearchStateExplorer\t\t      =\n" +
                                "=\t\t  Example: Cargo\t\t      =\n" +
                                "= Computer Science Dept - Sapienza University of Rome =\n" +
                                "=======================================================\n");
        }

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

        private void print_cargo_actions() {

                ArrayList<FlyCargoAction> flyActions = CargoAction.CargoActionFactory.getFlyActions();
                ArrayList<UnloadCargoAction> unloadActions = CargoAction.CargoActionFactory.getUnloadActions();
                ArrayList<LoadCargoAction> loadActions = CargoAction.CargoActionFactory.getLoadActions();
                System.out.println(predicates_with_index_objects);

                System.out.println(flyActions + "\n\n");
                System.out.println(unloadActions + "\n\n");
                System.out.println(loadActions + "\n\n");
        }

        public static String getFluentByIndex(int index) {
                return predicates_verbose.get(index);
        }

        public static ArrayList<String> get_predicates_verbose() {
                return predicates_verbose;
        }

        public HashMap<String, Integer> get_predicates_with_index_objects() {
                return this.predicates_with_index_objects;
        }
}
