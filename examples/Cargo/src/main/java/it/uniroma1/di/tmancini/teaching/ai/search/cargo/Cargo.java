package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import it.uniroma1.di.tmancini.teaching.ai.search.*;
import picocli.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.Map;

/**
 * 
 */
public class Cargo extends Problem implements Callable<Integer> {

        public static enum Heuristics {
                MANHATTAN, TILES_OUT_OF_PLACE
        }

        @CommandLine.Option(names = { "--algos",
                        "--algorithms" }, required = true, split = ",", description = "The algorithms to use, as a double quoted comma-separated list. Valid values are"
                                        +
                                        "{BFS, DFS, MINCOST, A*:<heuristics>, BFG:<heuristics>}, where" +
                                        "<heuristics> can be MANHATTAN or TILES_OUT_OF_PLACE")
        private String[] algos;

        @CommandLine.Option(names = { "-v",
                        "--verbosity" }, defaultValue = "0", description = "The verbosity level of the output, as a positive integer number. "
                                        +
                                        "0 corresponds to 'standard', while higher numbers correspond to higher verbosity.")
        private int vlevel;

        private Cargo.Heuristics h;

        // A problem is defined by an unchanegable set of object ( planes, cargoes,
        // airports )
        //
        private String[] cargoes;
        private String[] planes;
        private String[] airports;

        private Map<String, String> at;
        private Map<String, String> in;
        private static Map<String, String> goalstate;

        /**
         * @TODO: Get Initial State from File
         */
        private void setInitialState() {
                this.planes = new String[] { "FIRST_PLANE", "SECOND_PLANE" };
                this.airports = new String[] { "JFK", "Heathrow" };
                this.cargoes = new String[] { "FIRST_CARGO", "SECOND_CARGO" };
                this.at = Map.of("FIRST_CARGO", "JFK", "SECOND_CARGO", "Heathrow");
        }

        private static void setGoalState() {
                goalstate = Map.of("FIRST_CARGO", "JFK", "SECOND_CARGO", "Heathrow");
        }

        /**
         * @TODO: Replace map with State Class
         **/
        public boolean isGoal(Map<String, String> state) {
                return state.equals(goalstate);
        }

        public Cargo() {
                super("Cargo");
                setInitialState();
                setGoalState();
                CargoAction.CargoActionFactory.initializeActions(airports, cargoes, planes);

                ArrayList<CargoAction.FlyCargoAction> flyActions = CargoAction.CargoActionFactory.getFlyActions();
                ArrayList<CargoAction.LoadCargoAction> loadActions = CargoAction.CargoActionFactory.getLoadActions();
                ArrayList<CargoAction.UnloadCargoAction> unloadActions = CargoAction.CargoActionFactory
                                .getUnloadActions();

                for (CargoAction.FlyCargoAction flyCargoAction : flyActions) {
                        System.out.println(flyCargoAction + "\n\n");
                }

                for (CargoAction.LoadCargoAction flyCargoAction : loadActions) {
                        System.out.println(flyCargoAction + "\n\n");
                }

                for (CargoAction.UnloadCargoAction flyCargoAction : unloadActions) {
                        System.out.println(flyCargoAction + "\n\n");
                }

        }

        private void checkInput() throws IllegalArgumentException {
                System.out.println("checkInput called");
        }

        @Override
        public Integer call() {
                try {
                        this.checkInput();

                        CargoState initialState;
                        initialState = CargoState.initialState(this, this.at, this.in);

                        System.out.println("[INFO]  Initial state:\n" + initialState);

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
                return null;
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

        public Map<String, String> getAt() {
                return at;
        }

        public void setAt(Map<String, String> at) {
                this.at = at;
        }

        public Map<String, String> getIn() {
                return in;
        }

        public void setIn(Map<String, String> in) {
                this.in = in;
        }

        public Map<String, String> getGoalstate() {
                return goalstate;
        }

        public void setGoalstate(Map<String, String> goalstate) {
                this.goalstate = goalstate;
        }
}
