package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


/**
 * This behaviour allows an agent to explore the environment and learn the associated topological map.
 * The algorithm is a pseudo - DFS computationally consuming because its not optimised at all.</br>
 * 
 * When all the nodes around him are visited, the agent randomly select an open node and go there to restart its dfs.</br> 
 * This (non optimal) behaviour is done until all nodes are explored. </br> 
 * 
 * Warning, this behaviour does not save the content of visited nodes, only the topology.</br> 
 * Warning, this behaviour is a solo exploration and does not take into account the presence of other agents (or well) and indefinitely tries to reach its target node
 * @author hc
 *
 */
public class ExploMultiBehaviour extends SimpleBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private boolean finished = false;
	private boolean interblocage=false;
	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;

	/**
	 * Nodes known but not yet visited
	 */
	private List<String> openNodes;
	/**
	 * Visited nodes
	 */
	private Set<String> closedNodes;
	private List<String> agentNames;

	public ExploMultiBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap,List<String> agentNames) {
		super(myagent);
		this.myMap=myMap;
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		this.agentNames=agentNames;
		
	}
	public void maj(List<String> open,String closed) {
		if(!closedNodes.contains(closed)) {
			closedNodes.add(closed);
			
		}
		for(int i =0; i < open.size();i++) {
			if(! closedNodes.contains(open.get(i))) {
				if(! openNodes.contains(open.get(i))) {
					openNodes.add(open.get(i));
					
				}
			}
		}
		
	}
	public void maj(List<String> open,List<String> closed) {
		for(int i =0;i<closed.size();i++) {
			if(! closedNodes.contains(closed.get(i))) {
				closedNodes.add(closed.get(i));
				if(openNodes.contains(closed.get(i))) {
					openNodes.remove(closed.get(i));
				}
			}
		}
		for(int i =0;i<open.size();i++) {
			if(!openNodes.contains(open.get(i))) {
				if(!closedNodes.contains(open.get(i))) {
					openNodes.add(open.get(i));
				}
			}
		}
	}
	public void sendClassicMessage(List<Couple<String,List<Couple<Observation,Integer>>>> lobs,String myPosition) {
		ACLMessage msg; 
		
		msg=new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		
		for(int i =0;i<agentNames.size();i++) {
			if(! agentNames.get(i).equals(myAgent.getAID().getName())) {
				msg.addReceiver(new AID(agentNames.get(i),AID.ISLOCALNAME));
			}
		}
		Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
		List<String> m = new ArrayList<String>();
		String nodeId;
		while(iter.hasNext()){
			nodeId=iter.next().getLeft();
			if(nodeId != myPosition) {
				m.add(nodeId);
			}
		}
		m.add(myPosition);
		msg.setContent("CLASSIQUE");
		try {
			msg.setContentObject( (Serializable) m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		
	}
	public void interblocageMessage() {
		ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		try {
			List<Couple<String,String>> list_edges = myMap.getAllEdges();
			List<Couple<String,String>> list_Nodes = myMap.getAllNodes();
			Couple<List<Couple<String,String>>,List<Couple<String,String>>> c = new Couple<List<Couple<String,String>>,List<Couple<String,String>>>(list_Nodes,list_edges);
			
			
			msg.setContent("INTERBLOCAGE");
			msg.setContentObject(c);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		interblocage=false;
	}
	

	@Override
	public void action() {
		interblocage=false;
		if(this.myMap==null)
			this.myMap= new MapRepresentation();
		
		//0) Retrieve the current position
		String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		
		
		
		if (myPosition!=null){
			
			List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
			sendClassicMessage(lobs,myPosition);
			try {
				this.myAgent.doWait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			//1) remove the current node from openlist and add it to closedNodes.
			this.closedNodes.add(myPosition);
			this.openNodes.remove(myPosition);

			this.myMap.addNode(myPosition);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNode=null;
			Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				String nodeId=iter.next().getLeft();
				if (!this.closedNodes.contains(nodeId)){
					if (!this.openNodes.contains(nodeId)){
						this.openNodes.add(nodeId);
						this.myMap.addNode(nodeId, MapAttribute.open);
						this.myMap.addEdge(myPosition, nodeId);	
					}else{
						//the node exist, but not necessarily the edge
						this.myMap.addEdge(myPosition, nodeId);
					}
					if (nextNode==null) nextNode=nodeId;
				}
			}

			//3) while openNodes is not empty, continues.
			if (this.openNodes.isEmpty()){
				//Explo finished
				finished=true;
				System.out.println("Exploration successufully done, behaviour removed.");
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNode==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					nextNode=this.myMap.getShortestPath(myPosition, this.openNodes.get(0)).get(0);
				}
				interblocage = !((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
			
			}

		}
		if(interblocage){
			interblocageMessage();
		}
			
		
		
	}

	@Override
	public boolean done() {
		return finished;
	}

}
