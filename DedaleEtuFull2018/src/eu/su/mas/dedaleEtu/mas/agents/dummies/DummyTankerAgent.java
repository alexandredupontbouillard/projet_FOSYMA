package eu.su.mas.dedaleEtu.mas.agents.dummies;


import java.util.ArrayList;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.ReceiveTreasureTankerBehaviour;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploMultiReceiveBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import message.Case;


/**
 * Dummy Tanker agent. It does nothing more than printing what it observes every 10s and receiving the treasures from other agents. 
 * <br/>
 * Note that this last behaviour is hidden, every tanker agent automatically possess it.
 * 
 * @author hc
 *
 */
public class DummyTankerAgent extends AbstractDedaleAgent implements ExploAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1784844593772918359L;



	
	final Object[] args = getArguments();
	protected List<String> agentNames;
	protected RandomTankerBehaviour x;
	protected ExploMultiReceiveBehaviour y;


	private MapRepresentation myMap;
	protected void setup(){

		super.setup();

		List<Behaviour> lb=new ArrayList<Behaviour>();
		if(args.length!=0) {
			agentNames = (ArrayList<String>) args[2];
		x = new RandomTankerBehaviour(this,this.myMap,agentNames);
		y=new ExploMultiReceiveBehaviour(this);
		lb.add(y);
		lb.add(x);
		}
		addBehaviour(new startMyBehaviours(this,lb));
		
		

	}

	/**
	 * This method is automatically called after doDelete()
	 */
	protected void takeDown(){

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


/**************************************
 * 
 * 
 * 				BEHAVIOUR
 * 
 * 
 **************************************/


class RandomTankerBehaviour extends ExploMultiBehaviour{
	/**
	 * When an agent choose to migrate all its components should be serializable
	 *  
	 */
	private static final long serialVersionUID = 9088209402507795289L;
	protected List<String> agentNames;
	private String siloPos;
	public RandomTankerBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap, List<String> agentNames) {
		super(myagent,myMap,agentNames);
		

	}
	public void maj(List<Case> open, List<Case> closed) {
		super.maj(open,closed);
	}

	public void sendClassicMessage() {
		super.sendClassicMessage();

	}

	public boolean explore() {
		return !myMap.is_complete();
	}

	@Override
	public void action() {
		if (this.myMap == null) {
			this.myMap = new MapRepresentation();
		
		}
		
		super.action();
		if(myMap.is_complete()) {
			if(siloPos==null) {
				List<String> tran = myMap.syloPose();
				siloPos = tran.get(tran.size()-1);
			}
			if(! ((AbstractDedaleAgent)myAgent).getCurrentPosition().equals(siloPos)) {
				
			
				List<String> pl = myMap.getShortestPath(((AbstractDedaleAgent)myAgent).getCurrentPosition(), siloPos);
				if (pl.size() >0) {
					String nextNode = pl.get(0);
					((AbstractDedaleAgent)myAgent).moveTo(nextNode);
	
				} 
			}else {
				block();
			}

			
		}

	}
	@Override
	public boolean ramasser(List<Couple<String, List<Couple<Observation, Integer>>>> lobs) {
		return false;
	}

	public void deplacement_explo(List<Couple<String, List<Couple<Observation, Integer>>>> lobs, String myPosition) {
		super.deplacement_explo(lobs,myPosition);

	}
	protected void majMapComplete(List<Couple<String, List<Couple<Observation, Integer>>>> lobs) {
		super.majMapComplete(lobs);
	}

	protected void majMap(List<Couple<String, List<Couple<Observation, Integer>>>> lobs, String myPosition) {
		super.majMap(lobs, myPosition);
	}

	@Override
	public boolean done() {
		return finished;
	}

	protected void moveTo(String id) {
		super.moveTo(id);

	}

	protected void move_random() {
		super.move_random();
	}

}