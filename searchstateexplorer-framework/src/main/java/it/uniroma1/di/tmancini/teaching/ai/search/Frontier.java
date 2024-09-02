package it.uniroma1.di.tmancini.teaching.ai.search;
import java.util.*;

public abstract class Frontier implements Iterable<SearchNode> {
	
	protected Collection<SearchNode> nodes; // keeps nodes as to perform efficient extractions

	protected Map<State, SearchNode> stateToNodeIndex; // speeds-up...
	
	protected Frontier() {
		this.nodes = null;
		this.stateToNodeIndex = new HashMap<State, SearchNode>();		
	}
	protected void notifyDequeue(SearchNode n) {
		this.stateToNodeIndex.remove( n.getState() );
	}
	protected void notifyEnqueue(SearchNode n) {
		this.stateToNodeIndex.put( n.getState(), n );
	}
	protected void setNodesCollection(Collection<SearchNode> c) {
		this.nodes = c;
	}
	
	public abstract SearchNode dequeue();
	

	public boolean enqueue(SearchNode n) {
		SearchNode oldNode = this.stateToNodeIndex.get(n.getState());
		if (oldNode != null) { 
			if ( oldNode.gValue() > n.gValue() ) {
				this.remove(oldNode);
				return true;
			}
			else return false;
		}
		return true;
	}

	protected void remove(SearchNode n) {
		nodes.remove(n);
		stateToNodeIndex.remove(n.getState());
	}

	
	public Iterator<SearchNode> iterator() {
		return nodes.iterator();
	}
	public int size() {
		return nodes.size();		
	}
	public boolean isEmpty() {
		return nodes.isEmpty();
	}
	public void clear() {
		nodes.clear();
		stateToNodeIndex.clear();
	}
	
}
