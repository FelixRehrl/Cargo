package it.uniroma1.di.tmancini.teaching.ai.search;
import java.util.*;

public class LIFOFrontier extends Frontier {
	
	public LIFOFrontier() {
		super();
		setNodesCollection( new ArrayDeque<SearchNode>() );
	}
	
	public boolean enqueue(SearchNode n) {		
		boolean result = super.enqueue(n);
		if (!result) return false; // no need to enqueue (a better node referring to the same state is already in the frontier)
		((Deque<SearchNode>)nodes).addLast(n);
		super.notifyEnqueue(n);
		return true;
	}
	public SearchNode dequeue() {		
		SearchNode result = ((Deque<SearchNode>)nodes).removeLast();
		super.notifyDequeue(result);
		return result;
	}
	
	
	
}