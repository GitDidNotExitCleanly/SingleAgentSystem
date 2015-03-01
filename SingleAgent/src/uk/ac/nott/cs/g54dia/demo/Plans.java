package uk.ac.nott.cs.g54dia.demo;

import java.util.ArrayList;

import uk.ac.nott.cs.g54dia.library.Action;
import uk.ac.nott.cs.g54dia.library.DeliverWaterAction;
import uk.ac.nott.cs.g54dia.library.FuelPump;
import uk.ac.nott.cs.g54dia.library.LoadWaterAction;
import uk.ac.nott.cs.g54dia.library.MoveAction;
import uk.ac.nott.cs.g54dia.library.RefuelAction;
import uk.ac.nott.cs.g54dia.library.Station;
import uk.ac.nott.cs.g54dia.library.Tanker;
import uk.ac.nott.cs.g54dia.library.Well;

public class Plans {
	
	private myTask lastTask;
	
	public Plans() {
		this.lastTask = null;
	}
	
	// main function
	public Action execute(Desires desires,Beliefs beliefs) {
		
		// if current cell is a fuel pump, refuel
		if (beliefs.getCurrentCell() instanceof FuelPump && beliefs.getFuel() < Tanker.MAX_FUEL) {
			return new RefuelAction();
		}
		// if current cell is a well, load water
		else if (beliefs.getCurrentCell() instanceof Well && beliefs.getWater() < Tanker.MAX_WATER) {
			return  new LoadWaterAction();
		}
		else {
			
			int currentFuel;
			myPoint currentPosition;
			
			switch (desires.getCurrentDesire()) {
			
			
			// Desire Selection
			case FINSH_TASK:

				currentFuel = beliefs.getFuel();
				int currentWater = beliefs.getWater();
				currentPosition = beliefs.getCurrentPosition();

				// Task Selection
				myTask task;
				if (beliefs.getCurrentCell() instanceof Station && ((Station)beliefs.getCurrentCell()).getTask() != null) {
					task = beliefs.getTasks().get(beliefs.getTasks().indexOf(new myTask(currentPosition,((Station)beliefs.getCurrentCell()).getTask())));
				}
				else {
					if (this.lastTask == null) {
						task = beliefs.getTasks().get(0);
					}
					else {
						if (this.lastTask.getTask().isComplete()) {
							// by default
							task = beliefs.getTasks().get(0);
						}
						else {
							task = this.lastTask;
						}
					}
				}
				this.lastTask = task;

				myPoint target = task.getStationPosition();
				myPoint well = beliefs.getStationWellPair().get(target);
				
				
				// Task Execution
				if (currentPosition.equals(target)) {
					if (currentWater >= task.getTask().getRequired()) {
						
						// deliver water
						return new DeliverWaterAction(task.getTask());
					}
					else {
						if (this.canTankerGoThere(currentFuel, currentPosition, well)) {
							
							// go to a well
							return this.nextMovement(currentPosition, well);
						}
						else {
							
							// try options
							myPoint destination_well = this.makeGoodUseOfFuel(beliefs.getWells(), currentFuel, currentPosition);
							if (destination_well.equals(new myPoint(0,0))) {
								
								ArrayList<myPoint> alternatives = new ArrayList<myPoint>();
								for (int i=0;i<beliefs.getTasks().size();i++) {
									myTask t = beliefs.getTasks().get(i);
									if (currentWater >= t.getTask().getRequired()) {
										alternatives.add(t.getStationPosition());
									}
								}
								myPoint destination_station = this.makeGoodUseOfFuel(alternatives, currentFuel, currentPosition);
								
								if (destination_station.equals(new myPoint(0,0))) {
									myPoint destination_explore = this.makeGoodUseOfFuel(beliefs.getExplorationPoints(), currentFuel, currentPosition);
									
									// go to an exploration point
									return this.nextMovement(currentPosition, destination_explore);		
								}
								else {
									
									// go to a station
									return this.nextMovement(currentPosition, destination_station);
								}
							}
							else {
								
								// go to a well
								return this.nextMovement(currentPosition, destination_well);
							}
						}
					}
				}
				else {
					if (currentWater >= task.getTask().getRequired()) {
						if (this.canTankerGoThere(currentFuel, currentPosition, target)) {
							
							// go to a station
							return this.nextMovement(currentPosition, target);
						}
						else {
							
							// try options
							ArrayList<myPoint> alternatives = new ArrayList<myPoint>();
							for (int i=0;i<beliefs.getTasks().size();i++) {
								myTask t = beliefs.getTasks().get(i);
								if (currentWater >= t.getTask().getRequired()) {
									alternatives.add(t.getStationPosition());
								}
							}
							myPoint destination_station = this.makeGoodUseOfFuel(alternatives, currentFuel, currentPosition);
							
							if (destination_station.equals(new myPoint(0,0))) {
								myPoint destination_explore = this.makeGoodUseOfFuel(beliefs.getExplorationPoints(), currentFuel, currentPosition);
								
								// go to an exploration point
								return this.nextMovement(currentPosition, destination_explore);		
							}
							else {
								
								// go to a station
								return this.nextMovement(currentPosition, destination_station);
							}
						}
					}
					else {
						if (this.canTankerGoThere(currentFuel, currentPosition, well)) {
							
							// go to a well
							return this.nextMovement(currentPosition, well);
						}
						else {
							
							// try options
							myPoint destination_well = this.makeGoodUseOfFuel(beliefs.getWells(), currentFuel, currentPosition);
							
							if (destination_well.equals(new myPoint(0,0))) {
								
								ArrayList<myPoint> alternatives = new ArrayList<myPoint>();
								for (int i=0;i<beliefs.getTasks().size();i++) {
									myTask t = beliefs.getTasks().get(i);
									if (currentWater >= t.getTask().getRequired()) {
										alternatives.add(t.getStationPosition());
									}
								}
								myPoint destination_station = this.makeGoodUseOfFuel(alternatives, currentFuel, currentPosition);
								
								if (destination_station.equals(new myPoint(0,0))) {
									myPoint destination_explore = this.makeGoodUseOfFuel(beliefs.getExplorationPoints(), currentFuel, currentPosition);
									
									// go to an exploration point
									return this.nextMovement(currentPosition, destination_explore);		
								}
								else {
									
									// go to a station
									return this.nextMovement(currentPosition, destination_station);
								}
							}
							else {
								
								// go to a well
								return this.nextMovement(currentPosition, destination_well);
							}
						}
					}
				}

			case TRAVERSE_MAP:

				currentFuel = beliefs.getFuel();
				currentPosition = beliefs.getCurrentPosition();

				myPoint destination = this.makeGoodUseOfFuel(beliefs.getExplorationPoints(), currentFuel, currentPosition);
				return this.nextMovement(currentPosition, destination);

			default:
				
				return null;
				
			}
		}
		
	}
	
	/*
	 * Whether tanker can go there
	 * @return true if it is safe to go to the position
	 * */
	private boolean canTankerGoThere(int fuel,myPoint currentPosition,myPoint target) {
		
		// fuel used by go to the target
		int dx1 = currentPosition.getX() - target.getX();
		int dy1 = currentPosition.getY() - target.getY();
		int distance1 = Math.max(Math.abs(dx1), Math.abs(dy1));
		
		// fuel used by return from target
		int dx2 = target.getX();
		int dy2 = target.getY();
		int distance2 = Math.max(Math.abs(dx2), Math.abs(dy2));
		
		if (fuel - distance1 - distance2 >= 0) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	/*
	 * Try options in order to better save fuel
	 * @return a destination point
	 * */
	private myPoint makeGoodUseOfFuel(ArrayList<myPoint> alternatives,int currentFuel,myPoint currentPosition) {
		
		myPoint destination = null;
		
		for (int i=0;i<alternatives.size();i++) {
			myPoint target = alternatives.get(i);
			if (this.canTankerGoThere(currentFuel,currentPosition, target) && !currentPosition.equals(target)) {
				destination = alternatives.get(i);
				break;
			}
		}
					
		if (destination == null) {
			destination = new myPoint(0,0);
		}
		
		return destination;
	}
	
	/*
	 * The next action towards the target
	 * @return an action
	 * */
	private Action nextMovement(myPoint currentPosition, myPoint targetPosition) {
		
		int dx = targetPosition.getX() - currentPosition.getX();
		int dy = targetPosition.getY() - currentPosition.getY();
		
		if (dx > 0 && dy > 0) {
			currentPosition.increaseXbyOne();
			currentPosition.increaseYbyOne();
			return new MoveAction(MoveAction.NORTHEAST);
		}
		else if (dx > 0 && dy == 0) {
			currentPosition.increaseXbyOne();
			return new MoveAction(MoveAction.EAST);
		}
		else if (dx > 0 && dy < 0) {
			currentPosition.increaseXbyOne();
			currentPosition.decreaseYbyOne();
			return new MoveAction(MoveAction.SOUTHEAST);
		}
		else if (dx == 0 && dy > 0) {
			currentPosition.increaseYbyOne();
			return new MoveAction(MoveAction.NORTH);
		}
		else if (dx == 0 && dy < 0) {
			currentPosition.decreaseYbyOne();
			return new MoveAction(MoveAction.SOUTH);
		}
		else if (dx < 0 && dy > 0) {
			currentPosition.decreaseXbyOne();
			currentPosition.increaseYbyOne();
			return new MoveAction(MoveAction.NORTHWEST);
		}
		else if (dx < 0 && dy == 0) {
			currentPosition.decreaseXbyOne();
			return new MoveAction(MoveAction.WEST);
		}
		else if (dx < 0 && dy < 0) {
			currentPosition.decreaseXbyOne();
			currentPosition.decreaseYbyOne();
			return new MoveAction(MoveAction.SOUTHWEST);
		}
		
		return null;
	}
}