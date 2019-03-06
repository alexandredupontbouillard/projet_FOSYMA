package message;

import java.util.List;

import jade.util.leap.Serializable;

public class ClassicMessage implements Serializable {
	
	private List<Case> content;
	//on a un tableau de tableau de taille 5 dont le premier élément est l'id du noeud, le second son état (ouvert/fermé), le troisieme nb de
	//trésors le quatrième la qualité de serrurerie requise et le dernier la capacite de force
	public ClassicMessage(List<Case> content) {
		this.content = content;
	}
	public List<Case> get_message(){
		return content;
	}
}
