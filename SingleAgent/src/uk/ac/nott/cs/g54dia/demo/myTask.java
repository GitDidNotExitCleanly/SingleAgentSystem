package uk.ac.nott.cs.g54dia.demo;

import uk.ac.nott.cs.g54dia.library.Task;

public class myTask {
	
	private Task task;
	private myPoint point;
	
	public myTask(myPoint point, Task task) {
		this.task = task;
		this.point = point;
	}
	
	public myPoint getStationPosition() {
		return this.point;
	}
	
	public Task getTask() {
		return this.task;
	}
	
	@Override
	public boolean equals(Object o) {
        myTask t = (myTask)o;
        if (t==null) return false;
        return (t.getStationPosition().getX() == point.getX()) && (t.getStationPosition().getY() == point.getY());
    }
}
