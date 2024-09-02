package it.uniroma1.di.tmancini.teaching.ai.search;

public class BFSExplorer extends SearchStateExplorer {

	public BFSExplorer(Problem p, Integer maxDepth, String outFileName) {	
		super(p, maxDepth, outFileName);
		super.setFrontier( new FIFOFrontier() );
	}
	public BFSExplorer(Problem p, Integer maxDepth) {
		this(p, maxDepth, null);
	}
	public BFSExplorer(Problem p) {
		this(p, null, null);
	}
	
	public String toString() {
		return "Breadth first search";
	}
	
} //:~