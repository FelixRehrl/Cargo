package it.uniroma1.di.tmancini.teaching.ai.search;


public class SearchNode {

	private SearchNode parent;
	private Action action;
	private State state;	
	
	private int depth;
	private double g;
	private double h;
	
	public SearchNode getParent() {
		return parent;
	}
	public Action getAction() {
		return action;
	}
	public State getState() {
		return state;
	}	
	public int getDepth() {
		return depth;
	}
	public double gValue() {
		return g;
	}
	public double hValue() {
		return h;
	}
	public double fValue() {
		return g + h;
	}
	
	
	public SearchNode(State initialState) {
		this.parent = null;
		this.action = null;
		this.state = initialState;
		this.depth = 0;
		this.g = 0;
		this.h = initialState.hValue();
	}
		
	
	public SearchNode(SearchNode parent, Action a) {
		this.parent = parent;
		this.action = a;
		this.state = parent.getState().resultingState(a);
		this.depth = parent.depth+1;
		this.g = parent.g + a.getCost();
		this.h = state.hValue();
	}

	public String toString() {
		return toStringWithPrefix("");
	}
	public String toStringWithPrefix(String prefix) {
		String s = prefix + "begin:" +
				"\n" + prefix + " - id: " + this.hashCode() +
				"\n" + prefix + " - parent id: " +
				(parent != null ? parent.hashCode() : "null") +
				"\n" + prefix + " - last action: " +
				(action != null ? this.action : "null") +
				"\n" + prefix + " - state (hash=" + this.state.hashCode() +
				"):\n" +
				this.state.toStringWithPrefix(prefix) +
				"\n" + prefix + " - depth: " + this.depth +
				"\n" + prefix + " - gValue: " + this.g +
				"\n" + prefix + " - hValue: " + this.h +
				"\n" + prefix + "end.\n";
		return s;
	}
	
}