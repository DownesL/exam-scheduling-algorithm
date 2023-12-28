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
    TimeSlot[] timeSlotKeys;

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
        timeSlotKeys = timeSlots.keySet().toArray(TimeSlot[]::new);
        exams = dataReader.getExams();
        students = dataReader.getStudents();

        currentSolution = new CustomSolution(exams, timeSlots);
        bestSolution = currentSolution.clone();

        fillTimeTable();

        bestSolution.setScore(calculateTotalScore(currentSolution));
    }

    private void fillTimeTable() {
        Queue<Student> studentQueue = students
                .values()
                .stream()
                .sorted(
                        Comparator.comparingInt(s -> s.getExamIds().size())
                )
                .collect(Collectors.toCollection(PriorityQueue::new));

        //ids of all the exams that have been added to the timeSlots
        Set<Integer> examIds = new LinkedHashSet<>();

        TimeSlot[] initialTimeSlotIterator = timeSlots.keySet().toArray(new TimeSlot[0]);
        Arrays.sort(initialTimeSlotIterator);

        while (!studentQueue.isEmpty()) {
            Student student = studentQueue.poll();
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

        Random random = new Random(2344532L);

        timeSlots = bestSolution.timeSlots;

        int count = 0;
        for (int i = 0; i < numberOfIterations; i++) {
            if (i % 5 == 0) {
                count = performTimeSlotSwitcheroo(random, count);

            } else {
                count = performExamSwitcheroo(random, count);
            }
//            System.out.println(newScore);
        }

        System.out.println("final score: " + bestSolution.getScore());

        System.out.println("**********************************");
        timeSlots.forEach((timeSlot, exams1) -> {
            for (Exam exam : exams1) {
                System.out.println(exam.getID() + " " + timeSlot.getID());
            }
        });

        return 0;
    }

    private int performExamSwitcheroo(Random random, int count) {
        int receiverIndex, supplierIndex, packetIndex;

        int[] indexArr = getRandomIndexes(random);

        supplierIndex = indexArr[0];
        receiverIndex = indexArr[1];

        TimeSlot supplierTimeslot = timeSlotKeys[supplierIndex];
        TimeSlot receiverTimeslot = timeSlotKeys[receiverIndex];

        List<Exam> supplierExams = timeSlots.get(supplierTimeslot);
        List<Exam> receiverExams = timeSlots.get(receiverTimeslot);

        if (supplierExams.isEmpty()) {
            return count;
        }

        packetIndex = random.nextInt(supplierExams.size());
        Exam packet = supplierExams.get(packetIndex);

        supplierExams.remove(packetIndex);
        receiverExams.add(packet);

        List<Integer> receiverExamIds = receiverExams.stream().map(Exam::getID).toList();
        boolean hardConstrainFail = isHardConstrainFail(packet, receiverExamIds);

        if (hardConstrainFail) {
            undoExamSwitch(receiverExams, packet, supplierExams);
            return count;
        }
        count++;

        currentSolution.setTimeSlots(timeSlots);

        double newScore = calculateTotalScore(currentSolution);
        currentSolution.setScore(newScore);

        if (newScore < bestSolution.getScore()) {
            bestSolution = currentSolution.clone();

        } else {
            undoExamSwitch(receiverExams, packet, supplierExams);
        }
        return count;
    }

    private int performTimeSlotSwitcheroo(Random random, int count) {
        int timeslot1Index, timeslot2Index;

        int[] indexArr = getRandomIndexes(random);

        timeslot1Index = indexArr[0];
        timeslot2Index = indexArr[1];

        TimeSlot timeSlot1 = timeSlotKeys[timeslot1Index];
        TimeSlot timeSlot2 = timeSlotKeys[timeslot2Index];

        timeslotSwap(timeSlot1, timeSlot2);

        currentSolution.setTimeSlots(timeSlots);

        double newScore = calculateTotalScore(currentSolution);
        currentSolution.setScore(newScore);

        if (newScore < bestSolution.getScore()) {
            bestSolution = currentSolution.clone();
            count++;
        } else {
            timeslotSwap(timeSlot1, timeSlot2);
        }
        return count;
    }

    private void timeslotSwap(TimeSlot timeSlot1, TimeSlot timeSlot2) {
        List<Exam> examList1 = timeSlots.get(timeSlot1);
        List<Exam> examList2 = timeSlots.get(timeSlot2);
        List<Exam> temp = new ArrayList<>(examList1);
        examList1 = new ArrayList<>(examList2);
        examList2 = new ArrayList<>(temp);
        timeSlots.put(timeSlot1, examList1);
        timeSlots.put(timeSlot2, examList2);
    }

    private int[] getRandomIndexes(Random random) {
        int supplier = random.nextInt(timeSlots.size());
        int receiver = random.nextInt(timeSlots.size());

        while (supplier == receiver) {
            receiver = random.nextInt(timeSlots.size());
        }

        if (supplier > receiver) {
            int temp = receiver;
            receiver = supplier;
            supplier = temp;
        }
        return new int[]{supplier, receiver};
    }

    private boolean isHardConstrainFail(Exam packet, List<Integer> receiverExamIds) {
        boolean hardConstrainFail = false;
        for (int studentId : packet.getSID()) {
            Student stud = students.get(studentId);
            List<Integer> studExamIds = new ArrayList<>(stud.getExamIds());
            studExamIds.retainAll(receiverExamIds);
            int endLen = studExamIds.size();
            if (endLen > 1) {
                hardConstrainFail = true;
                break;
            }
        }
        return hardConstrainFail;
    }

    private static void undoExamSwitch(List<Exam> receiverExams, Exam packet, List<Exam> supplierExams) {
        receiverExams.remove(packet);
        supplierExams.add(packet);
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
                ts.ifPresent(timeSlot -> schedule[timeSlot.getID()] = 1);
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
