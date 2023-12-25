package be.odisee.domain;

import java.io.Serializable;

public abstract class Resource implements Serializable{	
	
	private int ID;
	private transient static int ID_=0;
	private String name;
	private transient int internalID ;
	
	public Resource(int ID, String name){
		this.ID = ID;
		this.name = name;
		this.internalID = ID_++;
	}
	
	public Resource(int ID){
		this.ID = ID;
		this.internalID = ID_++;
	}
	
	public Resource(String name){
		this.name = name;
		this.internalID = ID_++;
		this.ID = getInternalID();
	}

	public void setID(int ID){
		this.ID = ID;
	}
	
	public int getID(){
		return ID;
	}	
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public int getInternalID() {
		return internalID;
	}	

}
