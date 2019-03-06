package message;

import java.util.Date;

import jade.util.leap.Serializable;

public class Case implements Serializable{
	private String id;
	private int tresor;
	private int serrurerie;
	private Date d;
	public Case(String id,int resor, int serrurerie, int force) {
		this.id=id;
		this.tresor=resor;
		this.serrurerie=serrurerie;
		this.force=force;
		d = new Date();
	}
	public String getId() {
		return id;
	}
	public Date getDate() {
		return d;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int getTresor() {
		return tresor;
	}
	public void setTresor(int tresor) {
		this.tresor = tresor;
	}
	public int getSerrurerie() {
		return serrurerie;
	}
	public void setSerrurerie(int serrurerie) {
		this.serrurerie = serrurerie;
	}
	public int getForce() {
		return force;
	}
	public void setForce(int force) {
		this.force = force;
	}
	private int force;
	
}
