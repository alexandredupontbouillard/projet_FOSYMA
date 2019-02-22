package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.AbstractDocument.Content;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ExploSoloReceiveBehaviour extends SimpleBehaviour {

		private static final long serialVersionUID = 9088209402507795289L;

		private boolean finished=false;

		/**
		 * 
		 * This behaviour is a one Shot.
		 * It receives a message tagged with an inform performative, print the content in the console and destroy itlself
		 * @param myagent
		 */
		private MapRepresentation myMap;
		public ExploSoloReceiveBehaviour(final Agent myagent,MapRepresentation mymap) {
			super(myagent);
			this.myMap = mymap;

		}


		public void action() {
			//1) receive the message
			String closedNode;
			List<String> openNodes = new ArrayList<String>();
			final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);			
			System.out.println("j'ai bien reçu");
			final ACLMessage msg = this.myAgent.receive(msgTemplate);
			if (msg != null) {		
				System.out.println(this.myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
				if(msg.getContent().equals("CLASSIQUE")) {
					try {
						Couple<List<Couple<String,List<Couple<Observation,Integer>>>>,String> content =(Couple<List<Couple<String,List<Couple<Observation,Integer>>>>,String>) msg.getContentObject();
						
						String nextNode=null;
						Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=content.getLeft().iterator();
						closedNode = content.getRight();
						myMap.addNode(closedNode, MapAttribute.agent);
						while(iter.hasNext()){
							String nodeId=iter.next().getLeft();
							if (!closedNode.equals(nodeId)){
								
									openNodes.add(nodeId);
									if(! myMap.containNode(nodeId)) {
										myMap.addNode(nodeId, MapAttribute.open);
									}
									myMap.addEdge(closedNode, nodeId);
									
								
							if (nextNode==null) nextNode=nodeId;
							
							}
						}
					
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
				}
				else if(msg.getContent().equals("INTERBLOCAGE")) {
					
				}
				
				
				
			}else{
				block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
			}
		}

		public boolean done() {
			return finished;
		}

}


