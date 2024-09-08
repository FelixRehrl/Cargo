package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.util.*;
import java.util.stream.Collectors;
import it.uniroma1.di.tmancini.teaching.ai.search.*;

public class CargoState extends State {

	private boolean debug = false;
	private char[] state;
	private Cargo cargo;
	private int unmet_goals;

	public CargoState(Cargo c, char[] state) {
		super(c);
		this.cargo = c;
		this.state = state;
		this.unmet_goals = calculate_unmet_goals();

	}

	private int calculate_unmet_goals() {
		ArrayList<Integer> goal_fluents = cargo.getGoal_fluents();
		long unmet_goals = goal_fluents.stream().filter(fluent -> state[fluent] != 'T').count();
		return (int) unmet_goals;
	}

	public Collection<? extends Action> executableActions() {

		List<CargoAction> result = new ArrayList<CargoAction>();

		ArrayList<CargoAction.FlyCargoAction> executableFlyActions = calculate_valid_flyactions();
		ArrayList<CargoAction.UnloadCargoAction> executableLoadActions = calculate_valid_unload_actions();
		ArrayList<CargoAction.LoadCargoAction> executableUnloadActions = calculate_valid_load_actions();

		result.addAll(executableFlyActions);
		result.addAll(executableUnloadActions);
		result.addAll(executableLoadActions);

		return result;
	}

	private ArrayList<CargoAction.LoadCargoAction> calculate_valid_load_actions() {

		ArrayList<CargoAction.LoadCargoAction> all_load_actions = CargoAction.CargoActionFactory.getLoadActions();

		ArrayList<CargoAction.LoadCargoAction> valid_load_actions = all_load_actions.stream()
				.filter(action -> satisfies_preconditions(action)).collect(Collectors.toCollection(ArrayList::new));

		return valid_load_actions;
	}

	private ArrayList<CargoAction.FlyCargoAction> calculate_valid_flyactions() {

		ArrayList<CargoAction.FlyCargoAction> all_fly_actions = CargoAction.CargoActionFactory.getFlyActions();

		ArrayList<CargoAction.FlyCargoAction> valid_fly_actions = all_fly_actions.stream()
				.filter(action -> satisfies_preconditions(action)).collect(Collectors.toCollection(ArrayList::new));

		return valid_fly_actions;
	}

	private boolean satisfies_preconditions(CargoAction action) {

		return action.getPreconditions().stream().allMatch(precondition -> this.state[precondition] == 'T');
	}

	private ArrayList<CargoAction.UnloadCargoAction> calculate_valid_unload_actions() {

		ArrayList<CargoAction.UnloadCargoAction> all_load_actions = CargoAction.CargoActionFactory.getUnloadActions();

		ArrayList<CargoAction.UnloadCargoAction> valid_unload_actions = all_load_actions.stream()
				.filter(action -> satisfies_preconditions(action)).collect(Collectors.toCollection(ArrayList::new));

		return valid_unload_actions;
	}

	public State resultingState(Action a) {

		CargoState new_state = (CargoState) this.clone();

		apply_effects(a, new_state);

		if (debug) {
			System.out.println("Action: " + a);
			System.out.println("New State: " + new_state);
		}

		return new_state;
	}

	private void apply_effects(Action a, CargoState new_cargo_state) {

		ArrayList<Integer> postive_effects = a.getPositive_effects();
		ArrayList<Integer> negative_effects = a.getNegative_effects();

		char[] new_state = deepCopyCurrentState();

		for (int index : negative_effects) {
			new_state[index] = 'F';
		}

		for (int index : postive_effects) {
			new_state[index] = 'T';
		}

		new_cargo_state.setState(new_state);
	}

	private char[] deepCopyCurrentState() {
		char[] new_state = new char[this.state.length];
		for (int i = 0; i < this.state.length; i++) {
			new_state[i] = this.state[i];
		}
		return new_state;
	}

	public boolean isGoal() {
		// System.out.println("isGoal called");
		ArrayList<Integer> goal_fluents = this.cargo.getGoal_fluents();

		// System.out.println("Goal Fluents" + goal_fluents);
		//
		// ArrayList<Integer> current_positives = new ArrayList<>();
		// for (int i = 0; i < this.state.length; i++) {
		// if (this.state[i] == 'T') {
		// current_positives.add(i); // Add the index where the character is 'T'
		// }
		// }
		//
		// System.out.println("Current Positives" + current_positives);

		return goal_fluents.stream().allMatch(position -> this.state[position] == 'T');
	}

	public double hValue() {
		Cargo.Heuristics h = cargo.getHeuristics();
		if (h == null) {
			return 0;
		}
		switch (h) {
			case UNMET_GOALS:
				return unmet_goals;
			default:
				throw new RuntimeException("Heuristics " + h + " unknown");
		}

	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!o.getClass().equals(this.getClass()))
			return false;

		CargoState oo = (CargoState) o;

		if (this.hashCode() != oo.hashCode())
			return false;

		return Arrays.equals(state, oo.state);
	}

	@Override
	public int hashCode() {
		int result = 1;
		for (char element : state) {
			result = 31 * result + element;
		}
		return result;
	}

	public Object clone() {
		return super.clone();
	}

	public String toStringWithPrefix(String prefix) {
		return null;
	}

	public String printStateVerbose() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < state.length; i++) {
			if (state[i] == 'T') {
				sb.append(" (" + i + ") " + Cargo.getFluentByIndex(i) + " ");
			}
		}
		return sb.toString();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("\n");
		for (int i = 0; i < state.length; i++) {
			if (state[i] == 'T') {
				sb.append(Cargo.getFluentByIndex(i) + " ");
			}
		}
		return sb.toString();
	}

	public char[] getState() {
		return state;
	}

	public void setState(char[] state) {
		this.state = state;
	}

}
