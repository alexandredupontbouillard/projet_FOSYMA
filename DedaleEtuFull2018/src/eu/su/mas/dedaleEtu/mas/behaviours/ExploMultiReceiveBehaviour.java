package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.text.AbstractDocument.Content;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ExploMultiReceiveBehaviour extends SimpleBehaviour {

		private static final long serialVersionUID = 9088209402507795289L;

		private boolean finished=false;
		protected ExploreMultiAgent myagent;
		/**
		 * 
		 * This behaviour is a one Shot.
		 * It receives a message tagged with an inform performative, print the content in the console and destroy itlself
		 * @param myagent
		 */
		protected MapRepresentation myMap;
		public ExploMultiReceiveBehaviour(final ExploreMultiAgent myagent) {
			super(myagent);
			this.myagent=myagent;
		}
		public void setMap(MapRepresentation map) {
			myMap = map;
		}
		public void messageClassique(List<String> content) {
			String closedNode=content.get(content.size()-1);
			content.remove(content.size()-1);
			
			myMap.addNode(closedNode, null);
			for(int i =0 ; i< content.size();i++){
				String nodeId=content.get(i);
				
				if(! myMap.containNode(nodeId)) {
					myMap.addNode(nodeId, MapAttribute.open);
				}
					myMap.addEdge(closedNode, nodeId);
						
					
				
			}
			
			myagent.maj(content, closedNode);	
		}
		public void messageInterblocage(Couple<List<Couple<String,String>>,List<Couple<String,String>>> content) {
			List<Couple<String,String>> nodes = content.getLeft();
			List<Couple<String,String>> edges = content.getRight();
			List<String> open = new ArrayList();
			List<String> closed = new ArrayList();
			for(int i =0; i<nodes.size();i++) {
				if(nodes.get(i).getRight().equals(("closed"))) {
					myMap.addNode(nodes.get(i).getLeft());
					closed.add(nodes.get(i).getLeft());
				}
				else {
					myMap.addNode(nodes.get(i).getLeft(),MapAttribute.open);
					open.add(nodes.get(i).getLeft());

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
							System.out.println("CLASSIQUE");
							try {
								List<String> content =(ArrayList<String>) msg.getContentObject();
								messageClassique(content);
								
		
							} catch (UnreadableException e) {
								
								e.printStackTrace();
							}
						}
						else if(msg.getProtocol().equals("INTERBLOCAGE")) {
							System.out.println("INTERBLOCAGE");
							try {
								Couple<List<Couple<String,String>>,List<Couple<String,String>>> c = (Couple<List<Couple<String,String>>,List<Couple<String,String>>>) msg.getContentObject();
								messageInterblocage(c);
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


