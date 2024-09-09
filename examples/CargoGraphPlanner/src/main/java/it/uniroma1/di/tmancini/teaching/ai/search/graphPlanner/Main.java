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

        private static ArrayList<String> all_propositions = new ArrayList<>();

        public static void main(String[] args) {

                int exitCode = new CommandLine(new Main()).execute(args);

        }

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

        private Set<Proposition> get_initial_state_propositions() {

                ArrayList<String> props_string = this.problem.get_initial_state_fluents();
                Set<Proposition> props = props_string.stream().map(string -> new Proposition(string))
                                .collect(Collectors.toSet());
                return props;
        }

        private Set<Proposition> get_goal_state_propositions() {

                ArrayList<String> props_string = this.problem.get_goal_fluents_as_strings();

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
                                effects.add(new Proposition(pos_effect));
                        }

                        actions.add(new Action(cargoAction.toString(), preconditions, effects));
                }

                Set<Action> persistent_actions = create_persistent_actions();
                actions.addAll(persistent_actions);

                return actions;
        }

        private Set<Action> create_persistent_actions() {

                Set<Action> actions = new HashSet<>();

                for (String prop : all_propositions) {
                        ArrayList<Proposition> preconditions = new ArrayList<Proposition>();
                        ArrayList<Proposition> effects = new ArrayList<Proposition>();

                        preconditions.add(new Proposition(prop));
                        preconditions.add(new Proposition("!" + prop));
                        effects.add(new Proposition(prop));
                        effects.add(new Proposition("!" + prop));

                        for (int i = 0; i < preconditions.size(); i++) {

                                Set<Proposition> prec = new HashSet<>();
                                prec.add(preconditions.get(i));
                                Set<Proposition> eff = new HashSet<>();
                                prec.add(effects.get(i));

                                actions.add(new Action((i % 2 == 0 ? "" : "!") + prop + "_persistent", prec, eff));

                        }

                }
                return actions;

        }

        private void intilize_cargo_problem() {
                this.problem = new Cargo();
                problem.instaniate_problem(this.file_path);
                problem.initialize_fly_actions();
                problem.initialize_load_and_unload_actions();
                all_propositions = problem.get_predicates_verbose();
        }
}
