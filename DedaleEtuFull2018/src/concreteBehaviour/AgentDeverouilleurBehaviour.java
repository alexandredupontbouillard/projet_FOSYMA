package concreteBehaviour;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

public class AgentDeverouilleurBehaviour extends ExploMultiBehaviour {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6452473587435877360L;
	public AgentDeverouilleurBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap,List<String> agentNames) {
		super(myagent, myMap, agentNames);
		this.myMap=myMap;
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		this.agentNames=agentNames;
		
	}
	@Override
	public synchronized void action() {
		super.action();
	}
	

}
