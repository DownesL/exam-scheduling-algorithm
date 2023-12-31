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

    int[][] conflictMatrix;

    Move lastMove;

    public CustomSolution(Map<Integer, Exam> examMap, Map<TimeSlot, List<Exam>> timeSlots, Map<Integer, Student> students) {
        this.exams = examMap;
        this.students = students;
        this.timeSlots = timeSlots;
        initializeConflictMatrix();
    }

    private void initializeConflictMatrix() {
        int size = exams.size();
        conflictMatrix = new int[size][size];
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                Exam exam1 = exams.get(i);
                Exam exam2 = exams.get(j);
                List<Integer> students1 = new ArrayList<>(exam1.getSID());
                List<Integer> students2 = new ArrayList<>(exam2.getSID());
                students1.retainAll(students2);
                conflictMatrix[i-1][j-1] = students1.size();
            }
        }
        System.out.println();
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
        this.totalCost = calTotCost();
        return totalCost;
    }
    public double calTotCost() {
        double score = 0.0;
        for (int i = 0; i < exams.size() - 1; i++) {
            for (int j = i + 1; j < exams.size(); j++) {
                score += conflictMatrix[i][j] * proximity(i, j);
            }
        }
        return score;
    }

    private int proximity(int i, int j) {
        Optional<TimeSlot> ts1 = getExamIndex(this,i);
        Optional<TimeSlot> ts2 = getExamIndex(this,j);
        if (ts1.isEmpty() || ts2.isEmpty()) return -1;
        int exam1Index = ts1.get().getID();
        int exam2Index = ts2.get().getID();
        // j >= i + 1
        int delta = exam2Index - exam1Index;
        if (delta < 1 || 5 < delta) return 0;

        return 1 << (5 - delta);
    }

    private double getStudentCost(List<Integer> examIds) {
        int[] schedule = new int[13];
        double studentScore = 0;
        Arrays.fill(schedule, 0);
        for (int examId : examIds) {
            Optional<TimeSlot> ts = getExamIndex(this, examId);
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
//        double scoreAfter = getStudentsCost(affectedStudentIDS);
        double scoreAfter = calTotCost();
        move.undoMove();
//        double scoreBefore = getStudentsCost(affectedStudentIDS);
        double scoreBefore = calTotCost();
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
