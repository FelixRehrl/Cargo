package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.util.*;
import it.uniroma1.di.tmancini.teaching.ai.search.*;

public abstract class CargoAction extends Action {

	public abstract double getCost();

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

		int at_plane_airport_1;
		int at_plane_airport_2;

		ArrayList<Integer> preconditions = new ArrayList<Integer>();
		ArrayList<Integer> negative_effects = new ArrayList<Integer>();
		ArrayList<Integer> positive_effects = new ArrayList<Integer>();

		public FlyCargoAction(int at_plane_airport_1, int at_plane_airport_2) {

			this.preconditions.add(at_plane_airport_1);
			this.negative_effects.add(at_plane_airport_1);
			this.positive_effects.add(at_plane_airport_2);

			this.at_plane_airport_1 = at_plane_airport_1;
			this.at_plane_airport_2 = at_plane_airport_2;
		}

		@Override
		public double getCost() {
			return 1;
		}

		@Override
		public String toString() {
			return Cargo.getFluentByIndex(at_plane_airport_1) + " ---> " + Cargo.getFluentByIndex(at_plane_airport_2);
		}

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

	}

	public static class LoadCargoAction extends CargoAction {

		int at_plane_airport;
		int at_cargo_airport;
		int in_plane_cargo;

		ArrayList<Integer> preconditions = new ArrayList<Integer>();
		ArrayList<Integer> negative_effects = new ArrayList<Integer>();
		ArrayList<Integer> positive_effects = new ArrayList<Integer>();

		public LoadCargoAction(int at_plane_airport, int at_cargo_airport, int in_plane_cargo) {
			preconditions.add(at_plane_airport);
			preconditions.add(at_cargo_airport);

			negative_effects.add(at_cargo_airport);

			positive_effects.add(in_plane_cargo);

			this.at_plane_airport = at_plane_airport;
			this.at_cargo_airport = at_cargo_airport;
			this.in_plane_cargo = in_plane_cargo;

		}

		@Override
		public double getCost() {
			return 1;
		}

		@Override
		public String toString() {
			return Cargo.getFluentByIndex(this.at_plane_airport) + "AND " + Cargo.getFluentByIndex(this.at_cargo_airport)
					+ " --> "
					+ Cargo.getFluentByIndex(this.in_plane_cargo);
		}

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

	}

	public static class UnloadCargoAction extends CargoAction {

		int at_plane_airport;
		int in_plane_cargo;
		int at_cargo_airport;

		ArrayList<Integer> preconditions = new ArrayList<Integer>();
		ArrayList<Integer> negative_effects = new ArrayList<Integer>();
		ArrayList<Integer> positive_effects = new ArrayList<Integer>();

		public UnloadCargoAction(int at_plane_airport, int in_plane_cargo, int at_cargo_airport) {

			preconditions.add(at_plane_airport);
			preconditions.add(in_plane_cargo);

			negative_effects.add(in_plane_cargo);
			positive_effects.add(at_cargo_airport);

			this.at_plane_airport = at_plane_airport;
			this.in_plane_cargo = in_plane_cargo;
			this.at_cargo_airport = at_cargo_airport;
		}

		@Override
		public double getCost() {
			return 1;
		}

		@Override
		public String toString() {
			return Cargo.getFluentByIndex(this.at_plane_airport) + "AND " + Cargo.getFluentByIndex(this.in_plane_cargo)
					+ " --> "
					+ Cargo.getFluentByIndex(this.at_cargo_airport);
		}

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
