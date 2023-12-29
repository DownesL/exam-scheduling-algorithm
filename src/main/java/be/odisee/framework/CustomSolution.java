package be.odisee.framework;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;

import java.util.*;

import static be.odisee.framework.SearchHelper.getExamIndex;


public class CustomSolution implements Solution {
    double totalCost;
    Map<Integer, Exam> exams;
    Map<Integer, Student> students;
    Map<TimeSlot, List<Exam>> timeSlots;

    double lastMoveCost;

    Move lastMove;

    public CustomSolution(Map<Integer, Exam> examMap, Map<TimeSlot, List<Exam>> timeSlots, Map<Integer, Student> students) {
        this.exams = examMap;
        this.students = students;
        this.timeSlots = timeSlots;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }

    public Map<Integer, Exam> getExams() {
        return exams;
    }

    public void setExams(Map<Integer, Exam> exams) {

        this.exams = exams;
    }

    public Map<Integer, Student> getStudents() {
        return students;
    }

    public Map<TimeSlot, List<Exam>> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(Map<TimeSlot, List<Exam>> timeSlots) {

        this.timeSlots = timeSlots;
    }

    public double getTotalCost() {
        return this.totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public CustomSolution clone() {
        try {
            CustomSolution customSolution = (CustomSolution) super.clone();
            customSolution.exams = this.exams;
            customSolution.totalCost = this.totalCost;
            customSolution.timeSlots = this.timeSlots;
            return customSolution;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


    public double calculateAndSetTotalCost() {
        double score = 0.0;
        // iterate over students and calculate the score of each schedule
        for (Student student : students.values()) {
            List<Integer> examIds = student.getExamIds();
            double studentScore = getStudentCost(examIds);
            score += studentScore;
        }
        //average for the students
//        score /= students.size();
        this.totalCost = score;
        return score;
    }

    private double getStudentCost(List<Integer> examIds) {
        int[] schedule = new int[13];
        double studentScore = 0;
        Arrays.fill(schedule, 0);
        for (int examId : examIds) {
            Optional<TimeSlot> ts = getExamIndex( this, examId);
            ts.ifPresent(timeSlot -> schedule[timeSlot.getID()] = 1);
        }
        int last = -1;
        for (int i = 0; i < schedule.length; i++) {
            if (schedule[i] == 1) {
                if (last == -1) {
                    last = i;
                } else {
                    int delta = i - last;
                    if (delta > 5) {
                        continue;
                    }
                    studentScore += schedule[i] * (1 << (5 - delta));
                    last = i;
                }
            }
        }
        return studentScore;
    }

    public double moveCost(Move move) {
        Set<Integer> affectedStudentIDS = move.affectedStudents();
        double scoreAfter = getStudentsCost(affectedStudentIDS);
        move.undoMove();
        double scoreBefore = getStudentsCost(affectedStudentIDS);
        move.doMove();
        return scoreAfter - scoreBefore;
    }

    private double getStudentsCost(Set<Integer> affectedStudentIDS) {
        double scoreAfter = 0;
        for (int studentId : affectedStudentIDS) {
            scoreAfter += getStudentCost(students.get(studentId).getExamIds());
        }
        return scoreAfter;
    }


    @Override
    public int compareTo(Solution o) {
        return Comparator.comparingDouble(Solution::getTotalCost).compare(this, o);
    }
}
