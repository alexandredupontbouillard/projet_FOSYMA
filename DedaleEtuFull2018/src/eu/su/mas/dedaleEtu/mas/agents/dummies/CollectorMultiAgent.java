package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.CollectMultiBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiReceiveBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import message.Case;

public class CollectorMultiAgent extends AbstractDedaleAgent implements ExploAgent{
	private static final long serialVersionUID = -6431752665590433727L;
	protected MapRepresentation myMap;
	

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	protected CollectMultiBehaviour x;
	protected ExploMultiReceiveBehaviour y;
	
	protected List<String> agentNames;
	protected void setup(){

		super.setup();
		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		final Object[] args = getArguments();
		
		if(args.length!=0) {
			agentNames = (ArrayList<String>) args[2];
			
			x= new CollectMultiBehaviour(this,this.myMap,agentNames);
			
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
	
	public void maj(List<Case> open, List<Case> closed) {
		x.maj(open, closed);
	}
	
	public void setMap(MapRepresentation map) {
		myMap = map;
		y.setMap(myMap);
	}
	public boolean explore() {
		return x.explore();
	}
	
	
	


}
