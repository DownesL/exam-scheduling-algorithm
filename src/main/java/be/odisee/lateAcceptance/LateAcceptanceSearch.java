package be.odisee.lateAcceptance;

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

public class LateAcceptanceSearch implements SearchAlgorithm {

    CustomSolution currentSolution;
    CustomSolution bestSolution;
    DataReader dataReader;
    SearchHelper helper;

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
        return this.bestSolution;
    }

    @Override
    public Solution getCurrentSolution() {
        return this.currentSolution;
    }

    @Override
    public int execute(int numberOfIterations) {




        return 0;
    }

}
