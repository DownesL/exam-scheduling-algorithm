package be.odisee.localSearch;

import be.odisee.data.DataReader;
import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;
import be.odisee.framework.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.odisee.framework.SearchHelper.flattenSchedule;

public class CustomSearch implements SearchAlgorithm {

    CustomSolution currentSolution;
    CustomSolution bestSolution;
    DataReader dataReader;
    SearchHelper helper;

    public CustomSearch(DataReader dataReader) {
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
    }

    @Override
    public Solution getBestSolution() {
        return bestSolution;
    }

    /*
    algorithm that picks a random exam and places it in a different timeslot
    and then evaluates the score to see if it improved
    todo: write an algorithm with:
        - hard constraint: student can't have two exams in the same timeslot
        - soft constraint: student's exams should be as far apart as possible
        a. pick 2 different random timeslot index (Supplier, Receiver)
        b. pick random index of the Supplier timeslot (Packet)
        c. place Packet in the Receiver
        d. check hard constraint
            1. get exam with Packet index
            2. get students from exam
            3. loop over students to check overlap in timeslots
                (retainAll: https://stackoverflow.com/questions/2400838/efficient-intersection-of-two-liststring-in-java)
            4. length < 2
        e. calculate the new score & repeat
*/

    @Override
    public Solution getCurrentSolution() {
        return currentSolution;
    }

    @Override
    public int execute(int numberOfIterations) {
        for (int i = 0; i < numberOfIterations; i++) {
            if (i % 1000 == 0) {
                System.out.print(".");
                for (int j = 0; j < 100; j++) {
                    boolean hasChanged = helper.performTimeSlotSwitcheroo(currentSolution);
                    if (hasChanged) {
                        checkForImprovement(currentSolution.getLastMove());
                    }
                }

            } else {
                helper.performExamSwitcheroo(currentSolution);
                checkForImprovement(currentSolution.getLastMove());
            }
//            System.out.println(newScore);
        }

        System.out.println("----------------------------------");
        System.out.println("final score: " + bestSolution.calculateAndSetTotalCost() + " avg/S");
        System.out.println("**********************************");

//        timeSlots.forEach((timeSlot, exams1) -> {
//            for (Exam exam : exams1) {
//                System.out.println(exam.getID() + " " + timeSlot.getID());
//            }
//        });

        return 0;
    }

    private void checkForImprovement(Move move) {
        double newScore = currentSolution.calculateAndSetTotalCost();

         if (newScore < bestSolution.getTotalCost()) {
            bestSolution = currentSolution.clone();
        } else {
            move.undoMove();
        }
    }


}
