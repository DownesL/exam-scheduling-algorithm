package be.odisee.domain;

import java.io.Serializable;

public class Attendee extends Resource implements Serializable {

    public Attendee(int ID, String name) {
        super(ID, name);
    }

    public Attendee(String name) {
        super(name);
    }

    public Attendee(int ID) {
        super(ID);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Attendee att0 = new Attendee("peter");
        System.out.println(att0.getID());
        att0.setID(3);
        System.out.println(att0.getID());
        Attendee att1 = new Attendee("test");
        System.out.println(att1.getID());
        Attendee att2 = new Attendee("test1");
        System.out.println(att2.getID());
        Attendee att3 = new Attendee("test2");
        System.out.println(att3.getID());
    }

}
