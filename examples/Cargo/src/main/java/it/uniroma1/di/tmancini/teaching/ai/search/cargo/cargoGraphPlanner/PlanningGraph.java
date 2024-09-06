package it.uniroma1.di.tmancini.teaching.ai.search.cargo.cargoGraphPlanner;

import java.util.*;

class Proposition {
        String name;

        public Proposition(String name) {
                this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj)
                        return true;
                if (!(obj instanceof Proposition))
                        return false;
                Proposition p = (Proposition) obj;
                return this.name.equals(p.name);
        }

        @Override
        public int hashCode() {
                return Objects.hash(name);
        }

        @Override
        public String toString() {
                return name;
        }
}

class Action {

        String name;
        Set<Proposition> preconditions;
        Set<Proposition> effects;

        public Action(String name, Set<Proposition> preconditions, Set<Proposition> effects) {
                this.name = name;
                this.preconditions = preconditions;
                this.effects = effects;
        }

        public boolean canBeApplied(Set<Proposition> currentState) {
                return currentState.containsAll(preconditions);
        }

        @Override
        public String toString() {
                return name;
        }
}

class Mutex {
        Set<Action> mutexActions = new HashSet<>();
        Set<Proposition> mutexPropositions = new HashSet<>();

        public void addActionMutex(Action a1, Action a2) {
                mutexActions.add(a1);
                mutexActions.add(a2);
        }

        public void addPropositionMutex(Proposition p1, Proposition p2) {
                mutexPropositions.add(p1);
                mutexPropositions.add(p2);
        }

        public boolean areActionsMutex(Action a1, Action a2) {
                return mutexActions.contains(a1) && mutexActions.contains(a2);
        }

        public boolean arePropositionsMutex(Proposition p1, Proposition p2) {
                return mutexPropositions.contains(p1) && mutexPropositions.contains(p2);
        }
}

class PlanningGraph {

        List<Set<Proposition>> propositionLayers = new ArrayList<>();
        List<Set<Action>> actionLayers = new ArrayList<>();
        List<Mutex> mutexLayers = new ArrayList<>();

        public PlanningGraph(Set<Proposition> initialState, Set<Action> actions, int depth) {
                // Initialize with the initial state
                Set<Proposition> currentPropositions = new HashSet<>(initialState);
                propositionLayers.add(currentPropositions);

                for (int i = 0; i < depth; i++) {
                        Mutex mutexLayer = new Mutex();
                        Set<Action> newActions = new HashSet<>();
                        Set<Proposition> newPropositions = new HashSet<>();

                        // Add applicable actions to the action layer
                        for (Action action : actions) {
                                if (action.canBeApplied(currentPropositions)) {
                                        newActions.add(action);
                                        newPropositions.addAll(action.effects);

                                        // Check and add mutexes between actions
                                        for (Action otherAction : newActions) {
                                                if (action != otherAction && areActionsMutex(action, otherAction)) {
                                                        mutexLayer.addActionMutex(action, otherAction);
                                                }
                                        }
                                }
                        }

                        actionLayers.add(newActions);
                        propositionLayers.add(newPropositions);
                        mutexLayers.add(mutexLayer);

                        // Set the current propositions to the newly generated set for the next layer
                        currentPropositions = newPropositions;

                        // Add mutexes between propositions (dummy logic)
                        for (Proposition p1 : newPropositions) {
                                for (Proposition p2 : newPropositions) {
                                        if (!p1.equals(p2) && arePropositionsMutex(p1, p2)) {
                                                mutexLayer.addPropositionMutex(p1, p2);
                                        }
                                }
                        }
                }
        }

        // Function to check if two actions are mutex (incompatible), you can customize
        // this
        private boolean areActionsMutex(Action a1, Action a2) {
                // Dummy logic: For this example, actions are mutex if they have contradictory
                // effects
                for (Proposition effect1 : a1.effects) {
                        for (Proposition effect2 : a2.effects) {
                                if (effect1.equals(effect2)) {
                                        return true; // Actions are mutex if they share an effect
                                }
                        }
                }
                return false;
        }

        // Function to check if two propositions are mutex (incompatible), you can
        // customize this
        private boolean arePropositionsMutex(Proposition p1, Proposition p2) {
                // Dummy logic: For this example, propositions are mutex if their names are
                // opposites
                return p1.name.equals("!" + p2.name) || p2.name.equals("!" + p1.name);
        }

        // Print the planning graph for visualization
        public void printPlanningGraph() {
                System.out.println("Planning Graph:");
                for (int i = 0; i < propositionLayers.size(); i++) {
                        System.out.println("Layer " + i + " - Propositions: " + propositionLayers.get(i));
                        if (i < actionLayers.size()) {
                                System.out.println("Layer " + i + " - Actions: " + actionLayers.get(i));
                        }
                }
        }

        public static void main(String[] args) {
                // Define propositions
                Proposition p1 = new Proposition("At(A)");
                Proposition p2 = new Proposition("At(B)");
                Proposition p3 = new Proposition("!At(A)");
                Proposition p4 = new Proposition("!At(B)");

                // Define actions
                Set<Proposition> preconditions1 = new HashSet<>(Arrays.asList(p1));
                Set<Proposition> effects1 = new HashSet<>(Arrays.asList(p2, p3));
                Action action1 = new Action("MoveAtoB", preconditions1, effects1);

                Set<Proposition> preconditions2 = new HashSet<>(Arrays.asList(p2));
                Set<Proposition> effects2 = new HashSet<>(Arrays.asList(p1, p4));
                Action action2 = new Action("MoveBtoA", preconditions2, effects2);

                Set<Action> actions = new HashSet<>(Arrays.asList(action1, action2));

                // Initial state
                Set<Proposition> initialState = new HashSet<>(Arrays.asList(p1));

                // Build the planning graph
                PlanningGraph planningGraph = new PlanningGraph(initialState, actions, 3);
                planningGraph.printPlanningGraph();
        }
}
