package be.odisee.domain;

import java.io.Serializable;

public class TimeSlot implements Serializable, Comparable<TimeSlot> {
	
	private int ID;
	
	public TimeSlot(int ID){
		this.ID  = ID;
	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		ID = id;
	}

	@Override
	public int compareTo(TimeSlot timeSlot) {
		return Integer.compare(this.getID(), timeSlot.getID());

	}
}
