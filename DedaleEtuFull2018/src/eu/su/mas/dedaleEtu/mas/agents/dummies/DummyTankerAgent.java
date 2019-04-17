package eu.su.mas.dedaleEtu.mas.agents.dummies;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
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



	
	
	protected List<String> agentNames;
	protected TankerBehaviour x;
	protected TankerMultiReceiveBehaviour y;


	protected MapRepresentation myMap;
	protected void setup(){
		super.setup();
		Object[] args = getArguments();
		List<Behaviour> lb=new ArrayList<Behaviour>();
		if(args.length!=0) {
			agentNames = (ArrayList<String>) args[2];
			x = new TankerBehaviour(this,this.myMap,agentNames);
			//y=new ExploMultiReceiveBehaviour(this);
			//lb.add(y);
			lb.add(x);
		}
		addBehaviour(new startMyBehaviours(this,lb));
		
		System.out.println("the  agent "+this.getLocalName()+ " is started");


	}

	/**
	 * This method is automatically called after doDelete()
	 */

	public void maj(List<Case> open, List<Case> closed) {
		x.maj(open, closed);
	}
	
	public void setMap(MapRepresentation map) {
		myMap = map;
		//y.setMap(myMap);
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


class TankerBehaviour extends ExploMultiBehaviour{
	/**
	 * When an agent choose to migrate all its components should be serializable
	 *  
	 */
	private boolean finished = false;
	protected ArrayList<String> openNodes;
	/**
	 * Visited nodes
	 */
	protected MapRepresentation myMap;

	private Set<String> closedNodes;
	private static final long serialVersionUID = 9088209402507795289L;
	protected List<String> agentNames;
	private String siloPos="nopos";
	public TankerBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap, List<String> agentNames) {
		super(myagent,myMap,agentNames);
		this.openNodes = new ArrayList<String>();
		this.closedNodes = new HashSet<String>();
		this.agentNames = agentNames;
		

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
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (this.myMap == null) {
			this.myMap = new MapRepresentation();
		}

		setmap();
		List<Couple<String, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent) this.myAgent).observe();
		
		
		
		if (!myMap.is_complete()) {
			// 0) Retrieve the current position
			String myPosition = ((AbstractDedaleAgent) this.myAgent).getCurrentPosition();
			if (myPosition != null) {

				// myPosition

				// 1) remove the current node from openlist and add it to closedNodes.
				this.closedNodes.add(myPosition);
				this.openNodes.remove(myPosition);

				addNodeMypos(lobs);

				if (lobs != null)
					deplacement_explo(lobs, myPosition);
			}

		}
		else {
			System.out.println("lalalala");
			if(siloPos.equals("nopos")) {
				List<String> tran = myMap.syloPose();
				System.out.println(tran + myAgent.getName());
				siloPos = tran.get(tran.size());
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
	@Override
	public void setmap() {
		((DummyTankerAgent) this.myAgent).setMap(myMap);
	}

}
class TankerMultiReceiveBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 9088209402507795289L;

	private boolean finished=false;
	protected ExploAgent myagent;
	/**
	 * 
	 * This behaviour is a one Shot.
	 * It receives a message tagged with an inform performative, print the content in the console and destroy itlself
	 * @param myagent
	 */
	protected MapRepresentation myMap;
	
	public TankerMultiReceiveBehaviour(final DummyTankerAgent myagent) {
		super(myagent);
		this.myagent=myagent;
	}
	public void setMap(MapRepresentation map) {
		myMap = map;
	}
	public void messageClassique(Couple<List<Case>,List<Couple<String,String>>> content) {
		List<Case> nodes = content.getLeft();
		
		List<Couple<String,String>> edges = content.getRight();
		List<Case> open = new ArrayList();
		List<Case> closed = new ArrayList();
		for(int i =0; i<nodes.size();i++) {
			
			myMap.addNode(nodes.get(i));
			
			if(!nodes.get(i).is_open()){
				closed.add(nodes.get(i));
			}
			else {
				
				open.add(nodes.get(i));

			}
		}
		for(int i =0; i<edges.size();i++) {
			myMap.addEdge(edges.get(i).getLeft(), edges.get(i).getRight());
		}
		
		myagent.maj(open, closed);

			
	}
	

	public synchronized void action() {
		if(myMap!=null) {
			if(!myMap.is_complete()) {
				final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			
				final ACLMessage msg = this.myAgent.receive(msgTemplate);
				if (msg != null) {
					if(msg.getProtocol().equals("CLASSIQUE")) {
						try {
							Couple<List<Case>,List<Couple<String,String>>> c = (Couple<List<Case>,List<Couple<String,String>>>) msg.getContentObject();
							if(((DummyTankerAgent) myAgent).explore()) {
								messageClassique(c);
							}
							
	
						} catch (UnreadableException e) {
							
							e.printStackTrace();
						}
					}
					
				}
				else{
						block();
				}
			}
		}
	}

	public boolean done() {
		return finished;
	}

}


