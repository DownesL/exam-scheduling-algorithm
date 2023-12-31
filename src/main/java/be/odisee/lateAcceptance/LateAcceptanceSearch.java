package be.odisee.lateAcceptance;

import be.odisee.data.DataReader;
import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;
import be.odisee.framework.*;

import java.util.*;

import static be.odisee.framework.SearchHelper.flattenSchedule;

public class LateAcceptanceSearch implements SearchAlgorithm {

    CustomSolution currentSolution;
    CustomSolution bestSolution;
    DataReader dataReader;
    SearchHelper helper;

    PriorityQueue<Solution> recentSolutions;
    boolean doOther = false;
    private int count = 0;

    public LateAcceptanceSearch(DataReader dataReader) {
        RandomGenerator randomGenerator = new RandomGenerator();
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

        flattenSchedule(currentSolution);

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
    public int execute(int numberOfIterations) {

        for (int i = 0; i < numberOfIterations; i++) {
            boolean hasChanged;

            if (doOther) {
                hasChanged = helper.performTimeSlotSwitcheroo(currentSolution);
            } else {
                hasChanged = helper.performExamSwitcheroo(currentSolution);
            }
            if (hasChanged) {
                checkForImprovement(currentSolution.getLastMove());
            }
        }
        while (recentSolutions.size() > 1) {
            recentSolutions.remove();
        }
        System.out.println(recentSolutions.peek().getTotalCost() / currentSolution.getExams().size());
        System.out.println(recentSolutions.peek().getTotalCost());
        logForBenchmark();
        return 0;
    }

    private void logForBenchmark() {
        bestSolution.getTimeSlots().forEach((timeSlot, exams1) -> {
            for (Exam exam : exams1) {
                System.out.println(exam.getID() + " " + timeSlot.getID());
            }
        });
    }

    @Override
    public void checkForImprovement(Move move) {
        double newScore = currentSolution.moveCost(move);
        double tempScore = currentSolution.getTotalCost() + newScore;
        if (recentSolutions.size() == 0)
            return;
        System.out.println(tempScore);
        if (tempScore < recentSolutions.peek().getTotalCost()) {
            currentSolution.setTotalCost(tempScore);
            recentSolutions.add(currentSolution.clone());
            if (recentSolutions.size() > 1000) {
                recentSolutions.remove();
            }
        } else {
            count++;
            if (count % 100 == 0) {
//                doOther = !doOther;
            }
            move.undoMove();
        }
    }

}
