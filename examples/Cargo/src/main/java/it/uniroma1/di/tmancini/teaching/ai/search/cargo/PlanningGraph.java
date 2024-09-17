package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.util.*;

/**
 * Represents a proposition in the planning graph.
 */
class Proposition {
        String name;

        /**
         * Creates a new proposition with the given name.
         *
         * @param name the name of the proposition.
         */
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

        /**
         * Checks if two propositions are opposites.
         *
         * @param prop1 the proposition to compare.
         * @return true if the propositions are opposites, false otherwise.
         */
        public boolean areOpposites(Proposition prop1) {
                return (prop1.name.equals("!" + this.name) || this.name.equals("!" + prop1.name));
        }
}

/**
 * Represents an action in the planning graph.
 */
class Planning_Action {

        String name;
        Set<Proposition> preconditions;
        Set<Proposition> effects;

        /**
         * Creates a new action with the given name, preconditions, and effects.
         *
         * @param name          the name of the action.
         * @param preconditions the preconditions for the action.
         * @param effects       the effects of the action.
         */
        public Planning_Action(String name, Set<Proposition> preconditions, Set<Proposition> effects) {
                this.name = name;
                this.preconditions = preconditions;
                this.effects = effects;
        }

        /**
         * Checks if the action can be applied given the current state.
         *
         * @param currentState the current set of propositions.
         * @return true if the action can be applied, false otherwise.
         */
        public boolean canBeApplied(Set<Proposition> currentState) {
                return currentState.containsAll(preconditions);
        }

        @Override
        public String toString() {
                return name;
        }

        public Set<Proposition> getPreconditions() {
                return preconditions;
        }

        @Override
        public boolean equals(Object obj) {
                if (this == obj)
                        return true;
                if (!(obj instanceof Planning_Action))
                        return false;
                Planning_Action action = (Planning_Action) obj;
                return this.name.equals(action.name);
        }

        @Override
        public int hashCode() {
                return Objects.hash(name, preconditions, effects);
        }
}

/**
 * Represents mutual exclusion (mutex) relationships between actions or
 * propositions.
 */
class Mutex {
        Set<MutexActionLink> mutexActionLinks = new HashSet<>();
        Set<MutexPropositionLink> mutexPropositionLinks = new HashSet<>();

        /**
         * Adds a mutex link between two actions.
         *
         * @param a1 the first action.
         * @param a2 the second action.
         */
        public void addActionMutexLink(Planning_Action a1, Planning_Action a2) {
                mutexActionLinks.add(new MutexActionLink(a1, a2));
        }

        /**
         * Adds a mutex link between two propositions.
         *
         * @param p1 the first proposition.
         * @param p2 the second proposition.
         */
        public void addPropositionMutexLink(Proposition p1, Proposition p2) {
                mutexPropositionLinks.add(new MutexPropositionLink(p1, p2));
        }

        /**
         * Checks if two actions are mutex (mutually exclusive).
         *
         * @param a1 the first action.
         * @param a2 the second action.
         * @return true if the actions are mutex, false otherwise.
         */
        public boolean areActionsMutex(Planning_Action a1, Planning_Action a2) {
                return mutexActionLinks.contains(new MutexActionLink(a1, a2));
        }

        /**
         * Checks if two propositions are mutex.
         *
         * @param p1 the first proposition.
         * @param p2 the second proposition.
         * @return true if the propositions are mutex, false otherwise.
         */
        public boolean arePropositionsMutex(Proposition p1, Proposition p2) {
                return mutexPropositionLinks.contains(new MutexPropositionLink(p1, p2));
        }

        /**
         * Represents a mutual exclusion (mutex) link between two actions.
         */
        class MutexActionLink {
                Planning_Action a1;
                Planning_Action a2;

                public MutexActionLink(Planning_Action a1, Planning_Action a2) {
                        this.a1 = a1;
                        this.a2 = a2;
                }

                @Override
                public boolean equals(Object obj) {
                        if (this == obj)
                                return true;
                        if (!(obj instanceof MutexActionLink))
                                return false;
                        MutexActionLink mal = (MutexActionLink) obj;
                        return (this.a1.equals(mal.a1) && this.a2.equals(mal.a2)) ||
                                        (this.a1.equals(mal.a2) && this.a2.equals(mal.a1));
                }
        }

        /**
         * Represents a mutual exclusion (mutex) link between two propositions.
         */
        class MutexPropositionLink {
                Proposition p1;
                Proposition p2;

                public MutexPropositionLink(Proposition p1, Proposition p2) {
                        this.p1 = p1;
                        this.p2 = p2;
                }

                @Override
                public boolean equals(Object obj) {
                        if (this == obj)
                                return true;
                        if (!(obj instanceof MutexPropositionLink))
                                return false;
                        MutexPropositionLink mpl = (MutexPropositionLink) obj;
                        return (this.p1.equals(mpl.p1) && this.p2.equals(mpl.p2)) ||
                                        (this.p1.equals(mpl.p2) && this.p2.equals(mpl.p1));
                }
        }
}

/**
 * Represents a planning graph.
 */
public class PlanningGraph {

        List<Set<Proposition>> propositionLayers = new ArrayList<>();
        List<Set<Planning_Action>> actionLayers = new ArrayList<>();
        List<Mutex> mutexLayers = new ArrayList<>();
        Set<Proposition> initial_state_propositions = new HashSet<>();

        /**
         * Constructs a new PlanningGraph.
         *
         * @param initialState the initial set of propositions.
         * @param actions      the set of actions available.
         * @param depth        the depth of the planning graph.
         */
        public PlanningGraph(Set<Proposition> initialState, Set<Planning_Action> actions, int depth) {
                Set<Proposition> currentPropositions = new HashSet<>(initialState);
                propositionLayers.add(currentPropositions);

                for (int i = 0; i < depth; i++) {
                        Mutex mutexLayer = new Mutex();
                        Set<Planning_Action> newActions = new HashSet<>();

                        if (i > 0) {
                                Set<Planning_Action> oldActions = actionLayers.get(actionLayers.size() - 1);
                                newActions.addAll(oldActions);
                        }

                        Set<Proposition> oldPropositions = propositionLayers.get(propositionLayers.size() - 1);
                        Set<Proposition> newPropositions = new HashSet<>(oldPropositions);

                        for (Planning_Action action : actions) {
                                if (action.canBeApplied(currentPropositions)) {
                                        newActions.add(action);
                                        newPropositions.addAll(action.effects);

                                        for (Planning_Action otherAction : newActions) {
                                                if (action != otherAction && areActionsMutex(action, otherAction)) {
                                                        mutexLayer.addActionMutexLink(action, otherAction);
                                                }
                                        }
                                }
                        }

                        actionLayers.add(newActions);
                        propositionLayers.add(newPropositions);
                        mutexLayers.add(mutexLayer);
                        currentPropositions = newPropositions;

                        for (Proposition p1 : newPropositions) {
                                for (Proposition p2 : newPropositions) {
                                        if (!p1.equals(p2) && arePropositionsMutex(p1, p2, newActions)) {
                                                mutexLayer.addPropositionMutexLink(p1, p2);
                                        }
                                }
                        }

                        if (oldPropositions.equals(newPropositions)) {
                                break;
                        }
                }
        }

        /**
         * Checks if two actions are mutually exclusive (mutex).
         *
         * @param a1 the first action.
         * @param a2 the second action.
         * @return true if the actions are mutex, false otherwise.
         */
        private boolean areActionsMutex(Planning_Action a1, Planning_Action a2) {
                return inconsistent_effect(a1, a2) || interference(a1, a2) || competing_needs(a1, a2);
        }

        /**
         * Checks for competing needs between two actions.
         *
         * @param a1 the first action.
         * @param a2 the second action.
         * @return true if there are competing needs, false otherwise.
         */
        private boolean competing_needs(Planning_Action a1, Planning_Action a2) {
                for (Proposition precondition1 : a1.preconditions) {
                        for (Proposition precondition2 : a2.preconditions) {
                                if (precondition1.areOpposites(precondition2)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        /**
         * Checks for interference between two actions.
         *
         * @param a1 the first action.
         * @param a2 the second action.
         * @return true if there is interference, false otherwise.
         */
        private boolean interference(Planning_Action a1, Planning_Action a2) {
                for (Proposition effect1 : a1.effects) {
                        for (Proposition precondition2 : a2.preconditions) {
                                if (effect1.areOpposites(precondition2)) {
                                        return true;
                                }
                        }
                }
                for (Proposition effect2 : a2.effects) {
                        for (Proposition precondition1 : a1.preconditions) {
                                if (effect2.areOpposites(precondition1)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        /**
         * Checks if two actions have inconsistent effects.
         *
         * @param a1 the first action.
         * @param a2 the second action.
         * @return true if the effects are inconsistent, false otherwise.
         */
        private boolean inconsistent_effect(Planning_Action a1, Planning_Action a2) {
                for (Proposition effect1 : a1.effects) {
                        for (Proposition effect2 : a2.effects) {
                                if (effect1.areOpposites(effect2)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        /**
         * Returns the heuristic level of the goals in the planning graph.
         *
         * @param goals the set of goal propositions.
         * @return the heuristic level of the goals, or Integer.MAX_VALUE if the goals
         *         cannot be reached.
         */
        public int calculate_set_level(Set<Proposition> goals) {
                for (int i = 0; i < propositionLayers.size(); i++) {
                        Set<Proposition> layer = propositionLayers.get(i);
                        if (layer.containsAll(goals) && !areGoalsMutex(goals, i)) {
                                return i;
                        }
                }
                return Integer.MAX_VALUE;
        }

        /**
         * Checks if two propositions are mutually exclusive (mutex).
         * Propositions are mutex if they are opposites or all all the actions
         * supporting a1 are mutex to those supporting a2
         *
         * @param p1 the first proposition.
         * @param p2 the second proposition.
         * @return true if the propositions are mutex, false otherwise.
         */
        private boolean arePropositionsMutex(Proposition p1, Proposition p2, Set<Planning_Action> newActions) {

                // Propositions are opposites
                if (p1.name.equals("!" + p2.name) || p2.name.equals("!" + p1.name)) {
                        return true;
                }

                Set<Planning_Action> actions_supporting_p1 = new HashSet<>();
                Set<Planning_Action> actions_supporting_p2 = new HashSet<>();

                // Returns true if all respective actions are mutex
                for (Planning_Action action : newActions) {

                        if (action.effects.contains(p1)) {
                                actions_supporting_p1.add(action);
                        }

                        if (action.effects.contains(p2)) {
                                actions_supporting_p2.add(action);
                        }
                }

                for (Planning_Action a1 : actions_supporting_p1) {
                        for (Planning_Action a2 : actions_supporting_p2) {
                                if (!areActionsMutex(a1, a2)) {
                                        return false;
                                }
                        }
                }

                return true;
        }

        /**
         * Checks if the goals are mutually exclusive at the given layer.
         *
         * @param goals the set of goal propositions.
         * @param layer the layer to check.
         * @return true if the goals are mutex, false otherwise.
         */
        private boolean areGoalsMutex(Set<Proposition> goals, int layer) {
                Mutex mutexLayer = mutexLayers.get(layer);
                for (Proposition p1 : goals) {
                        for (Proposition p2 : goals) {
                                if (!p1.equals(p2) && mutexLayer.arePropositionsMutex(p1, p2)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        /**
         * Prints the layers of the planning graph.
         */
        public void printPlanningGraph() {
                System.out.println("\n\nPlanning Graph:\n");
                for (int i = 0; i < propositionLayers.size(); i++) {
                        System.out.println("Layer " + i + " - Propositions: " + propositionLayers.get(i) + "\n");
                        if (i < actionLayers.size()) {
                                System.out.println("Layer " + i + " - Actions: " + actionLayers.get(i));
                        }
                }
        }
}
