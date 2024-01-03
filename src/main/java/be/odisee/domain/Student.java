package be.odisee.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Student extends Attendee implements Serializable,Comparable<Student> {
	
	private List<Integer> examIds;
	
	public Student(String name){
		super(name);
	}
	
	public Student(int ID){
		super(ID);
		examIds = new ArrayList<>();
	}	

	public List<Integer> getExamIds() {
		return examIds;
	}

	public void setExamIds(List<Integer> eid) {
		this.examIds = eid;
	}

	@Override
	public int compareTo(Student o) {
		return Integer.compare(this.getID(), o.getID());
	}
}
