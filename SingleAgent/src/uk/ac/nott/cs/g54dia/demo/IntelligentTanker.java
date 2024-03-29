package uk.ac.nott.cs.g54dia.demo;
import uk.ac.nott.cs.g54dia.library.*;

public class IntelligentTanker extends Tanker {
	
	private Beliefs beliefs;
	private Desires desires;
	private Plans plans;
	
	public IntelligentTanker(int environmentSize) {
		this.beliefs = new Beliefs(environmentSize);
		this.desires = new Desires();
		this.desires.addDesires(Goal.NONE);
		this.plans = new Plans();
	}

	@Override
	public Action senseAndAct(Cell[][] view, long timestep) {
		
		// update beliefs
		this.beliefs.updateBeliefs(view,this.getFuelLevel(),this.getWaterLevel(),this.getCurrentCell(view));
		
		// update desires
		if (this.desires.getCurrentDesire() == Goal.NONE) {
			this.desires.addDesires(Goal.TRAVERSE_MAP);
		}
		else if (this.desires.getCurrentDesire() == Goal.FINSH_TASK) {
			if (this.beliefs.getTasks().size() == 0) {
				this.desires.popDesires();
			}
		}
		else {
			if (this.beliefs.getTasks().size() != 0) {
				if (this.beliefs.isExplorationFinished()) {
					this.desires.addDesires(Goal.FINSH_TASK);
				}
			}
		}
		
		// select plan
		return this.plans.execute(this.desires, this.beliefs);
	}

}
