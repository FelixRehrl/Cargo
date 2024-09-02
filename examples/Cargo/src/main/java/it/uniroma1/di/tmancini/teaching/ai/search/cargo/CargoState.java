package it.uniroma1.di.tmancini.teaching.ai.search.cargo;

import java.io.InterruptedIOException;
import java.util.*;
import it.uniroma1.di.tmancini.teaching.ai.search.*;

public class CargoState extends State {

	private Map<String, String> at;
	private Map<String, String> in;

	public CargoState(Cargo c) {
		super(c);
	}

	public Collection<? extends Action> executableActions() {

		List<CargoAction> result = new ArrayList<CargoAction>();

		ArrayList<CargoAction> executableFlyActions = calculateFlyActions();
		ArrayList<CargoAction> executableLoadActions = calculateUnloadActions();
		ArrayList<CargoAction> executableUnloadActions = calculateLoadActions();

		result.addAll(executableFlyActions);
		result.addAll(executableUnloadActions);
		result.addAll(executableLoadActions);

		return result;
	}

	private ArrayList<CargoAction> calculateLoadActions() {
		throw new UnsupportedOperationException("Unimplemented method 'calculateLoadActions'");
	}

	private ArrayList<CargoAction> calculateUnloadActions() {
		throw new UnsupportedOperationException("Unimplemented method 'calculateUnloadActions'");
	}

	private ArrayList<CargoAction> calculateFlyActions() {
		throw new UnsupportedOperationException("Unimplemented method 'calculateFlyActions'");
	}

	public State resultingState(Action a) {
		return null;
	}

	public static CargoState initialState(Cargo c, Map<String, String> at, Map<String, String> in) {
		CargoState initialState = new CargoState(c);
		initialState.setAt(at);
		initialState.setIn(in);
		return initialState;
	}

	public boolean isGoal() {
		return true;
	}

	public double hValue() {
		return 1;

	}

	public boolean equals(Object o) {
		return false;

	}

	public int hashCode() {
		return 1;
	}

	public Object clone() {
		return super.clone();
	}

	public String toStringWithPrefix(String prefix) {
		return null;
	}

	public String toString() {
		return toStringWithPrefix("");
	}

	public Map<String, String> getAt() {
		return at;
	}

	public void setAt(Map<String, String> at) {
		this.at = at;
	}

	public Map<String, String> getIn() {
		return in;
	}

	public void setIn(Map<String, String> in) {
		this.in = in;
	}
}
