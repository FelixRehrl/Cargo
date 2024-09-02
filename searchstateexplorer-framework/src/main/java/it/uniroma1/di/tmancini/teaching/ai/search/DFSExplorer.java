package it.uniroma1.di.tmancini.teaching.ai.search;

public class DFSExplorer extends SearchStateExplorer {

	public DFSExplorer(Problem p, Integer maxDepth, String outFileName) {	
		super(p, maxDepth, outFileName);
		super.setFrontier( new LIFOFrontier() );
	}
	public DFSExplorer(Problem p, Integer maxDepth) {
		this(p, maxDepth, null);
	}
	public DFSExplorer(Problem p) {
		this(p, null, null);
	}
	
	public String toString() {
		return "Depth first search";
	}
	
} //:~