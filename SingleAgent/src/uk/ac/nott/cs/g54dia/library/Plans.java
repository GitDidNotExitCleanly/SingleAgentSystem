package uk.ac.nott.cs.g54dia.library;

public class Plans {
	
	public Plans() {}
	
	// functions
	public Action execute(Desires desires,Beliefs beliefs) {
		
		Action result = null;
		switch (desires.getCurrentDesire()) {
		case 0:
			
			
			
			
			break;
		case 1:
			if (beliefs.getCurrentCell() instanceof FuelPump && beliefs.getFuel() < Tanker.MAX_FUEL) {
				result = new RefuelAction();
			}
			else {
				if (!beliefs.isExplorationFinished()) {
					if (beliefs.isFuelEnoughForReturn()) {
						result = new MoveTowardsAction(beliefs.getExploringPoints().get(0));
					}
					else {
						result = new MoveTowardsAction(Tanker.FUEL_PUMP_LOCATION);
					}
				}
				else {
					result = new MoveTowardsAction(Tanker.FUEL_PUMP_LOCATION);
				}
			}
			
			
			
			
			break;
		}
		
		return result;
	}
}