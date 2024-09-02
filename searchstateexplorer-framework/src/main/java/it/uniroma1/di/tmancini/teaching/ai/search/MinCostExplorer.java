package it.uniroma1.di.tmancini.teaching.ai.search;

public class MinCostExplorer extends SearchStateExplorer {

	public MinCostExplorer(Problem p, Integer maxDepth, String outFileName) {	
		super(p, maxDepth, outFileName);
		super.setFrontier( new MinGcostFrontier() );
	}
	
	public MinCostExplorer(Problem p, Integer maxDepth) {
		this(p, maxDepth, null);
	}
	public MinCostExplorer(Problem p) {
		this(p, null, null);
	}
	
	public String toString() {
		return "Min-cost search";
	}
	
} //:~