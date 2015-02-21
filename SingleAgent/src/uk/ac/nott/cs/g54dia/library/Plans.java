package uk.ac.nott.cs.g54dia.library;

import java.util.ArrayList;

public class Plans {
	
	private Task lastTask;
	
	public Plans() {
		this.lastTask = null;
	}
	
	// functions
	public Action execute(Desires desires,Beliefs beliefs) {
		
		Action result = null;
		
		// if current cell is a fuel pump, refuel
		if (beliefs.getCurrentCell() instanceof FuelPump && beliefs.getFuel() < Tanker.MAX_FUEL) {
			result = new RefuelAction();
		}
		// if current cell is a well, load water
		else if (beliefs.getCurrentCell() instanceof Well && beliefs.getWater() < Tanker.MAX_WATER) {
			result = new LoadWaterAction();
		}
		// if current cell is just a normal cell or station, base on desires
		else {
			int currentFuel;
			int currentWater;
			Point currentPosition;
			Point target;
			Point well;
			
			switch (desires.getCurrentDesire()) {
			case FINSH_TASK:

				currentFuel = beliefs.getFuel();
				currentWater = beliefs.getWater();
				currentPosition = beliefs.getCurrentPosition();

				Task task = null;
				if (beliefs.getCurrentCell() instanceof Station && ((Station)beliefs.getCurrentCell()).getTask() != null) {
					task = ((Station)beliefs.getCurrentCell()).getTask();
				}
				else {
					if (this.lastTask == null) {
						task = beliefs.getTasks().get(0);
					}
					else {
						if (this.lastTask.isComplete()) {
							// by default
							task = beliefs.getTasks().get(0);
							
							// neighborhood task of last task
							Point lastWell = beliefs.getStationWellPair().get(this.lastTask.getStationPosition());
							for (int i=0;i<beliefs.getTasks().size();i++) {
								Task current = beliefs.getTasks().get(i);
								if (beliefs.getStationWellPair().get(current.getStationPosition()).equals(lastWell)) {
									task = current;
									break;
								}
							}
						}
						else {
							task = this.lastTask;
						}
					}
				}
				this.lastTask = task;
				
				target = task.getStationPosition();
				well = beliefs.getStationWellPair().get(target);
				
				if (beliefs.getCurrentPosition().equals(target) && currentWater >= task.getRequired()) {
					
					result = new DeliverWaterAction(task);	
					
				}		
				else {
					
					if (currentWater < task.getRequired()) {
						// go to well
						if (this.canTankerGoThere(currentFuel, currentPosition, well)) {
							result = new MoveTowardsAction(well);
						}
						else {
							Point destination_well = this.makeGoodUseOfFuel(beliefs.getWells(), currentFuel, currentPosition);
							if (destination_well.equals(Tanker.FUEL_PUMP_LOCATION)) {
								
								Point destination_explore = this.makeGoodUseOfFuel(beliefs.getExploringPoints(), currentFuel, currentPosition);
								result = new MoveTowardsAction(destination_explore);
							}
							else {
								result = new MoveTowardsAction(destination_well);
							}
						}
					}
					else {
						// go to station
						if (this.canTankerGoThere(currentFuel, currentPosition, target)) {
							result = new MoveTowardsAction(target);
						}
						else {
							ArrayList<Point> alternatives = new ArrayList<Point>();
							for (int i=0;i<beliefs.getTasks().size();i++) {
								Task t = beliefs.getTasks().get(i);
								if (currentWater >= t.getRequired()) {
									alternatives.add(t.getStationPosition());
								}
							}
							Point destination_station = this.makeGoodUseOfFuel(alternatives, currentFuel, currentPosition);
							if (destination_station.equals(Tanker.FUEL_PUMP_LOCATION)) {
								Point destination_explore = this.makeGoodUseOfFuel(beliefs.getExploringPoints(), currentFuel, currentPosition);
								result = new MoveTowardsAction(destination_explore);			
							}
							else {
								result = new MoveTowardsAction(destination_station);
							}
						}
					}
					
				}
				
				break;
			case TRAVERSE_MAP:

				currentFuel = beliefs.getFuel();
				currentPosition = beliefs.getCurrentPosition();

				Point destination = this.makeGoodUseOfFuel(beliefs.getExploringPoints(), currentFuel, currentPosition);
				result = new MoveTowardsAction(destination);

				break;		
			default:
				break;
			}
		}
		
		return result;
	}
	
	/*
	 * Whether tanker can go there
	 * @return true if it is safe to go to the position
	 * */
	private boolean canTankerGoThere(int fuel,Point currentPosition,Point target) {
		
		// fuel used by go to the target
		int dx1 = currentPosition.x - target.x;
		int dy1 = currentPosition.y - target.y;
		int distance1 = Math.max(Math.abs(dx1), Math.abs(dy1));
		
		// fuel used by return from target
		int dx2 = target.x - Tanker.FUEL_PUMP_LOCATION.x;
		int dy2 = target.y - Tanker.FUEL_PUMP_LOCATION.y;
		int distance2 = Math.max(Math.abs(dx2), Math.abs(dy2));
		
		if (fuel - distance1 - distance2 >= 0) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	private Point makeGoodUseOfFuel(ArrayList<Point> alternatives,int currentFuel,Point currentPosition) {
		
		Point destination = null;
		
		for (int i=0;i<alternatives.size();i++) {
			Point target = alternatives.get(i);
			if (this.canTankerGoThere(currentFuel,currentPosition, target)) {
				destination = alternatives.get(i);
				break;
			}
		}
					
		if (destination == null) {
			destination = Tanker.FUEL_PUMP_LOCATION;
		}
		
		return destination;
	}
}