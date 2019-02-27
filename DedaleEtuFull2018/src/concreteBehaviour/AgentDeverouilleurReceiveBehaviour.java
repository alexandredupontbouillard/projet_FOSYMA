package concreteBehaviour;

import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiReceiveBehaviour;

public class AgentDeverouilleurReceiveBehaviour extends ExploMultiReceiveBehaviour{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6659644242869592196L;

	public AgentDeverouilleurReceiveBehaviour(final ExploreMultiAgent myagent) {
		super(myagent);
		this.myagent=myagent;
	}
	public void action() {
		if(! this.myMap.is_complete()) {
			super.action();
	
		}
	}
}
