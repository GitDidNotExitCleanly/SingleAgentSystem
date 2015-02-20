package uk.ac.nott.cs.g54dia.library;

import java.util.Stack;

public class Desires {
	
	/*
	 * Mission Code
	 * 
	 * 	FinshTask = 0
	 * 	TraverseMap = 1
	 * 
	 */
	
	private Stack<Integer> desires;
	
	public Desires() {
		this.desires = new Stack<Integer>();
	}
	
	/**
     * Get Current Goal
     * @return Current Goal, 0 for FinshTask, 1 for TraverseMap , -1 for no goal.
     */
	public int getCurrentDesire() {
		if (this.desires.empty()) {
			return -1;
		}
		else {
			return this.desires.peek();
		}
	}
	
	/**
     * Update Desires
     * @return Add/Delete a goal to/from the stack, 0 for FinshTask, 1 for TraverseMap, -1 for pop a goal.
     */
	public void updateDesires(int desireCode) {
		this.desires.push(desireCode);
	} 
}
