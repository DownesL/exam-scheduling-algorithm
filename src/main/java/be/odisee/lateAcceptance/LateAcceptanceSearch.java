package be.odisee.lateAcceptance;

import be.odisee.data.DataReader;
import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;
import be.odisee.framework.*;

import java.util.*;


public class LateAcceptanceSearch implements SearchAlgorithm {

    CustomSolution currentSolution;
    CustomSolution bestSolution;
    DataReader dataReader;
    SearchHelper helper;

    PriorityQueue<Solution> recentSolutions;

    public LateAcceptanceSearch(DataReader dataReader, RandomGenerator randomGenerator) {
        helper = new SearchHelper(randomGenerator);
        this.dataReader = dataReader;

        Map<Integer, Exam> exams = dataReader.getExams();
        Map<TimeSlot, List<Exam>> timeSlots = new HashMap<>();
        Map<Integer, Student> students = dataReader.getStudents();

        dataReader.getTimeslots().forEach(
                timeSlot -> timeSlots.put(timeSlot, new ArrayList<>())
        );

        currentSolution = new CustomSolution(exams, timeSlots, students);

        helper.fillTimeTable(currentSolution);

        recentSolutions = new PriorityQueue<>(Comparator.reverseOrder());

        initializeBestSolution();
    }

    private void initializeBestSolution() {
        currentSolution.calculateAndSetTotalCost();
        bestSolution = currentSolution.clone();

        /*
            algorithm that flattens timeslots to distribute the exams more evenly over the period
            this is done by placing the trailing empty timeslots before the busiest timeslots
        */
        System.out.println("initial score: " + bestSolution.calculateAndSetTotalCost());

        helper.flattenSchedule(currentSolution, 3);

        double score = currentSolution.calculateAndSetTotalCost();

        System.out.println("Flattenscore: " + score);

        if (score < bestSolution.calculateAndSetTotalCost()) {
            bestSolution = currentSolution.clone();
        }

        recentSolutions.add(currentSolution);
    }

    @Override
    public Solution getBestSolution() {
        return this.bestSolution;
    }

    @Override
    public Solution getCurrentSolution() {
        return this.currentSolution;
    }

    @Override
    public Solution execute(int numberOfIterations) {

        for (int i = 0; i < numberOfIterations; i++) {
            if (i % 1000 == 0) {
                System.out.print("=");
                for (int j = 0; j < 100; j++) {
                    boolean hasChanged = helper.performTimeSlotSwitcheroo(currentSolution);
                    if (hasChanged) {
                        checkForImprovement(currentSolution.getLastMove());
                    }
                }

            } else {
                boolean hasChanged = helper.performExamSwitcheroo(currentSolution);
                if (hasChanged) {
                    checkForImprovement(currentSolution.getLastMove());
                }
            }
        }

        while (recentSolutions.size() > 1) {
            recentSolutions.remove();
        }
        System.out.println(recentSolutions.peek().getTotalCost() / currentSolution.getExams().size());
        System.out.println(recentSolutions.peek().getTotalCost());

        bestSolution = ((CustomSolution) recentSolutions.peek()).clone();
        return bestSolution;
    }

    private int count = 0;
    boolean doOther = false;
    @Override
    public void checkForImprovement(Move move) {
        double tempScore = currentSolution.calTotCost();
        if (recentSolutions.size() == 0)
            return;
        if (tempScore < recentSolutions.peek().getTotalCost()) {
            currentSolution.setTotalCost(tempScore);
            recentSolutions.add(currentSolution.clone());
//            System.out.println(tempScore);
            if (recentSolutions.size() > 13) {
                recentSolutions.remove();
            }
        } else {
            count++;
            if (count % 100 == 0) {
                doOther = !doOther;
            }
            move.undoMove();
        }
    }

    ;
}
