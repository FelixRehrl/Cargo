package it.uniroma1.di.tmancini.teaching.ai.search.graphPlanner;

import java.util.*;

import javax.swing.plaf.basic.BasicSliderUI.ActionScroller;
import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

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

        public boolean areOpposites(Proposition prop1) {
                if (prop1.name == "!" + this.name || this.name == "!" + prop1.name) {
                        return true;
                }
                return false;
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

        public Set<Proposition> getPreconditions() {
                return preconditions;
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

public class PlanningGraph {

        List<Set<Proposition>> propositionLayers = new ArrayList<>();
        List<Set<Action>> actionLayers = new ArrayList<>();
        List<Mutex> mutexLayers = new ArrayList<>();
        Set<Proposition> initial_state_propositions = new HashSet<>();

        public PlanningGraph(Set<Proposition> initialState, Set<Action> actions, int depth) {
                // Initialize with the initial state
                Set<Proposition> currentPropositions = new HashSet<>(initialState);
                propositionLayers.add(currentPropositions);

                for (int i = 0; i < depth; i++) {

                        Mutex mutexLayer = new Mutex();
                        Set<Action> newActions = new HashSet<>();

                        if (i > 0) {
                                Set<Action> oldActions = actionLayers.get(actionLayers.size() - 1);
                                newActions.addAll(oldActions);
                        }

                        Set<Proposition> oldPropositions = propositionLayers.get(propositionLayers.size() - 1);
                        Set<Proposition> newPropositions = new HashSet<>();

                        newPropositions.addAll(oldPropositions);

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

                        currentPropositions = newPropositions;

                        for (Proposition p1 : newPropositions) {
                                for (Proposition p2 : newPropositions) {
                                        if (!p1.equals(p2) && arePropositionsMutex(p1, p2, newActions)) {
                                                mutexLayer.addPropositionMutex(p1, p2);
                                        }
                                }
                        }

                        // if (oldPropositions == newPropositions || oldPropositions.size() ==
                        // newPropositions.size()) {
                        // break;
                        // }
                }
        }

        private boolean areActionsMutex(Action a1, Action a2) {

                if (inconsistent_effect(a1, a2) || interference(a1, a2) || competing_needs(a1, a2)) {
                        return true;
                }

                return false;

        }

        private boolean competing_needs(Action a1, Action a2) {

                for (Proposition precondition1 : a1.preconditions) {

                        for (Proposition precondition2 : a2.preconditions) {
                                if (precondition1.areOpposites(precondition2)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        private boolean interference(Action a1, Action a2) {

                for (Proposition effect1 : a1.effects) {
                        for (Proposition precondition_2 : a2.preconditions) {
                                if (effect1.areOpposites(precondition_2)) {
                                        return true;
                                }
                        }
                }
                for (Proposition effect2 : a2.effects) {
                        for (Proposition precondition_1 : a1.preconditions) {
                                if (effect2.areOpposites(precondition_1)) {
                                        return true;
                                }
                        }
                }
                return false;

        }

        private boolean inconsistent_effect(Action a1, Action a2) {
                for (Proposition effect1 : a1.effects) {

                        for (Proposition effect2 : a2.effects) {
                                if (effect1.areOpposites(effect2)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        public int hLevel(Set<Proposition> goals) {

                System.out.println(goals);

                for (int i = 0; i < propositionLayers.size() - 1; i++) {

                        Set<Proposition> layer = propositionLayers.get(i);

                        if (layer.containsAll(goals) && !areGoalsMutex(goals, i)) {
                                return i;
                        }
                }
                return Integer.MAX_VALUE;
        }

        private boolean arePropositionsMutex(Proposition p1, Proposition p2, Set<Action> newActions) {

                if (p1.name.equals("!" + p2.name) || p2.name.equals("!" + p1.name)) {
                        return true;
                }

                return false;
                // Set<Action> actions_supporting_p1 = new HashSet<>();
                // Set<Action> actions_supporting_p2 = new HashSet<>();
                //
                // for (Action action : newActions) {
                //
                // if (action.effects.contains(p1)) {
                // actions_supporting_p1.add(action);
                // }
                //
                // if (action.effects.contains(p2)) {
                // actions_supporting_p2.add(action);
                // }
                // }
                //
                // for (Action a1 : actions_supporting_p1) {
                // for (Action a2 : actions_supporting_p2) {
                // if (!areActionsMutex(a1, a2)) {
                // return false;
                // }
                // }
                // }
                //
                // return true;
        }

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

        // Print the planning graph for visualization
        public void printPlanningGraph() {
                System.out.println("\n\nPlanning Graph:\n");
                for (int i = 0; i < propositionLayers.size(); i++) {
                        // System.out.println("Layer " + i + " - Propositions: " +
                        // propositionLayers.get(i) + "\n");
                        // if (i < actionLayers.size()) {
                        // System.out.println("Layer " + i + " - Actions: " + actionLayers.get(i));
                        // }
                }
        }

}
