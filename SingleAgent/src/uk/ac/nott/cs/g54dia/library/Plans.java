package uk.ac.nott.cs.g54dia.library;

import java.util.ArrayList;

public class Plans {
	
	public Plans() {}
	
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

				// Finish the task by order
				Task task = beliefs.getTasks().get(0);
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
							Point destination = this.makeGoodUseOfFuel(beliefs.getExploringPoints(), currentFuel, currentPosition);
							result = new MoveTowardsAction(destination);
						}

					}
					else {
						// go to station
						if (this.canTankerGoThere(currentFuel, currentPosition, target)) {
							result = new MoveTowardsAction(target);
						}
						else {
							Point destination = this.makeGoodUseOfFuel(beliefs.getExploringPoints(), currentFuel, currentPosition);
							result = new MoveTowardsAction(destination);
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