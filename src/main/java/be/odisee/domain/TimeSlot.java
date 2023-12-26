package be.odisee.domain;

import java.io.Serializable;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TimeSlot timeSlot = (TimeSlot) o;
		return ID == timeSlot.ID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ID);
	}
}
