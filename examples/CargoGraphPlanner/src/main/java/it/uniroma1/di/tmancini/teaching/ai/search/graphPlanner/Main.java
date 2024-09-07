package it.uniroma1.di.tmancini.teaching.ai.search.graphPlanner;

import it.uniroma1.di.tmancini.teaching.ai.search.cargo.Cargo;
import it.uniroma1.di.tmancini.teaching.ai.search.cargo.CargoAction;
import it.uniroma1.di.tmancini.teaching.ai.search.cargo.CargoState;
import it.uniroma1.di.tmancini.teaching.ai.search.cargo.CargoFileParser;

import java.util.*;
import java.util.concurrent.Callable;

import picocli.CommandLine;

public class Main implements Callable {

        @CommandLine.Option(names = { "-f",
                        "--file" }, defaultValue = "NULL", description = "The file presenting the input data ( planes, airports, cargoes ) and the initial state")

        private String file_path;

        private String[] planes;
        private String[] cargoes;
        private String[] airports;

        private ArrayList<String> initial_fluents;
        private ArrayList<String> goal_fluents;
        private ArrayList<Integer> goal_indexes;

        private int state_size;

        // Every
        private static ArrayList<String> predicates_verbose = new ArrayList<>();

        public static void main(String[] args) {

                int exitCode = new CommandLine(new Main()).execute(args);

                Cargo problem = new Cargo();
                problem.instaniate_problem();
                predicates_verbose = Cargo.get_predicates_verbose();
                problem.initialize_fly_actions();
                problem.initialize_load_and_unload_actions();

                System.out.println(CargoAction.CargoActionFactory.getFlyActions());
                // Define propositions

                // Define actions
                // Set<Proposition> preconditions1 = new HashSet<>(Arrays.asList(p1));
                // Set<Proposition> effects1 = new HashSet<>(Arrays.asList(p2, p3));
                // Action action1 = new Action("MoveAtoB", preconditions1, effects1);
                //
                // Set<Proposition> preconditions2 = new HashSet<>(Arrays.asList(p2));
                // Set<Proposition> effects2 = new HashSet<>(Arrays.asList(p1, p4));
                // Action action2 = new Action("MoveBtoA", preconditions2, effects2);
                //
                // Set<Action> actions = new HashSet<>(Arrays.asList(action1, action2));
                //
                // // Initial state
                // Set<Proposition> initialState = new HashSet<>(Arrays.asList(p1));
                //
                // // Build the planning graph
                // PlanningGraph planningGraph = new PlanningGraph(initialState, actions, 3);
                // planningGraph.printPlanningGraph();
        }

        @Override
        public Integer call() throws Exception {
                return 1;
        }
}
