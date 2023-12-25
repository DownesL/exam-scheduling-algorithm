package be.odisee.searchImplementation;

import be.odisee.data.DataReader;
import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;
import be.odisee.framework.SearchAlgorithm;
import be.odisee.framework.Solution;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.*;

public class CustomSearch implements SearchAlgorithm {

    CustomSolution currentSolution;
    CustomSolution bestSolution;
    DataReader dataReader;
    Map<Integer, Exam> exams;
    Map<TimeSlot, List<Exam>> timeSlots;
    Map<Integer, Student> students;

    public CustomSearch(DataReader dataReader) {
        this.dataReader = dataReader;

        /*
         * Max amount of exams per student is 11
         * There are 13 timeslots
         * optimal if students with the least exams have priority
         */
        timeSlots = new HashMap<>();
        dataReader.getTimeslots().forEach(
                timeSlot -> timeSlots.put(timeSlot, new ArrayList<>())
        );
        exams = dataReader.getExams();
        students = dataReader.getStudents();

        currentSolution = new CustomSolution(exams, timeSlots);
        bestSolution = currentSolution;
        bestSolution.setScore(calculateTotalScore(currentSolution));
        bestSolution.getObjectiveValue();

        fillTimeTable();
    }

    private void fillTimeTable() {

        Stack<Student> studentQueue = (students
                .values()
                .stream()
                .sorted(
                        Comparator.comparingInt(s -> s.getExamIds().size())
                )
                .collect(Collectors.toCollection(Stack::new)));

        Set<Integer> examIds = new LinkedHashSet<>();

        List<List<Integer>> studentExams = new ArrayList<>();
        Student student = studentQueue.peek();
        if (student != null) {
            examIds.addAll(student.getExamIds());
        }


        TimeSlot[] initialTimeSlotIterator = timeSlots.keySet().toArray(new TimeSlot[0]);
        Arrays.sort(initialTimeSlotIterator);

        while (!studentQueue.isEmpty()) {
            student = studentQueue.pop();
            Set<Integer> sEids = new HashSet<>(student.getExamIds());
            int len = sEids.size();
            List<Integer> schedule = new ArrayList<>();
            Iterator<TimeSlot> timeSlotIterator = stream(initialTimeSlotIterator.clone()).iterator();
            TimeSlot timeSlot = timeSlotIterator.next();
            for (int i : examIds) {
                //index plaatsen en optelle als hij bijvoegt
                if (sEids.contains(i)) {
                    schedule.add(i);
                    sEids.remove(i);

                    timeSlots.get(timeSlot).add(exams.get(i));
                    timeSlot = timeSlotIterator.next();

                } else if (!sEids.isEmpty() && sEids.size() != len) {
                    schedule.add(0);
                }
            }
            // overige getallen toevoegen aan timeslots vanaf de index bovenaan en dan de index weer gelijkstellen aan 0
            if (!sEids.isEmpty())
                timeSlotIterator.forEachRemaining((ts) -> {
                    if (!sEids.isEmpty()) {
                        int temp = sEids.iterator().next();
                        timeSlots.get(ts).add(exams.get(temp));
                        sEids.remove(temp);
                    }
                });

            schedule.addAll(sEids);
            examIds.addAll(sEids);
            studentExams.add(schedule);
        }

        System.out.println("xx");
        return;
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }

    @Override
    public Solution getCurrentSolution() {
        return null;
    }

    @Override
    public int execute(int numberOfIterations) {
        int iterator = 0;

        return 0;
    }

    public int calculateTotalScore(CustomSolution solution) {
        var ref = new Object() {
            int last = solution.examMap.keySet().iterator().next();
        };
        Optional<Integer> optional = solution.examMap.keySet().stream().reduce((keySum, key) -> {
            int sum = keySum + (2 << (4 - (key - ref.last)));
            ref.last = key;
            return sum;
        });
        return optional.orElse(-1);
    }
}
