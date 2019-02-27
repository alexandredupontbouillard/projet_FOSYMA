package concreteAgent;

import java.util.ArrayList;
import java.util.List;

import concreteBehaviour.AgentDeverouilleurBehaviour;
import concreteBehaviour.AgentDeverouilleurReceiveBehaviour;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiReceiveBehaviour;
import jade.core.behaviours.Behaviour;

public class AgentDeverouilleur extends ExploreMultiAgent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6604579891367687088L;

	protected void setup(){

		super.setup();
		
		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		final Object[] args = getArguments();
		
		if(args.length!=0) {
			agentNames = (ArrayList<String>) args[2];
			
			x= new AgentDeverouilleurBehaviour(this,this.myMap,agentNames);
			
			y=new AgentDeverouilleurReceiveBehaviour(this);
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
