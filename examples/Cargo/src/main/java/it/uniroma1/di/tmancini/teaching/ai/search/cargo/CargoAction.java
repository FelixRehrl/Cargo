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

		private final String from;
		private final String to;
		private final String plane;

		public FlyCargoAction(String from, String to, String plane) {
			this.from = from;
			this.to = to;
			this.plane = plane;
		}

		public String getFrom() {
			return from;
		}

		public String getTo() {
			return to;
		}

		public String getPlane() {
			return plane;
		}

		@Override
		public double getCost() {
			return 1;
		}

		@Override
		public String toString() {
			return String.format("FlyCargoAction from %s to %s using plane %s", from, to, plane);
		}
	}

	public static class LoadCargoAction extends CargoAction {

		private final String cargo;
		private final String airport;
		private final String plane;

		public LoadCargoAction(String cargo, String airport, String plane) {
			this.cargo = cargo;
			this.airport = airport;
			this.plane = plane;
		}

		public String getCargo() {
			return cargo;
		}

		public String getAirport() {
			return airport;
		}

		public String getPlane() {
			return plane;
		}

		@Override
		public double getCost() {
			return 1;
		}

		@Override
		public String toString() {
			return String.format("LoadCargoAction %s at %s on plane %s", cargo, airport, plane);
		}
	}

	public static class UnloadCargoAction extends CargoAction {

		private final String cargo;
		private final String airport;
		private final String plane;

		public UnloadCargoAction(String cargo, String airport, String plane) {
			this.cargo = cargo;
			this.airport = airport;
			this.plane = plane;
		}

		public String getCargo() {
			return cargo;
		}

		public String getAirport() {
			return airport;
		}

		public String getPlane() {
			return plane;
		}

		@Override
		public double getCost() {
			return 1;
		}

		@Override
		public String toString() {
			return String.format("UnloadCargoAction %s at %s from plane %s", cargo, airport, plane);
		}
	}

	public static class CargoActionFactory {

		private static ArrayList<FlyCargoAction> flyActions = new ArrayList<>();
		private static ArrayList<LoadCargoAction> loadActions = new ArrayList<>();
		private static ArrayList<UnloadCargoAction> unloadActions = new ArrayList<>();

		public static void initializeActions(String[] airports, String[] cargoes, String[] planes) {

			// Initialize all possibel FlyCargoActions
			for (String plane : planes) {
				for (String airport_from : airports) {
					for (String airport_to : airports) {
						if (airport_to != airport_from) {
							flyActions.add(new FlyCargoAction(airport_from, airport_to, plane));
						}
					}
				}
			}

			// Initialize all possibel LoadCargoActions
			for (String plane : planes) {
				for (String cargo : cargoes) {
					for (String airport : airports) {
						loadActions.add(new LoadCargoAction(cargo, airport, plane));
					}
				}
			}
			// Initialize all possibel UnLoadCargoActions
			for (String plane : planes) {
				for (String cargo : cargoes) {
					for (String airport : airports) {
						unloadActions.add(new UnloadCargoAction(cargo, airport, plane));
					}
				}
			}
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
