package be.odisee.searchImplementation;

import be.odisee.domain.Exam;
import be.odisee.domain.TimeSlot;
import be.odisee.framework.Solution;

import java.util.*;

public class CustomSolution implements Solution {
    double score;
    Map<Integer, Exam> examMap;
    Map<TimeSlot, List<Exam>> timeSlots;


    CustomSolution(Map<Integer, Exam> examMap,
                   Map<TimeSlot, List<Exam>> timeSlots) {
        this.examMap = examMap;
        this.timeSlots = timeSlots;
    }

    @Override
    public double getObjectiveValue() {
        return this.score;
    }

    @Override
    public void setScore(double value) {
        this.score = value;
    }

    @Override
    public Object clone() {
        return null;
    }
}
