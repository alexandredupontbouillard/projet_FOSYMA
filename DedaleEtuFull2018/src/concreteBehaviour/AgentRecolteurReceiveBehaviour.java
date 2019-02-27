package concreteBehaviour;

import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiReceiveBehaviour;

public class AgentRecolteurReceiveBehaviour extends ExploMultiReceiveBehaviour{
	public AgentRecolteurReceiveBehaviour(final ExploreMultiAgent myagent) {
		super(myagent);
		this.myagent=myagent;
	}
	public void action() {
		if(this.myMap!=null) {
			if(! this.myMap.is_complete()) {
				super.action();
			}
		}
	}
}
