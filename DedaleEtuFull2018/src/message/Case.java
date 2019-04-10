package message;

import java.util.Date;

import jade.util.leap.Serializable;

public class Case implements Serializable{
	private String id;
	private int tresor;
	private int serrurerie;
	private Date d;
	private boolean ouvert=true;
	private boolean coffre_ouvert=false;

	public Case(String id,int resor, int serrurerie, int force,boolean ouvert,boolean coffre_ouvert) {
		this.id=id;
		this.tresor=resor;
		this.serrurerie=serrurerie;
		this.force=force;
		d = new Date();
		this.ouvert=ouvert;
		this.coffre_ouvert=coffre_ouvert;
	}
	public Case(String id,int resor, int serrurerie, int force,boolean ouvert,Date d) {
		this.id=id;
		this.tresor=resor;
		this.serrurerie=serrurerie;
		this.force=force;
		this.d = d;
		this.ouvert=ouvert;
	}
	public boolean is_open() {
		return ouvert;
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
	public boolean is_ouvert(){
		return coffre_ouvert;
	}
	public void set_ouvert(boolean b) {
		ouvert = b;
	}
	public void ouvrir_coffre() {
		coffre_ouvert=true;
	}
}
