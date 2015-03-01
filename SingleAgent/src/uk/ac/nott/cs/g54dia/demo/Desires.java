package uk.ac.nott.cs.g54dia.demo;

import java.util.Stack;

public class Desires {
	
	private Stack<Goal> desires;
	
	public Desires() {
		this.desires = new Stack<Goal>();
	}
	
	/**
     * Get Current Goal
     * @return Current Goal.
     */
	public Goal getCurrentDesire() {
		if (this.desires.empty()) {
			return Goal.NONE;
		}
		else {
			return this.desires.peek();
		}
	}
	
	/**
     * Add Desires
     * @return Add a goal to the stack.
     */
	public void addDesires(Goal goal) {
		this.desires.push(goal);
	}
	
	/**
     * Update Desires
     * @return Remove a goal from the stack.
     */
	public Goal popDesires() {
		return this.desires.pop();
	}
}
