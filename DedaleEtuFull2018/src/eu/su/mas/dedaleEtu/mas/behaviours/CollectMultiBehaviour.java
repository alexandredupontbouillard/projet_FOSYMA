package eu.su.mas.dedaleEtu.mas.behaviours;

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
import eu.su.mas.dedaleEtu.mas.agents.dummies.CollectorMultiAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import message.Case;

public class CollectMultiBehaviour extends SimpleBehaviour{
	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;
	private boolean interblocage = false;
	/**
	 * Current knowledge of the agent regarding the environment
	 */
	protected MapRepresentation myMap;
	private Case next_treasure;
	/**
	 * Nodes known but not yet visited
	 */
	protected ArrayList<String> openNodes;
	/**
	 * Visited nodes
	 */
	private Set<String> closedNodes;
	protected List<String> agentNames;
	private List<String> objectives;
	private boolean gotosilo=false;

	public CollectMultiBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap, List<String> agentNames) {
		super(myagent);
		this.myMap = myMap;
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
	public synchronized void action() {
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

		} else {
			if (ramasser(lobs)) {
				
				List<String> treasure_list = myMap.getAlltreasureClosed();
				System.out.println(treasure_list + myAgent.getName());
				String myPosition = ((AbstractDedaleAgent) this.myAgent).getCurrentPosition();
				if (treasure_list.size() > 0) {

					List<String> pl = this.myMap.getShortestPath(myPosition, treasure_list.get(0));
					if (pl.size() >0) {
						String nextNode = pl.get(0);
						((AbstractDedaleAgent)myAgent).moveTo(nextNode);

					} 
				} else {
					for (int i = 0; i < 10; i++) {
						move_random();
					}

					finished = true;
				}
			}

			majMapComplete(lobs);
			sendClassicMessage();

		}

	}
		protected boolean ramasser(List<Couple<String, List<Couple<Observation, Integer>>>> lobs) {
			if(objectives==null) {
				objectives = myMap.syloPose();
				
			}
			if(gotosilo) {
				List<String> pl = this.myMap.getShortestPathToClosestNode(((AbstractDedaleAgent)myAgent).getCurrentPosition(), objectives);
				if (pl.size() > 0) {
					String nextNode = pl.get(0);
					moveTo(nextNode);

				}else {
					((AbstractDedaleAgent)myAgent).dropOff();
				}
			}
			else if (lobs.get(0).getRight().size() > 0) {
				ArrayList<Integer> h = transfoLobs(lobs.get(0).getRight());
				
				if (h.get(0) > 0) {
					if (h.get(1) != 1) {
						((AbstractDedaleAgent) this.myAgent).openLock(Observation.GOLD);
						
					}
					lobs = ((AbstractDedaleAgent) this.myAgent).observe();
					h=transfoLobs(lobs.get(0).getRight());
					if (h.get(1) == 1) {
						((AbstractDedaleAgent)myAgent).pick();
						gotosilo = true;
						addNodeMypos(lobs);
						

						return false;

					} else {
						return false;
					}
				}
				return true;
			}
			return true;
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

		protected void move_random() {
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
			((CollectorMultiAgent) this.myAgent).setMap(myMap);
		}
}
