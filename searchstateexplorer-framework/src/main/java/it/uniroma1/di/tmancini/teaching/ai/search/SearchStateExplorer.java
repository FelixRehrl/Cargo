package it.uniroma1.di.tmancini.teaching.ai.search;

import java.util.*;
import java.io.*;

public abstract class SearchStateExplorer {

	private Problem problem;
	private Integer maxDepth;

	private long startTime = -1;
	private long durationMsec = -1;
	private PrintWriter out = new PrintWriter(System.out);
	private boolean DEBUG = false;

	private Set<State> explored;
	protected Frontier frontier;

	protected int nbIter;
	protected int maxFrontierSize;

	public static enum VERBOSITY {
		none, statsonly, low, high
	};

	protected VERBOSITY verbosity = VERBOSITY.none;

	private boolean isRunning = false;

	protected void setFrontier(Frontier f) {
		this.frontier = f;
	}

	public Integer getMaxDepth() {
		return maxDepth;
	}

	public SearchStateExplorer(Problem p, Integer maxDepth, String outFileName) {
		try {
			this.problem = p;
			if (outFileName != null) {
				this.out = new PrintWriter(new File(outFileName));
			}
			this.startTime = (new java.util.Date()).getTime();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.explored = new HashSet<State>();
		this.frontier = null; // Set by subclasses
		this.maxDepth = maxDepth;
		if (maxDepth != null && maxDepth <= 0) {
			throw new RuntimeException("maxDepth must be null or > 0");
		}
		isRunning = false;
	}

	public SearchStateExplorer(Problem p, Integer maxDepth) {
		this(p, maxDepth, null);
	}

	public SearchStateExplorer(Problem p) {
		this(p, null, null);
	}

	private long getDurationMsec() {
		if (isRunning || durationMsec < 0) {
			durationMsec = (new java.util.Date()).getTime() - startTime;
		}
		return durationMsec;
	}

	public void setVerbosity(VERBOSITY v) {
		verbosity = v;
	}

	public List<Action> run(State initialState) {
		return run(initialState, false);
	}

	public List<Action> run(State initialState, boolean findBestSolution) {
		isRunning = true;
		frontier.clear();
		explored.clear();
		frontier.enqueue(new SearchNode(initialState));

		nbIter = 0;
		maxFrontierSize = 0;

		boolean done = false;
		SearchNode result = null;
		while (!done) {
			if (frontier.isEmpty()) {
				done = true;
				break;
			}

			nbIter++;
			if (maxFrontierSize < frontier.size()) {
				maxFrontierSize = frontier.size();
			}

			if (verbosity.ordinal() >= VERBOSITY.statsonly.ordinal() && nbIter % 100 == 0) {
				outputStats();
			}

			SearchNode currNode = frontier.dequeue();
			State currState = currNode.getState();
			int currDepth = currNode.getDepth();

			outputString(VERBOSITY.low, currDepth, " X");
			outputString(VERBOSITY.high, currDepth, "Current node:");
			outputNode(VERBOSITY.high, currDepth, currNode);

			if (currState.isGoal()) {
				if (result == null || result.gValue() > currNode.gValue()) {
					result = currNode;
					if (!findBestSolution)
						done = true;
				}
			} else {
				explored.add(currState);
				if (maxDepth == null || currDepth < maxDepth) {
					for (Action a : currState.executableActions()) {
						SearchNode childNode = new SearchNode(currNode, a);
						outputString(VERBOSITY.high, currDepth, "Enqueueing child node:");
						outputNode(VERBOSITY.high, currDepth, childNode);
						if (explored.contains(childNode.getState())) {
							outputString(VERBOSITY.high, currDepth, "--> already explored --> no action");
						} else {
							boolean enqueued = frontier.enqueue(childNode);
							if (enqueued) {
								outputString(VERBOSITY.high, currDepth, "--> enqueued");
							} else {
								outputString(VERBOSITY.high, currDepth, "--> already in frontier with lower cost --> no action");
							}
						}
					}
				} else {
					outputString(VERBOSITY.low, currDepth, "max depth reached, cutting tree");
				}
			}
		}

		isRunning = false;
		if (result == null)
			return null;

		LinkedList<Action> actions = new LinkedList<Action>();
		while (result != null) {
			Action a = result.getAction();
			if (a != null) {
				actions.addFirst(result.getAction());
			}
			result = result.getParent();
		}

		return actions;
	}

	public int getNbIterations() {
		return nbIter;
	}

	public int maxFrontierSize() {
		return maxFrontierSize;
	}

	public void outputStats() {
		System.out.println("Statistics:\n - nbIterations = " + nbIter);
		System.out.println(" - maxFrontierSize = " + maxFrontierSize);
		System.out.println(" - duration (sec) = " + getDurationMsec() / 1000.0);
	}

	private static Map<Integer, String> prefixes = new HashMap<Integer, String>();

	private static String outputPrefix(int depth) {
		String s = prefixes.get(depth);
		if (s != null)
			return s;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			sb.append("      ");
		}
		s = sb.toString();
		prefixes.put(depth, s);
		return s;
	}

	private void outputNode(VERBOSITY minVerbosity, int depth, SearchNode o) {
		if (verbosity.ordinal() < minVerbosity.ordinal())
			return;
		System.out.println(o.toStringWithPrefix(outputPrefix(depth)));
	}

	private void outputString(VERBOSITY minVerbosity, int depth, String msg) {
		if (verbosity.ordinal() < minVerbosity.ordinal())
			return;
		System.out.print(outputPrefix(depth));
		System.out.println(msg);
	}

} // :~
