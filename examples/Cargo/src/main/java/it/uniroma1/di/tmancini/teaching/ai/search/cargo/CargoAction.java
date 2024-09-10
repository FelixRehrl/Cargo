package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.util.*;
import java.util.stream.Collectors;

import it.uniroma1.di.tmancini.teaching.ai.search.*;

/**
 * Represent an abstract CargoAction. This class is extended by the three
 * classes representing the three possible actions of the problem (
 * FlyCargoAction, LoadCargoAction, UnloadCargoAction )
 */
public abstract class CargoAction extends Action {

	public abstract double getCost();

	/**
	 * Like every action in a classical planning we define preconditions and
	 * effects, again the Integers in this case refert to the index of the
	 * respecitve
	 * fluent in the state which can either be true or false ( TTTFFTFTFTFT )
	 *
	 */
	// ArrayList<Integer> preconditions = new ArrayList<Integer>();
	// ArrayList<Integer> negative_effects = new ArrayList<Integer>();
	// ArrayList<Integer> positive_effects = new ArrayList<Integer>();
	//
	protected ArrayList<Integer> preconditions = new ArrayList<Integer>();
	protected ArrayList<Integer> negative_effects = new ArrayList<Integer>();
	protected ArrayList<Integer> positive_effects = new ArrayList<Integer>();

	@Override
	public ArrayList<Integer> getPreconditions() {
		return preconditions;
	}

	@Override
	public ArrayList<Integer> getNegative_effects() {
		return negative_effects;
	}

	@Override
	public ArrayList<Integer> getPositive_effects() {
		return positive_effects;
	}

	/**
	 * Converts the index-based preconditions into a list of strings ( 1, 2, 3 ) ->
	 * ( AT(CARGO_1, JFK); IN(CARGO_1, PLANE) ) .
	 * 
	 * @return a list of preconditions as strings.
	 */
	public ArrayList<String> getStringPreconditions() {
		return this.preconditions.stream()
				.map(index -> Cargo.getFluentByIndex(index))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Converts the index-based negative effects into a list of strings.
	 * 
	 * @return a list of negative effects as strings.
	 */
	public ArrayList<String> getStringNegativeEffects() {
		return this.negative_effects.stream()
				.map(index -> "!" + Cargo.getFluentByIndex(index))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Converts the index-based positive effects into a list of strings.
	 * 
	 * @return a list of positive effects as strings.
	 */
	public ArrayList<String> getStringPositiveEffects() {
		return this.positive_effects.stream()
				.map(index -> Cargo.getFluentByIndex(index))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof CargoAction && this.hashCode() == o.hashCode();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.toString());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	public static class FlyCargoAction extends CargoAction {

		String plane;
		String airport_dest;

		public FlyCargoAction(int at_plane_airport_1, int at_plane_airport_2) {

			this.preconditions.add(at_plane_airport_1);
			this.negative_effects.add(at_plane_airport_1);
			this.positive_effects.add(at_plane_airport_2);

			// Get the name of plane and airport for printing
			this.plane = Cargo.getObjectsFromProposition(Cargo.getFluentByIndex(at_plane_airport_1)).get(0);
			this.airport_dest = Cargo.getObjectsFromProposition(Cargo.getFluentByIndex(at_plane_airport_2)).get(1);
		}

		@Override
		public double getCost() {
			return 1;
		}

		@Override
		public String toString() {
			return " FLY(" + this.plane + "," + this.airport_dest + ")";
		}

	}

	public static class LoadCargoAction extends CargoAction {

		String cargo;
		String plane;
		String airport;

		public LoadCargoAction(int at_plane_airport, int at_cargo_airport, int in_plane_cargo) {

			preconditions.add(at_plane_airport);
			preconditions.add(at_cargo_airport);

			negative_effects.add(at_cargo_airport);

			positive_effects.add(in_plane_cargo);

			// Get name of objects for toString()
			this.plane = Cargo.getObjectsFromProposition(Cargo.getFluentByIndex(in_plane_cargo)).get(1);
			this.cargo = Cargo.getObjectsFromProposition(Cargo.getFluentByIndex(in_plane_cargo)).get(0);
			this.airport = Cargo.getObjectsFromProposition(Cargo.getFluentByIndex(at_plane_airport)).get(1);

		}

		@Override
		public double getCost() {
			return 1;
		}

		@Override
		public String toString() {
			return " LOAD(" + this.cargo + "," + this.plane + ")\t AT[" + this.airport + "]";
		}

	}

	public static class UnloadCargoAction extends CargoAction {

		String cargo;
		String plane;
		String airport;

		public UnloadCargoAction(int at_plane_airport, int in_plane_cargo, int at_cargo_airport) {

			preconditions.add(at_plane_airport);
			preconditions.add(in_plane_cargo);

			negative_effects.add(in_plane_cargo);
			positive_effects.add(at_cargo_airport);

			// Get name of objects for toString()
			this.plane = Cargo.getObjectsFromProposition(Cargo.getFluentByIndex(in_plane_cargo)).get(1);
			this.cargo = Cargo.getObjectsFromProposition(Cargo.getFluentByIndex(in_plane_cargo)).get(0);
			this.airport = Cargo.getObjectsFromProposition(Cargo.getFluentByIndex(at_plane_airport)).get(1);
		}

		@Override
		public double getCost() {
			return 1;
		}

		@Override
		public String toString() {
			return " UNLOAD(" + this.cargo + "," + this.plane + ")\t AT[" + this.airport + "]";
		}

	}

	public static class CargoActionFactory {

		private static ArrayList<FlyCargoAction> flyActions = new ArrayList<>();
		private static ArrayList<LoadCargoAction> loadActions = new ArrayList<>();
		private static ArrayList<UnloadCargoAction> unloadActions = new ArrayList<>();

		public static void addFlyCargoAction(int at_plane_airport_1, int at_plane_airport_2) {
			flyActions.add(new FlyCargoAction(at_plane_airport_1, at_plane_airport_2));
		}

		public static void addLoadAction(int at_plane_airport, int at_cargo_airport, int in_plane_cargo) {
			loadActions.add(new LoadCargoAction(at_plane_airport, at_cargo_airport, in_plane_cargo));
		}

		public static void addUnloadAction(int at_plane_airport, int in_plane_cargo, int at_cargo_airport) {
			unloadActions.add(new UnloadCargoAction(at_plane_airport, in_plane_cargo, at_cargo_airport));
		}

		public static ArrayList<FlyCargoAction> getFlyActions() {
			return flyActions;
		}

		public static ArrayList<LoadCargoAction> getLoadActions() {
			return loadActions;
		}

		public static ArrayList<UnloadCargoAction> getUnloadActions() {
			return unloadActions;
		}

	}
}
