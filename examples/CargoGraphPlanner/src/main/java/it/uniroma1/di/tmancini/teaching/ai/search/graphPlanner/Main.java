package it.uniroma1.di.tmancini.teaching.ai.search.graphPlanner;

import it.uniroma1.di.tmancini.teaching.ai.search.cargo.Cargo;
import it.uniroma1.di.tmancini.teaching.ai.search.cargo.CargoAction;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import picocli.CommandLine;

/**
 * Main class for running the Cargo problem with graph-based planning.
 * Implements the Callable interface to support command-line execution.
 */
public class Main implements Callable<Integer> {

        @CommandLine.Option(names = { "-f",
                        "--file" }, defaultValue = "NULL", description = "The file presenting the input data (planes, airports, cargoes) and the initial state")
        private String file_path;

        private Cargo problem;
        private static ArrayList<String> all_propositions = new ArrayList<>();

        /**
         * Entry point for the program. Executes the graph planning and prints the
         * output.
         * 
         * @param args command-line arguments.
         */
        public static void main(String[] args) {
                printOutputHeader();
                int exitCode = new CommandLine(new Main()).execute(args);
                System.out.printf("%nExecution completed. Exiting %s errors.%n", exitCode > 0 ? "with" : "without");
                System.exit(exitCode);
        }

        /**
         * Initializes the cargo problem, builds the planning graph, and calculates the
         * h-level.
         * 
         * @return 1 if the execution completes successfully.
         */
        @Override
        public Integer call() throws Exception {
                intilize_cargo_problem();

                Set<Action> actions = create_graph_planning_actions();
                Set<Proposition> initial_state = get_initial_state_propositions();

                PlanningGraph planningGraph = new PlanningGraph(initial_state, actions, 50);
                planningGraph.printPlanningGraph();

                Set<Proposition> goal = get_goal_state_propositions();

                int hLevel = planningGraph.hLevel(goal);

                System.out.println("\n\n hLevel: " + hLevel);

                return 1;
        }

        /**
         * Retrieves the initial state as a set of propositions from the cargo problem.
         * 
         * @return the initial state as a set of Proposition objects.
         */
        private Set<Proposition> get_initial_state_propositions() {
                ArrayList<String> props_string = this.problem.get_initial_state_fluents();
                return props_string.stream().map(Proposition::new).collect(Collectors.toSet());
        }

        /**
         * Retrieves the goal state as a set of propositions from the cargo problem.
         * 
         * @return the goal state as a set of Proposition objects.
         */
        private Set<Proposition> get_goal_state_propositions() {
                ArrayList<String> props_string = this.problem.get_goal_fluents_as_strings();
                return props_string.stream().map(Proposition::new).collect(Collectors.toSet());
        }

        /**
         * Creates a set of actions for the graph planning process from the cargo
         * actions.
         * 
         * @return a set of Action objects for the planning graph.
         */
        private Set<Action> create_graph_planning_actions() {
                Set<Action> actions = new HashSet<>();

                ArrayList<CargoAction> cargo_actions = new ArrayList<>();
                cargo_actions.addAll(CargoAction.CargoActionFactory.getFlyActions());
                cargo_actions.addAll(CargoAction.CargoActionFactory.getLoadActions());
                cargo_actions.addAll(CargoAction.CargoActionFactory.getUnloadActions());

                for (CargoAction cargoAction : cargo_actions) {
                        Set<Proposition> preconditions = cargoAction.getStringPreconditions().stream()
                                        .map(Proposition::new)
                                        .collect(Collectors.toSet());

                        Set<Proposition> effects = cargoAction.getStringPositiveEffects().stream()
                                        .map(Proposition::new)
                                        .collect(Collectors.toSet());
                        effects.addAll(cargoAction.getStringNegativeEffects().stream()
                                        .map(Proposition::new)
                                        .collect(Collectors.toSet()));

                        actions.add(new Action(cargoAction.toString(), preconditions, effects));
                }

                actions.addAll(create_persistent_actions());
                return actions;
        }

        /**
         * Creates a set of persistent actions that maintain state in the planning
         * graph.
         * 
         * @return a set of persistent Action objects.
         */
        private Set<Action> create_persistent_actions() {
                Set<Action> actions = new HashSet<>();

                for (String prop : all_propositions) {
                        ArrayList<Proposition> preconditions = new ArrayList<>();
                        ArrayList<Proposition> effects = new ArrayList<>();

                        preconditions.add(new Proposition(prop));
                        preconditions.add(new Proposition("!" + prop));
                        effects.add(new Proposition(prop));
                        effects.add(new Proposition("!" + prop));

                        for (int i = 0; i < preconditions.size(); i++) {
                                Set<Proposition> prec = new HashSet<>();
                                prec.add(preconditions.get(i));
                                Set<Proposition> eff = new HashSet<>();
                                eff.add(effects.get(i));

                                actions.add(new Action((i % 2 == 0 ? "" : "!") + prop + "_persistent", prec, eff));
                        }
                }
                return actions;
        }

        /**
         * Initializes the Cargo problem by loading data from the input file and setting
         * up actions.
         */
        private void intilize_cargo_problem() {
                this.problem = new Cargo();
                problem.instaniate_problem(this.file_path);
                problem.initialize_fly_actions();
                problem.initialize_load_and_unload_actions();
                all_propositions = problem.get_predicates_verbose();
        }

        /**
         * Prints the header for the output of the graph planning process.
         */
        private static void printOutputHeader() {
                System.out.println("\n=======================================================" +
                                "=\n=\t\tGraphPlanning\t\t      =\n" +
                                "=\t\t  Example: Cargo\t\t      =\n" +
                                "= Computer Science Dept - Sapienza University of Rome =\n" +
                                "=======================================================\n");
        }
}
