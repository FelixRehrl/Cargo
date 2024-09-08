package it.uniroma1.di.tmancini.teaching.ai.search.graphPlanner;

import it.uniroma1.di.tmancini.teaching.ai.search.cargo.Cargo;
import it.uniroma1.di.tmancini.teaching.ai.search.cargo.CargoAction;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import picocli.CommandLine;

public class Main implements Callable {

        @CommandLine.Option(names = { "-f",
                        "--file" }, defaultValue = "NULL", description = "The file presenting the input data ( planes, airports, cargoes ) and the initial state")

        private String file_path;

        private Cargo problem;
        private ArrayList<String> initial_fluents;

        private static ArrayList<String> predicates_verbose = new ArrayList<>();

        public static void main(String[] args) {

                int exitCode = new CommandLine(new Main()).execute(args);

        }

        @Override
        public Integer call() throws Exception {

                intilize_cargo_problem();

                Set<Action> actions = create_graph_planning_actions();
                Set<Proposition> initial_state = get_initial_state_propositions();

                System.out.println(actions);
                System.out.println(initial_state);

                PlanningGraph planningGraph = new PlanningGraph(initial_state, actions, 3);
                planningGraph.printPlanningGraph();

                return 1;
        }

        private Set<Proposition> get_initial_state_propositions() {
                ArrayList<String> props_string = this.problem.get_initial_state_fluents();
                Set<Proposition> props = props_string.stream().map(string -> new Proposition(string))
                                .collect(Collectors.toSet());
                return props;
        }

        private Set<Action> create_graph_planning_actions() {

                Set<Action> actions = new HashSet<>();

                ArrayList<CargoAction> cargo_actions = new ArrayList<>();
                cargo_actions.addAll(CargoAction.CargoActionFactory.getFlyActions());
                cargo_actions.addAll(CargoAction.CargoActionFactory.getLoadActions());
                cargo_actions.addAll(CargoAction.CargoActionFactory.getUnloadActions());

                for (CargoAction cargoAction : cargo_actions) {

                        Set<Proposition> preconditions = new HashSet<>();
                        Set<Proposition> effects = new HashSet<>();

                        for (String precondition : cargoAction.getStringPreconditions()) {
                                preconditions.add(new Proposition(precondition));
                        }

                        for (String neg_effect : cargoAction.getStringNegativeEffects()) {
                                effects.add(new Proposition(neg_effect));
                        }

                        for (String pos_effect : cargoAction.getStringPositiveEffects()) {
                                preconditions.add(new Proposition(pos_effect));
                        }

                        actions.add(new Action(cargoAction.toString(), preconditions, effects));
                }
                return actions;
        }

        private void intilize_cargo_problem() {
                this.problem = new Cargo();
                problem.instaniate_problem(this.file_path);
                problem.initialize_fly_actions();
                problem.initialize_load_and_unload_actions();

        }
}
