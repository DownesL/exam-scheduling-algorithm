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
        bestSolution = currentSolution.clone();

        fillTimeTable();

        bestSolution.setScore(calculateTotalScore(currentSolution));
        bestSolution.getObjectiveValue();
    }

    private void fillTimeTable() {
        Stack<Student> studentQueue = (students
                .values()
                .stream()
                .sorted(
                        Comparator.comparingInt(s -> s.getExamIds().size())
                )
                .collect(Collectors.toCollection(Stack::new)));

        //ids of all the exams that have been added to the timeSlots
        Set<Integer> examIds = new LinkedHashSet<>();

        TimeSlot[] initialTimeSlotIterator = timeSlots.keySet().toArray(new TimeSlot[0]);
        Arrays.sort(initialTimeSlotIterator);

        while (!studentQueue.isEmpty()) {
            Student student = studentQueue.pop();
            Set<Integer> sEids = new HashSet<>(student.getExamIds());

            //This iterator makes sure the timeslots are filled from start to end
            Iterator<TimeSlot> timeSlotIterator = stream(initialTimeSlotIterator.clone()).iterator();
            TimeSlot timeSlot = timeSlotIterator.next();

            for (int i : sEids) {
                if (examIds.contains(i)) {
                    continue;
                }
                timeSlots.get(timeSlot).add(exams.get(i));
                timeSlot = timeSlotIterator.next();
            }
            examIds.addAll(sEids);
        }
        currentSolution.setTimeSlots(timeSlots);
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
        /*
            algorithm that flattens timeslots to distribute the exams more evenly over the period
            this is done by placing the trailing empty timeslots before the busiest timeslots
        */
        System.out.println("initial score: " + bestSolution.getScore());
        flattenSchedule();

        double score = calculateTotalScore(currentSolution);
        currentSolution.setScore(score);

        System.out.println("Flattenscore: " + currentSolution.getScore());

        if (score < bestSolution.getScore()) {
            bestSolution = currentSolution.clone();
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

        int supplier, receiver, packetIndex;
        Random random = new Random(2344532L);

        timeSlots = bestSolution.timeSlots;

        int count = 0;
        for (int i = 0; i < numberOfIterations; i++) {
            supplier = random.nextInt(timeSlots.size());
            receiver = random.nextInt(timeSlots.size());

            while (supplier == receiver) {
                receiver = random.nextInt(timeSlots.size());
            }

            if (supplier > receiver) {
                int temp = receiver;
                receiver = supplier;
                supplier = temp;
            }
            TimeSlot[] keySet = timeSlots.keySet().toArray(TimeSlot[]::new);

            TimeSlot supplierTimeslot = keySet[supplier];
            TimeSlot receiverTimeslot = keySet[receiver];

            List<Exam> supplierExams = timeSlots.get(supplierTimeslot);
            List<Exam> receiverExams = timeSlots.get(receiverTimeslot);

            if (supplierExams.isEmpty()) {
                continue;
            }
            packetIndex = random.nextInt(supplierExams.size());

            Exam packet = supplierExams.get(packetIndex);
            supplierExams.remove(packetIndex);
            receiverExams.add(packet);

            List<Integer> receiverExamIds = receiverExams.stream().map(Exam::getID).toList();
            boolean hardConstrainFail = false;
            for (int studentId : packet.getSID()) {
                Student stud = students.get(studentId);
                List<Integer> studExamIds = new ArrayList<>(stud.getExamIds());
                int initialLen = studExamIds.size();
                studExamIds.retainAll(receiverExamIds);
                int endLen = studExamIds.size();
                if (endLen > 1) {
                    hardConstrainFail = true;
                    break;
                }
            }

            if (hardConstrainFail) {
                receiverExams.remove(packet);
                supplierExams.add(packet);
                continue;
            }else {
                count++;
            }
            currentSolution.setTimeSlots(timeSlots);

            double newScore = calculateTotalScore(currentSolution);
            currentSolution.setScore(newScore);

            if (newScore < bestSolution.getScore()) {
                bestSolution = currentSolution.clone();

            } else {
                receiverExams.remove(packet);
                supplierExams.add(packet);
            }

//            System.out.println(newScore);

        }

        System.out.println("final score: " + bestSolution.getScore());

        return 0;
    }

    private void flattenSchedule() {
        timeSlots = new HashMap<>(currentSolution.timeSlots);
        int zeroCount = (int) timeSlots.values().stream().filter(List::isEmpty).count();
        List<Integer> idsByLength = timeSlots
                .keySet()
                .stream()
                .sorted((timeSlot, t1) -> Integer.compare(timeSlots.get(timeSlot).size(), timeSlots.get(t1).size()))
                .map(TimeSlot::getID)
                .toList();

        int lastBig = idsByLength.size() - 1;
        int firstBig = idsByLength.size() - 1 - zeroCount;

        List<Integer> arr = idsByLength.subList(firstBig, lastBig);

        Map<TimeSlot, List<Exam>> copyMap = new HashMap<>();
        int originalIndex = 0;
        for (int i = 0; i < timeSlots.size(); i++) {
            if (arr.contains(i)) {
                copyMap.put(new TimeSlot(i), new ArrayList<>());
            } else {
                copyMap.put(new TimeSlot(i), timeSlots.get(new TimeSlot(originalIndex)));
                originalIndex++;
            }
        }
        currentSolution.setTimeSlots(copyMap);
    }

    public double calculateTotalScore(CustomSolution solution) {
        double score = 0.0;
        // iterate over students and calculate the score of each schedule
        for (Student student : students.values()) {
            List<Integer> examIds = student.getExamIds();
            int[] schedule = new int[13];
            double studentScore = 0;
            Arrays.fill(schedule, 0);
            for (int examId : examIds) {
                Optional<TimeSlot> ts = getExamIndex(solution, examId);
                if (ts.isPresent()) {
                    schedule[ts.get().getID()] = 1;
                }
            }
            int last = -1;
            for (int i = 0; i < schedule.length; i++) {
                if (schedule[i] == 1) {
                    if (last == -1) {
                        last = i;
                    } else {
                        int delta = i - last;
                        if (delta > 4) {
                            delta = 5;
                        }
                        studentScore += schedule[i] * (2 << (3 - delta + 1));
                        last = i;
                    }
                }
            }
            score += studentScore;
        }
        score /= students.size();
        return score;
    }

    private Optional<TimeSlot> getExamIndex(CustomSolution solution, int examId) {
        for (TimeSlot timeSlot : solution.getTimeSlots().keySet()) {
            Optional<Exam> exam = solution.getTimeSlots()
                    .get(timeSlot)
                    .stream()
                    .filter(exam1 -> exam1.getID() == examId)
                    .findFirst();
            if (exam.isPresent()) {
                return Optional.ofNullable(timeSlot);
            }
        }
        return Optional.empty();
    }
}
