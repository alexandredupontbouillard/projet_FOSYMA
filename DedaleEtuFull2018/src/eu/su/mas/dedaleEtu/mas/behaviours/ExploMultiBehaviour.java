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
import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import message.Case;

import java.util.Date;




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
	protected MapRepresentation myMap;

	/**
	 * Nodes known but not yet visited
	 */
	protected ArrayList<String> openNodes;
	/**
	 * Visited nodes
	 */
	private Set<String> closedNodes;
	protected List<String> agentNames;
	
	public ExploMultiBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap,List<String> agentNames) {
		super(myagent);
		this.myMap=myMap;
		this.openNodes=new ArrayList<String>();
		this.closedNodes=new HashSet<String>();
		this.agentNames=agentNames;

	}
	
	public void maj(List<Case> open,List<Case> closed) {
		for(int i =0;i<closed.size();i++) {
			if(! closedNodes.contains(closed.get(i).getId())) {
				
				openNodes.remove(closed.get(i).getId());
				closedNodes.add(closed.get(i).getId());
				myMap.addNode(closed.get(i));
				
			}
		}
		for(int i =0;i<open.size();i++) {
			if(!openNodes.contains(open.get(i).getId())) {
				if(!closedNodes.contains(open.get(i).getId())) {
					openNodes.add(open.get(i).getId());
					open.get(i).set_ouvert(true);
					myMap.addNode(open.get(i));
				}
			}
		}
	}
	
	public void sendClassicMessage() {
		ACLMessage msg=new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());

		try {
			List<Couple<String,String>> list_edges = myMap.getAllEdges();
			List<Case> list_Nodes = myMap.getAllNodes();
			Couple<List<Case>,List<Couple<String,String>>> c = new Couple<List<Case>,List<Couple<String,String>>>(list_Nodes,list_edges);
			
			
			
			
			msg.setProtocol("CLASSIQUE");
			msg.setContentObject((Serializable) c);
			for(int i =0;i<agentNames.size();i++) {
				if(! agentNames.get(i).equals(myAgent.getAID().getName())) {
					msg.addReceiver(new AID(agentNames.get(i),AID.ISLOCALNAME));
				}
			}
			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
	}
	public void interblocageMessage() {
		
		
	}
	public boolean explore() {
		return !myMap.is_complete();
	}
	

	@Override
	public synchronized void action() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(this.myMap==null) {
			this.myMap= new MapRepresentation();
		}
		
		((ExploreMultiAgent) this.myAgent).setMap(myMap);

		if(!myMap.is_complete()) {
			//0) Retrieve the current position
			String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
			if (myPosition!=null){
				
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
				try {
					this.myAgent.doWait(1500);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//1) remove the current node from openlist and add it to closedNodes.
				this.closedNodes.add(myPosition);
				this.openNodes.remove(myPosition);
	
				this.myMap.addNode(myPosition,null,new Date(),0,0,0);
			
				if(lobs!=null) deplacement_explo(lobs,myPosition);
			}
		

		}
			
		
		
	}
	public void deplacement_explo(List<Couple<String,List<Couple<Observation,Integer>>>> lobs,String myPosition) {
		String nextNode=null;
		Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
		while(iter.hasNext()){
			String nodeId=iter.next().getLeft();
			if (!this.closedNodes.contains(nodeId)){
				if (!this.openNodes.contains(nodeId)){
					this.openNodes.add(nodeId);
					this.myMap.addNode(nodeId, MapAttribute.open, new Date(),0,0,0);
					
					this.myMap.addEdge(myPosition, nodeId);	
				}else{
					//the node exist, but not necessarily the edge
					this.myMap.addEdge(myPosition, nodeId);
				}
			}
		}
		sendClassicMessage();

		
		
		if (this.openNodes.isEmpty()){
			myMap.set_complete();
			finished=true;
			System.out.println("Exploration successufully done, behaviour removed."+myAgent.getName());
		}else{
			
				List<String> pl =this.myMap.getShortestPathToClosestNode(myPosition, openNodes); 
				if(pl.size()>0) {	
					nextNode=pl.get(0);
					boolean b = ((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
					System.out.println(myAgent.getName()+pl.get(pl.size()-1));
					System.out.println(myAgent.getName()+closedNodes);
				}
				
				
			
		}

	}

	@Override
	public boolean done() {
		return finished;
	}

}
