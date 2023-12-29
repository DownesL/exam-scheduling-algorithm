package be.odisee.framework;

import be.odisee.domain.Student;

import java.util.Set;

public interface Move {

    void doMove();
    void undoMove();

    Set<Integer> affectedStudents();
}
