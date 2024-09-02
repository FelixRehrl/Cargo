package it.uniroma1.di.tmancini.teaching.ai.search;


public class AstarExplorer extends SearchStateExplorer {

	public AstarExplorer(Problem p, Integer maxDepth, String outFileName) {	
		super(p, maxDepth,outFileName);
		super.setFrontier( new MinFcostFrontier() );
	}
	public AstarExplorer(Problem p, Integer maxDepth) {
		this(p, maxDepth, null);
	}
	public AstarExplorer(Problem p) {
		this(p, null, null);
	}
	
	public String toString() {
		return "A* search";
	}
	
} //:~