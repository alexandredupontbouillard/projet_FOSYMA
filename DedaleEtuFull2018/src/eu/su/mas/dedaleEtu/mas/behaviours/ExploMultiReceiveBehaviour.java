package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.text.AbstractDocument.Content;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.CollectorMultiAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.DummyTankerAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.states.MsgReceiver;
import message.Case;

public class ExploMultiReceiveBehaviour extends MsgReceiver {

	private static final long serialVersionUID = 9088209402507795289L;

	private boolean finished = false;
	protected ExploAgent myagent;
	/**
	 * 
	 * This behaviour is a one Shot. It receives a message tagged with an inform
	 * performative, print the content in the console and destroy itlself
	 * 
	 * @param myagent
	 */
	protected MapRepresentation myMap;

	public ExploMultiReceiveBehaviour(final ExploAgent myagent) {
		this.myagent = myagent;
	}

	public void setMap(MapRepresentation map) {
		myMap = map;
	}

	// methode appelée pour mettre à jour la map lors de la réception d'un message
	// de map
	public void messageClassique(Couple<List<Case>, List<Couple<String, String>>> content) {
		List<Case> nodes = content.getLeft();

		List<Couple<String, String>> edges = content.getRight();
		List<Case> open = new ArrayList();
		List<Case> closed = new ArrayList();
		for (int i = 0; i < nodes.size(); i++) {

			myMap.addNode(nodes.get(i));

			if (!nodes.get(i).is_open()) {
				closed.add(nodes.get(i));
			} else {

				open.add(nodes.get(i));

			}
		}
		for (int i = 0; i < edges.size(); i++) {
			myMap.addEdge(edges.get(i).getLeft(), edges.get(i).getRight());
		}

		myagent.maj(open, closed);

	}

	public synchronized void action() {
		final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		final ACLMessage msg = this.myAgent.receive(msgTemplate);

		if (myMap != null) {
			

				if (msg != null) {
					if (msg.getProtocol().equals("CLASSIQUE")) {
						try {
							Couple<List<Case>, List<Couple<String, String>>> c = (Couple<List<Case>, List<Couple<String, String>>>) msg
									.getContentObject();
							if (((ExploAgent) myAgent).explore()) {
								messageClassique(c);
							}

						} catch (UnreadableException e) {

							e.printStackTrace();
						}
					}

				
					else if (msg.getProtocol().equals("T")) {

					if (((ExploAgent) myAgent).isDroping()) {
						((CollectorMultiAgent) myAgent).siloOnpose();

						((AbstractDedaleAgent) myAgent).emptyMyBackPack(msg.getContent());
						((ExploAgent) myAgent).dropped();
						
					}
				}else {
					System.out.println(msg.getProtocol());
				}
			}

		}
	}

	public boolean done() {
		return finished;
	}

}
