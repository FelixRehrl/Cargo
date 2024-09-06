package it.uniroma1.di.tmancini.teaching.ai.search;

import java.util.ArrayList;

public abstract class Action {

	public abstract double getCost();

	public abstract ArrayList<Integer> getPreconditions();

	public abstract ArrayList<Integer> getNegative_effects();

	public abstract ArrayList<Integer> getPositive_effects();
}
