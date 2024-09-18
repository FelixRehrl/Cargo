package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class for running the Cargo problem with graph-based planning.
 * Implements the Callable interface to support command-line execution.
 */
public class CargoPlanningGraph {

        private Cargo problem;
        private static ArrayList<String> all_propositions = new ArrayList<>();
        private Set<Planning_Action> actions = new HashSet<>();
        private Set<Proposition> goal = new HashSet<>();

        public CargoPlanningGraph(Cargo c) {
                this.problem = c;
                this.intilize_cargo_planning_graph();
        }

        /**
         * Initializes the cargo problem, builds the planning graph, and calculates the
         * h-level.
         * 
         * @return 1 if the execution completes successfully.
         */
        public void intilize_cargo_planning_graph() {
                all_propositions = problem.get_predicates_verbose();
                this.actions = create_graph_planning_actions();
                this.goal = get_goal_state_propositions();
        }

        public int calculate_set_level(char[] state_) {
                Set<Proposition> state = create_proposition_map_from_state(state_);
                PlanningGraph planningGraph = new PlanningGraph(state, actions, 50);
                int set_level = planningGraph.calculate_set_level(goal);
                return set_level;

        }

        /**
         * @param state char array representation of the cargo state
         * 
         * @return the goal state as a set of Proposition objects.
         */
        private Set<Proposition> create_proposition_map_from_state(char[] state) {

                ArrayList<String> fluents = new ArrayList<>();

                for (int i = 0; i < state.length; i++) {
                        if (state[i] == 'T') {
                                fluents.add(Cargo.getFluentByIndex(i));
                        } else {
                                fluents.add("!" + Cargo.getFluentByIndex(i));
                        }
                }
                return fluents.stream().map(Proposition::new).collect(Collectors.toSet());
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
         * @return a set of Planning_Action objects for the planning graph.
         */
        private Set<Planning_Action> create_graph_planning_actions() {
                Set<Planning_Action> actions = new HashSet<>();

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

                        actions.add(new Planning_Action(cargoAction.toString(), preconditions, effects));
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
        private Set<Planning_Action> create_persistent_actions() {
                Set<Planning_Action> actions = new HashSet<>();

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

                                actions.add(new Planning_Action((i % 2 == 0 ? "" : "!") + prop + "_persistent", prec,
                                                eff));
                        }
                }
                return actions;
        }

}
