package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.util.*;
import java.util.stream.Collectors;
import it.uniroma1.di.tmancini.teaching.ai.search.*;
import it.uniroma1.di.tmancini.teaching.ai.search.cargo.Cargo.Heuristics;

public class CargoState extends State {

	private boolean debug = false;
	private char[] state;
	private Cargo cargo;
	private int unmet_goals;
	private int set_level;

	private static Random RANDOM = new Random();
	private static boolean use_seed = true;

	public static void setRandomSeed(long seed) {
		CargoState.RANDOM = new Random(seed);

		if (seed == 0) {
			use_seed = false;
		}
	}

	/**
	 * Constructs a new CargoState with the given Cargo Problem and state array.
	 *
	 * @param c     the Cargo instance.
	 * @param state the array representing the current state.
	 */
	public CargoState(Cargo c, char[] state) {
		super(c);
		this.cargo = c;
		this.state = state;

		if (c.getHeuristics() == Heuristics.UNMET_GOALS) {
			this.unmet_goals = calculate_unmet_goals();
		}
		if (c.getHeuristics() == Heuristics.SET_LEVEL) {
			this.set_level = calculate_set_level();
		}

	}

	private int calculate_set_level() {
		CargoPlanningGraph cpg = cargo.getCpg();
		int set_level = cpg.calculate_set_level(this.state);
		return set_level;
	}

	/**
	 * Calculates the number of unmet goals in the current state.
	 *
	 * @return the number of unmet goals.
	 */
	private int calculate_unmet_goals() {
		ArrayList<Integer> goal_fluents = cargo.getGoal_fluents();
		long unmet_goals = goal_fluents.stream().filter(fluent -> state[fluent] != 'T').count();
		return (int) unmet_goals;
	}

	/**
	 * Returns a collection of executable actions from the current state.
	 *
	 * @return a collection of valid CargoActions.
	 */
	public Collection<? extends Action> executableActions() {
		List<CargoAction> result = new ArrayList<>();
		result.addAll(calculate_valid_flyactions());
		result.addAll(calculate_valid_load_actions());
		result.addAll(calculate_valid_unload_actions());
		if (use_seed) {
			Collections.shuffle(result, RANDOM);
		}
		return result;
	}

	/**
	 * Calculates the valid load actions based on the current state.
	 *
	 * @return a list of LoadCargoActions satisfying the preconditions.
	 */
	private ArrayList<CargoAction.LoadCargoAction> calculate_valid_load_actions() {
		return CargoAction.CargoActionFactory.getLoadActions().stream()
				.filter(this::satisfies_preconditions)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Calculates the valid fly actions based on the current state.
	 *
	 * @return a list of FlyCargoActions satisfying the preconditions.
	 */
	private ArrayList<CargoAction.FlyCargoAction> calculate_valid_flyactions() {
		return CargoAction.CargoActionFactory.getFlyActions().stream()
				.filter(this::satisfies_preconditions)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Calculates the valid unload actions based on the current state.
	 *
	 * @return a list of UnloadCargoActions satisfying the preconditions.
	 */
	private ArrayList<CargoAction.UnloadCargoAction> calculate_valid_unload_actions() {
		return CargoAction.CargoActionFactory.getUnloadActions().stream()
				.filter(this::satisfies_preconditions)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Checks if the given action's preconditions are satisfied in the current
	 * state.
	 *
	 * @param action the CargoAction to check.
	 * @return true if all preconditions are satisfied, false otherwise.
	 */
	private boolean satisfies_preconditions(CargoAction action) {
		return action.getPreconditions().stream().allMatch(precondition -> this.state[precondition] == 'T');
	}

	/**
	 * Returns the resulting state after applying the given action.
	 *
	 * @param a the action to apply.
	 * @return the new CargoState after applying the action.
	 */
	public State resultingState(Action a) {
		// CargoState new_state = (CargoState) this.clone();
		CargoState new_state = new CargoState(cargo, state.clone());
		apply_effects(a, new_state);
		return new_state;
	}

	/**
	 * Applies the effects of the given action to the new cargo state.
	 *
	 * @param a               the action to apply.
	 * @param new_cargo_state the new state to modify.
	 */
	private void apply_effects(Action a, CargoState new_cargo_state) {
		char[] new_state = deepCopyCurrentState();

		a.getNegative_effects().forEach(index -> new_state[index] = 'F');
		a.getPositive_effects().forEach(index -> new_state[index] = 'T');

		new_cargo_state.setState(new_state);
	}

	/**
	 * Creates a deep copy of the current state array.
	 *
	 * @return a deep copy of the current state array.
	 */
	private char[] deepCopyCurrentState() {
		return Arrays.copyOf(this.state, this.state.length);
	}

	/**
	 * Checks if the current state meets the goal conditions.
	 *
	 * @return true if all goal conditions are met, false otherwise.
	 */
	public boolean isGoal() {
		return cargo.getGoal_fluents().stream().allMatch(position -> this.state[position] == 'T');
	}

	/**
	 * Returns the heuristic value for the current state.
	 *
	 * @return the heuristic value based on unmet goals or other heuristics.
	 */
	public double hValue() {
		Cargo.Heuristics h = cargo.getHeuristics();
		if (h == null) {
			return 0;
		}
		switch (h) {
			case UNMET_GOALS:
				return unmet_goals;
			case SET_LEVEL:
				return set_level;
			default:
				throw new RuntimeException("Heuristics " + h + " unknown");
		}
	}

	/**
	 * Checks if this state is equal to another object.
	 *
	 * @param o the object to compare.
	 * @return true if the states are equal, false otherwise.
	 */
	public boolean equals(Object o) {
		if (o == null || !o.getClass().equals(this.getClass())) {
			return false;
		}
		CargoState oo = (CargoState) o;
		return Arrays.equals(state, oo.state);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(state);
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	/**
	 * Returns a verbose string representation of the state.
	 *
	 * @return a verbose description of the current state.
	 */
	public String printStateVerbose() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < state.length; i++) {
			if (state[i] == 'T') {
				sb.append(" (" + i + ") " + Cargo.getFluentByIndex(i) + " ");
			}
		}
		return sb.toString();
	}

	/**
	 * Returns a string representation of the current state.
	 *
	 * @return a string listing all positive fluents.
	 */
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
