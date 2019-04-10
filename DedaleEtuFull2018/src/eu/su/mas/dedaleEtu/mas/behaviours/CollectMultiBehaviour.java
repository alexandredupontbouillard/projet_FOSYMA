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
import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreMultiAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import message.Case;

public class CollectMultiBehaviour extends ExploMultiBehaviour {
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

	public CollectMultiBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap, List<String> agentNames) {
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
	public synchronized void action() {
		super.action();
	}
	protected boolean ramasser(List<Couple<String, List<Couple<Observation, Integer>>>> lobs) {
		return super.ramasser( lobs);
			
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
