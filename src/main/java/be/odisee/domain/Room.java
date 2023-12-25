package be.odisee.domain;

import java.io.Serializable;

public class Room extends Resource implements Serializable{

	private int capacity;
	private String location;
	private double penaltyWeight;
	
	public Room(int ID, String name){
		super(ID, name);
	}
		
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capaciteit) {
		this.capacity = capaciteit;
	}	

	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}
	
	

	public double getPenaltyWeight() {
		return penaltyWeight;
	}

	public void setPenaltyWeight(double penaltyWeight) {
		this.penaltyWeight = penaltyWeight;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Room room1 = new Room(1, "D01");
		room1.setCapacity(50);
		System.out.println(room1.getID());
		Room room2 = new Room(2, "D02");
		System.out.println(room2.getID());
		Room room3 = new Room(3, "D03");
		System.out.println(room3.getID());
	}

}
