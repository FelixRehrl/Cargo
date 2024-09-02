package it.uniroma1.di.tmancini.teaching.ai.search;


public class BestFirstGreedyExplorer extends SearchStateExplorer {

	public BestFirstGreedyExplorer(Problem p, Integer maxDepth, String outFileName) {	
		super(p, maxDepth, outFileName);
		super.setFrontier( new MinHcostFrontier() );
	}
	public BestFirstGreedyExplorer(Problem p, Integer maxDepth) {
		this(p, maxDepth, null);
	}
	public BestFirstGreedyExplorer(Problem p) {
		this(p, null, null);
	}
	
	public String toString() {
		return "Best-first greedy search";
	}
	
} //:~