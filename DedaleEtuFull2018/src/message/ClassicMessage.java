package message;

import java.util.List;

import jade.util.leap.Serializable;

public class ClassicMessage implements Serializable {
	
	private List<Case> content;
	//on a un tableau de tableau de taille 5 dont le premier �l�ment est l'id du noeud, le second son �tat (ouvert/ferm�), le troisieme nb de
	//tr�sors le quatri�me la qualit� de serrurerie requise et le dernier la capacite de force
	public ClassicMessage(List<Case> content) {
		this.content = content;
	}
	public List<Case> get_message(){
		return content;
	}
}
