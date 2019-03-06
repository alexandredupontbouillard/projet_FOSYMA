package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiReceiveBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import message.Case;

/**
 * ExploreSolo agent. 
 * It explore the map using a DFS algorithm.
 * It stops when all nodes have been visited
 *  
 *  
 * @author hc
 *
 */

public class ExploreMultiAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -6431752665590433727L;
	protected MapRepresentation myMap;
	

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected ExploMultiBehaviour x;
	protected ExploMultiReceiveBehaviour y;
	
	protected List<String> agentNames;
	protected void setup(){

		super.setup();
		
		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		final Object[] args = getArguments();
		
		if(args.length!=0) {
			agentNames = (ArrayList<String>) args[2];
			
			x= new ExploMultiBehaviour(this,this.myMap,agentNames);
			
			y=new ExploMultiReceiveBehaviour(this);
			lb.add(y);
			lb.add(x);
		}
		
	
		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */
		
		
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	public void maj(List<Case> open, String closed) {
		x.maj(open, closed);
	}
	public void maj(List<String> open, List<String> closed) {
		x.maj(open, closed);
	}
	public void setMap(MapRepresentation map) {
		myMap = map;
		y.setMap(myMap);
	}
	
	
	
}
