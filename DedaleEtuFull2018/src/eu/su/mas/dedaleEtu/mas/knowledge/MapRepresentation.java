package eu.su.mas.dedaleEtu.mas.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import java.util.Date;

import dataStructures.tuple.Couple;
import message.Case;

/**
 * This simple topology representation only deals with the graph, not its content.</br>
 * The knowledge representation is not well written (at all), it is just given as a minimal example.</br>
 * The viewer methods are not independent of the data structure, and the dijkstra is recomputed every-time.
 * 
 * @author hc
 */
public class MapRepresentation implements Serializable {

	public enum MapAttribute {
		agent,open
	}

	private static final long serialVersionUID = -1333959882640838272L;

	private Graph g; //data structure
	private Viewer viewer; //ref to the display
	private Integer nbEdges;//used to generate the edges ids
	
	/*********************************
	 * Parameters for graph rendering
	 ********************************/
	
	private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
	private String nodeStyle_open = "node.agent {"+"fill-color: forestgreen;"+"}";
	private String nodeStyle_agent = "node.open {"+"fill-color: blue;"+"}";
	private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open;
	private boolean observation = true;
	
	public boolean is_complete() {
		return !observation;
	}
	public void set_complete() {
		observation = false;
	}
	//https://www.jmdoudoux.fr/java/dej/chap-utilisation_dates.htm
	
	
	public MapRepresentation() {
		System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		this.g= new SingleGraph("My world vision");
		this.g.setAttribute("ui.stylesheet",nodeStyle);
		this.viewer = this.g.display();
		this.nbEdges=0;
	}
	public List<Case> getAllNodes(){
		List<Case> result = new ArrayList<Case>();
		Iterator<Node> ite = g.getNodeIterator();
		Node x;
		boolean b;
		while(ite.hasNext()) {
			x=ite.next();
			
			if(x.getAttribute("ui.label")!=null) {
				String s = x.getAttribute("ui.class");
				b = s!=null;
				result.add(new Case(x.getAttribute("ui.label"), x.getAttribute("tresor"), x.getAttribute("serrure"), x.getAttribute("force"), b,x.getAttribute("date")));
			}
		}
		return result;
	}public List<Case> getAlltreasure(){
		List<Case> result = new ArrayList<Case>();
		Iterator<Node> ite = g.getNodeIterator();
		Node x;
		boolean b;
		while(ite.hasNext()) {
			x=ite.next();
			
			if(x.getAttribute("ui.label")!=null && (int)x.getAttribute("tresor")!=0) {
				String s = x.getAttribute("ui.class");
				b = s!=null;
				result.add(new Case(x.getAttribute("ui.label"), x.getAttribute("tresor"), x.getAttribute("serrure"), x.getAttribute("force"), b,x.getAttribute("date")));
			}
		}
		return result;
	}
	
	public List<Couple<String, String>> getAllEdges(){
		List<Couple<String,String>> result = new ArrayList<Couple<String,String>>();
		Iterator<Edge> ite = g.getEdgeIterator();
		Edge e;
		while(ite.hasNext()) {
			e=ite.next();
			result.add(new Couple<String, String>(e.getSourceNode().getAttribute("ui.label"),e.getTargetNode().getAttribute("ui.label")));
		}
		return result;
	}
	/**
	 * Associate to a node an attribute in order to identify them by type. 
	 * @param id
	 * @param mapAttribute
	 */
	public void addNode(Case c) {
		MapAttribute m=null;
		if(c.is_open()) {
			m= MapAttribute.open;
		}
		addNode(c.getId(),m,c.getDate(),c.getSerrurerie(),c.getForce(),c.getTresor(),c.is_ouvert());
	}
	public void addNodeF(Case c) {
		
		addNode(c.getId(),null,c.getDate(),c.getSerrurerie(),c.getForce(),c.getTresor(),c.is_ouvert());
	}
	
	public void addNode(String id,MapAttribute mapAttribute,Date d,int serrure,int force,int t,boolean ouvert){
		Node n;
		if (this.g.getNode(id)!=null){
			n=this.g.getNode(id);
			if( n.getAttribute("ui.class")!=null) {
				if(mapAttribute==null) {
					String s=null;
					n.changeAttribute("ui.class", s);
				}
				
			}
			//n.addAttribute("ui.label",id);
			Date x =(Date)n.getAttribute("date");
			if(x.before(d)){
				n.changeAttribute("date", d);
				n.changeAttribute("tresor", t);
				n.changeAttribute("serrure",serrure);
				n.changeAttribute("force",force);
				n.changeAttribute("coffre_ouvert", ouvert);
			}
			
		}
		else{
			String s = null;
			if(mapAttribute!=null) {
				s = MapAttribute.open.toString();
			}
			n=this.g.addNode(id);
			n.addAttribute("ui.label",id);
			n.addAttribute("ui.class", s);
			n.addAttribute("date", d);
			n.addAttribute("tresor", t);
			n.addAttribute("serrure",serrure);
			n.addAttribute("force",force);
			n.addAttribute("coffre_ouvert", ouvert);
		}
		
		
		
		
	}
	public boolean containNode(String node) {

		return ! (g.getNode(node)==null);
	}

	/**
	 * Add the node id if not already existing
	 * @param id
	 */


   /**
    * Add the edge if not already existing.
    * @param idNode1
    * @param idNode2
    */
	public void addEdge(String idNode1,String idNode2){
		try {
			if(containNode(idNode1) && containNode(idNode2)) {
				
			
				this.nbEdges++;
				this.g.addEdge(this.nbEdges.toString(), idNode1, idNode2);
			}
		}catch (EdgeRejectedException e){
			//Do not add an already existing one
			this.nbEdges--;
		}
		
	}

	/**
	 * Compute the shortest Path from idFrom to IdTo. The computation is currently not very efficient
	 * 
	 * @param idFrom id of the origin node
	 * @param idTo id of the destination node
	 * @return the list of nodes to follow
	 */
	public List<String> getShortestPath(String idFrom,String idTo){
		List<String> shortestPath=new ArrayList<String>();

		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));
		dijkstra.compute();//compute the distance to all nodes from idFrom
		List<Node> path=dijkstra.getPath(g.getNode(idTo)).getNodePath(); //the shortest path from idFrom to idTo
		Iterator<Node> iter=path.iterator();
		while (iter.hasNext()){
			shortestPath.add(iter.next().getId());
		}
		dijkstra.clear();
		shortestPath.remove(0);//remove the current position
		return shortestPath;
	}
	public List<String> getShortestPathToClosestNode(String idFrom, List<String> openNodes){
		List<String> shortestPath=new ArrayList<String>();

		Dijkstra dijkstra = new Dijkstra();//number of edge
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(idFrom));
		dijkstra.compute();//compute the distance to all nodes from idFrom
		Double paths_sizes=dijkstra.getPathLength(g.getNode(openNodes.get(0)));
		int index=0;
		Double m;
		// on cherche le noeud ouvert le plus proche
		for(int i =1 ; i<openNodes.size();i++) {
			m=dijkstra.getPathLength(g.getNode(openNodes.get(i)));
			if(paths_sizes > m ) {
				paths_sizes = m;
				index = i;
			}
		}
		List<Node> path=dijkstra.getPath(g.getNode(openNodes.get(index))).getNodePath(); 

		Iterator<Node> iter=path.iterator();
		while (iter.hasNext()){
			shortestPath.add(iter.next().getId());
		}
		dijkstra.clear();
		shortestPath.remove(0);//remove the current position
		return shortestPath;
		
	}
}
