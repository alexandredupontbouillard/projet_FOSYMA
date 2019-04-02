package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.Date;
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
import message.Case;

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
		public void messageClassique(Couple<List<Case>,List<Couple<String,String>>> content) {
			System.out.println("bien reçu"+myAgent.getName());
			List<Case> nodes = content.getLeft();
			
			List<Couple<String,String>> edges = content.getRight();
			List<Case> open = new ArrayList();
			List<Case> closed = new ArrayList();
			for(int i =0; i<nodes.size();i++) {
				myMap.addNode(nodes.get(i));
				if(!nodes.get(i).isOpen()){
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
		public void messageInterblocage(Couple<List<Case>,List<Couple<String,String>>> content) {
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
								if(((ExploreMultiAgent) myAgent).explore()) {
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


