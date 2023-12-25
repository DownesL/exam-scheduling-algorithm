package be.odisee.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Student extends Attendee implements Serializable,Comparable {
	
	private List<Integer> examIds;
	
	public Student(String name){
		super(name);
	}
	
	public Student(int ID){
		super(ID);
		examIds = new ArrayList<Integer>();
	}	

	public List<Integer> getExamIds() {
		return examIds;
	}

	public void setExamIds(List<Integer> eid) {
		this.examIds = eid;
	}

	@Override
	public int compareTo(Object o) {
		return Integer.compare(this.getID(), ((Student) o).getID());
	}
}
