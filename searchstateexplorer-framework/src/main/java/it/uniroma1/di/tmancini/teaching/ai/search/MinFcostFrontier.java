package it.uniroma1.di.tmancini.teaching.ai.search;
import java.util.*;

public class MinFcostFrontier extends Frontier {
	
	public MinFcostFrontier() {
		super();
		
		Comparator<SearchNode> comparator = (n1, n2) -> {
			if (n1.fValue() == n2.fValue()) return 0;
			return (int)(Math.ceil(n1.fValue() - n2.fValue()));
		};
		
		setNodesCollection( new PriorityQueue<>(1000, comparator) );
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