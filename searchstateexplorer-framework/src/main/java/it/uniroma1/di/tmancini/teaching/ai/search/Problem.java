package it.uniroma1.di.tmancini.teaching.ai.search;

public abstract class Problem {

	private String name;
	private State currentState;

	public Problem(String name) {
		this.name = name;
		this.currentState = null;		
	}
	
	public void setCurrentState(State s) {
		if (s.getProblem() != this) throw new RuntimeException("State refers to wrong problem");
		this.currentState = s;
	}

}
