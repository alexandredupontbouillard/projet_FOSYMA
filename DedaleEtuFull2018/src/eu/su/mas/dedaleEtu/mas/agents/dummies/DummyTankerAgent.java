package eu.su.mas.dedaleEtu.mas.agents.dummies;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
import jade.core.AID;
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
			y=new TankerMultiReceiveBehaviour(this);
			lb.add(y);
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

	@Override
	public void moveRandom() {
		x.move_random();
		
	}

	@Override
	public boolean isDroping() {
		return false;
	}

	@Override
	public void dropped() {
		// TODO Auto-generated method stub
		
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
		for (int i = 0; i < closed.size(); i++) {
			if (!closedNodes.contains(closed.get(i).getId())) {

				openNodes.remove(closed.get(i).getId());
				closedNodes.add(closed.get(i).getId());

			}
			myMap.addNodeF(closed.get(i));

		}
		for (int i = 0; i < open.size(); i++) {
			if (!openNodes.contains(open.get(i).getId())) {
				if (!closedNodes.contains(open.get(i).getId())) {
					openNodes.add(open.get(i).getId());
					open.get(i).set_ouvert(true);
					myMap.addNode(open.get(i));
				}
			}
		}
	}


	public void sendClassicMessage() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());

		try {
			List<Couple<String, String>> list_edges = myMap.getAllEdges();
			List<Case> list_Nodes = myMap.getAllNodes();
			Couple<List<Case>, List<Couple<String, String>>> c = new Couple<List<Case>, List<Couple<String, String>>>(
					list_Nodes, list_edges);

			msg.setProtocol("CLASSIQUE");
			msg.setContentObject((Serializable) c);
			for (int i = 0; i < agentNames.size(); i++) {
				if (!agentNames.get(i).equals(myAgent.getAID().getName())) {
					msg.addReceiver(new AID(agentNames.get(i), AID.ISLOCALNAME));
				}
			}
			((AbstractDedaleAgent) this.myAgent).sendMessage(msg);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public boolean explore() {
		return !myMap.is_complete();
	}

	public void addNodeMypos(List<Couple<String, List<Couple<Observation, Integer>>>> lobs) {
		int tresor = 0;
		int serrure = 0;
		int force = 0;
		boolean ouvert = false;
		Case c;
		if (lobs.get(0).getRight().size() > 0) {
			ArrayList<Integer> h = transfoLobs(lobs.get(0).getRight());
			tresor = h.get(0);
			serrure = h.get(2);
			force = h.get(3);
			ouvert = h.get(1) == 1;
			
		}
		
		c = new Case(lobs.get(0).getLeft(), tresor, serrure, force, false, ouvert);
		
		this.myMap.addNode(c);
		
		
	}
	public ArrayList<Integer> transfoLobs(List<Couple<Observation, Integer>> lobs){
		ArrayList<Integer> result = new ArrayList<Integer>();
		result.add(0);
		result.add(0);
		result.add(0);
		result.add(0);
		for(int i =0 ; i<lobs.size();i++) {
			if(lobs.get(i).getLeft() == Observation.GOLD) {
				result.set(0, lobs.get(i).getRight());
			}else if(lobs.get(i).getLeft() == Observation.LOCKSTATUS) {
				result.set(1, lobs.get(i).getRight());
			}else if(lobs.get(i).getLeft() == Observation.LOCKPICKING) {
				result.set(2, lobs.get(i).getRight());
			}else if(lobs.get(i).getLeft() == Observation.STRENGH) {
				result.set(3, lobs.get(i).getRight());
			}
		}
		return result;
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
			if(siloPos.equals("nopos")) {
				List<String> tran = myMap.syloPose();
				System.out.println(tran + myAgent.getName());
				siloPos = tran.get(tran.size()-1);
			}
			if(! ((AbstractDedaleAgent)myAgent).getCurrentPosition().equals(siloPos)) {
				
			
				List<String> pl = myMap.getShortestPath(((AbstractDedaleAgent)myAgent).getCurrentPosition(), siloPos);
				if (pl.size() >0) {
					String nextNode = pl.get(0);
					((AbstractDedaleAgent)myAgent).moveTo(nextNode);
	
				} 
			}else {
				message();
			}

			
		}

	}
	private void message() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		msg.setProtocol("TANKER");
		String c  = this.myAgent.getName();
		msg.setContent(c);
		for (int i = 0; i < agentNames.size(); i++) {
			if (!agentNames.get(i).equals(myAgent.getAID().getName())) {
				msg.addReceiver(new AID(agentNames.get(i), AID.ISLOCALNAME));
			}
		}
		((AbstractDedaleAgent) this.myAgent).sendMessage(msg);
	}
	@Override
	public boolean ramasser(List<Couple<String, List<Couple<Observation, Integer>>>> lobs) {
		return false;
	}

	public void deplacement_explo(List<Couple<String, List<Couple<Observation, Integer>>>> lobs, String myPosition) {
		String nextNode = null;
		majMap(lobs, myPosition);
		if (this.openNodes.isEmpty()) {
			myMap.set_complete();

			System.out.println("Exploration successufully done" + myAgent.getName());
		} else {

			List<String> pl = this.myMap.getShortestPathToClosestNode(myPosition, openNodes);
			if (pl.size() > 0) {
				nextNode = pl.get(0);
				moveTo(nextNode);

			}

		}

	}

	protected void majMapComplete(List<Couple<String, List<Couple<Observation, Integer>>>> lobs) {
		int tresor;
		int serrure;
		int force;
		boolean ouvert;
		Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter = lobs.iterator();
		while (iter.hasNext()) {
			Couple<String, List<Couple<Observation, Integer>>> elem = iter.next();
			String nodeId = elem.getLeft();
			tresor = 0;
			serrure = 0;
			force = 0;
			ouvert = false;
			Case c;
			if (elem.getRight().size() > 0) {
				tresor = elem.getRight().get(0).getRight();
				serrure = elem.getRight().get(3).getRight();
				force = elem.getRight().get(2).getRight();
				ouvert = elem.getRight().get(1).getRight() == 1;

			}
			c = new Case(nodeId, tresor, serrure, force, false, ouvert);
			this.myMap.addNode(c);

		}
	}

	protected void majMap(List<Couple<String, List<Couple<Observation, Integer>>>> lobs, String myPosition) {
		int tresor;
		int serrure;
		int force;
		boolean ouvert;
		Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter = lobs.iterator();
		while (iter.hasNext()) {
			Couple<String, List<Couple<Observation, Integer>>> elem = iter.next();
			String nodeId = elem.getLeft();
			if (!this.closedNodes.contains(nodeId)) {
				if (!this.openNodes.contains(nodeId)) {
					this.openNodes.add(nodeId);
					tresor = 0;
					serrure = 0;
					force = 0;
					ouvert = false;
					Case c;
					if (elem.getRight().size() > 0) {
						tresor = elem.getRight().get(0).getRight();
						serrure = elem.getRight().get(3).getRight();
						force = elem.getRight().get(2).getRight();
						ouvert = elem.getRight().get(1).getRight() == 1;

					}
					c = new Case(nodeId, tresor, serrure, force, true, ouvert);
					this.myMap.addNode(c);

					this.myMap.addEdge(myPosition, nodeId);
				} else {
					// the node exist, but not necessarily the edge
					this.myMap.addEdge(myPosition, nodeId);
				}
			}
		}
		sendClassicMessage();
	}

	@Override
	public boolean done() {
		return finished;
	}

	protected void moveTo(String id) {
		boolean b = ((AbstractDedaleAgent) this.myAgent).moveTo(id);
		if (!b) {
			for (int i = 0; i < 3; i++) {
				move_random();
			}

		}

	}

	public void move_random() {
		List<Couple<String, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent) this.myAgent).observe();// myPosition
		Random r = new Random();
		int x = r.nextInt(lobs.size());
		((AbstractDedaleAgent) this.myAgent).moveTo(lobs.get(x).getLeft());
	}

	public boolean ramasser() {
		// TODO Auto-generated method stub
		return false;
	}
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


