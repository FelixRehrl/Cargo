package it.uniroma1.di.tmancini.teaching.ai.search;

import java.util.*;

public abstract class State implements Cloneable {
	private Problem problem;
	
	public State(Problem p) {
		this.problem = p;
	}
	
	public Problem getProblem() {
		return this.problem;
	}
	
	public abstract Collection<? extends Action> executableActions();
	public abstract State resultingState(Action a);
	public abstract boolean isGoal();
	
	
	public double hValue() {
		throw new UnsupportedOperationException("Needs to be implemented by non-abstract subclasses");
	}


	public boolean equals(Object o) {
		throw new UnsupportedOperationException("Needs to be implemented by non-abstract subclasses");
	}
	public int hashcode() {
		throw new UnsupportedOperationException("Needs to be implemented by non-abstract subclasses");
	}
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toString() {
		throw new UnsupportedOperationException("Needs to be implemented by non-abstract subclasses");
	}	
	
	public String toStringWithPrefix(String prefix) {
		throw new UnsupportedOperationException("Needs to be implemented by non-abstract subclasses");
	}		
}
