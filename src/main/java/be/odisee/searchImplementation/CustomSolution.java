package be.odisee.searchImplementation;

import be.odisee.domain.Exam;
import be.odisee.domain.TimeSlot;
import be.odisee.framework.Solution;

import java.util.*;

public class CustomSolution implements Solution {
    double score;
    Map<Integer, Exam> examMap;
    Map<TimeSlot, List<Exam>> timeSlots;


    public Map<Integer, Exam> getExamMap() {
        return examMap;
    }

    public void setExamMap(Map<Integer, Exam> examMap) {
        this.examMap = examMap;
    }

    public Map<TimeSlot, List<Exam>> getTimeSlots() {
        return timeSlots;
    }

    CustomSolution(Map<Integer, Exam> examMap,
                   Map<TimeSlot, List<Exam>> timeSlots) {
        this.examMap = examMap;
        this.timeSlots = timeSlots;
    }

    CustomSolution(CustomSolution customSolution) {
        this.timeSlots = customSolution.timeSlots;
        this.score = customSolution.score;
        this.examMap = customSolution.examMap;
    }

    public void setTimeSlots(Map<TimeSlot, List<Exam>> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public double getScore() {
        return score;
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
    public CustomSolution clone() {
        try {
            CustomSolution customSolution = (CustomSolution) super.clone();
            customSolution.examMap = this.examMap;
            customSolution.score = this.score;
            customSolution.timeSlots = this.timeSlots;
            return customSolution;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
