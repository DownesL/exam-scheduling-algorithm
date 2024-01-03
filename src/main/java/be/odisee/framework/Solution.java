package be.odisee.framework;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;

import java.util.List;
import java.util.Map;

public interface Solution extends Cloneable, Comparable<Solution> {


    void logForBenchmark();

    Object clone();

    double getTotalCost();
}
