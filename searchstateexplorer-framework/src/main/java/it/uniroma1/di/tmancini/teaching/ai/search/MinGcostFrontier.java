package it.uniroma1.di.tmancini.teaching.ai.search;
import java.util.*;


public class MinGcostFrontier extends Frontier {
	
	public MinGcostFrontier() {
		super();
		
		Comparator<SearchNode> comparator = new Comparator<SearchNode>() {
			public int compare(SearchNode n1, SearchNode n2) {
				if (n1.gValue() == n2.gValue()) return 0;
				return (int)(Math.ceil(n1.gValue() - n2.gValue()));
			}
		};
		
		setNodesCollection( new PriorityQueue<SearchNode>(1000, comparator) );
	}
	
	public boolean enqueue(SearchNode n) {		
		boolean result = super.enqueue(n);
		if (!result) return false; // no need to enqueue (a better node referring to the same state is already in the frontier)
		nodes.add(n);
		super.notifyEnqueue(n);
		return true;
	}
	public SearchNode dequeue() {		
		SearchNode result = ((PriorityQueue<SearchNode>)nodes).poll();
		super.notifyDequeue(result);
		return result;
	}
	
	
	
}