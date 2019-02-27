package concreteAgent;

import java.util.ArrayList;
import java.util.List;

import concreteBehaviour.AgentRecolteurBehaviour;
import concreteBehaviour.AgentRecolteurReceiveBehaviour;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiReceiveBehaviour;
import jade.core.behaviours.Behaviour;

public class AgentRecolteur extends ExploreMultiAgent {
	
	
	protected void setup(){

		super.setup();
		
		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		final Object[] args = getArguments();
		
		if(args.length!=0) {
			this.agentNames = (ArrayList<String>) args[2];
			
			x= new AgentRecolteurBehaviour(this,this.myMap,agentNames);
			
			y=new AgentRecolteurReceiveBehaviour(this);
			lb.add(y);
			lb.add(x);
		}
		
	
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}

}
