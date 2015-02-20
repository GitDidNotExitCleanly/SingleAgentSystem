package uk.ac.nott.cs.g54dia.library;

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
			switch (desires.getCurrentDesire()) {
				case FINSH_TASK:
					
					
					
					
					
					
					
					
							
					break;
				case TRAVERSE_MAP:
					
					int currentFuel = beliefs.getFuel();
					Point currentPosition = beliefs.getCurrentPosition();
					
					Point target = beliefs.getExploringPoints().get(0);
					int distance = Math.max(Math.abs(target.x-currentPosition.x), Math.abs(target.y-currentPosition.y));
					if (this.isFuelEnoughForReturn(currentFuel-distance, beliefs.getExploringPoints().get(0)) >= 0) {	
						result = new MoveTowardsAction(beliefs.getExploringPoints().get(0));
					}
					else {
						for (int i=1;i<beliefs.getExploringPoints().size();i++) {
							target = beliefs.getExploringPoints().get(i);
							distance = Math.max(Math.abs(target.x-currentPosition.x), Math.abs(target.y-currentPosition.y));
							if (this.isFuelEnoughForReturn(currentFuel-distance, beliefs.getExploringPoints().get(i)) >= 0) {
								result = new MoveTowardsAction(beliefs.getExploringPoints().get(i));
								break;
							}
						}
						if (result == null) {
							result = new MoveTowardsAction(Tanker.FUEL_PUMP_LOCATION);
						}
					}
					
					break;		
				default:
					break;
			}
			
		}
		
		return result;
	}
	
	/*
	 * Get remaining fuel
	 * @return positive is there is fuel remaining, -1 otherwise
	 * */
	private int isFuelEnoughForReturn(int fuel,Point position) {
		
		int dx = position.x - Tanker.FUEL_PUMP_LOCATION.x;
		int dy = position.y - Tanker.FUEL_PUMP_LOCATION.y;
			
		if (fuel - Math.max(Math.abs(dx), Math.abs(dy)) >= 0) {
			return fuel - Math.max(Math.abs(dx), Math.abs(dy));
		}
		else {
			return -1;
		}
		
	}
}