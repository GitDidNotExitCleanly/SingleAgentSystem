package uk.ac.nott.cs.g54dia.demo;
import uk.ac.nott.cs.g54dia.library.*;

public class IntelligentTanker extends Tanker {
	
	private Beliefs beliefs;
	private Desires desires;
	private Plans plans;
	
	public IntelligentTanker(int environmentSize) {
		this.beliefs = new Beliefs(environmentSize);
		this.desires = new Desires();
		this.desires.updateDesires(1);
		this.plans = new Plans();
	}

	@Override
	public Action senseAndAct(Cell[][] view, long timestep) {
		
		// update beliefs
		this.beliefs.updateBeliefs(view,this.getFuelLevel(),this.getWaterLevel(),this.getPosition(),this.getCurrentCell(view));
		
		// update desires
		if (this.desires.getCurrentDesire() == -1) {
			this.desires.updateDesires(1);
		}
		/*
		else {
			if (this.desires.getCurrentDesire() != 0 && this.beliefs.getTask() != null) {
				this.desires.updateDesires(0);
			}
		}
		*/
		
		// select plan
		return this.plans.execute(this.desires, this.beliefs);
		
	}

}
