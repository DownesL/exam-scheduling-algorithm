package be.odisee.framework;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class SearchHelper {
    Constraint constraint;
    RandomGenerator randomGenerator;

    public SearchHelper(RandomGenerator randomGenerator) {
        constraint = new Constraint();
        this.randomGenerator = randomGenerator;
    }

    public static void flattenSchedule(
            CustomSolution solution
    ) {
        Map<TimeSlot, List<Exam>> timeSlots = new HashMap<>(solution.getTimeSlots());
        int zeroCount = (int) timeSlots.values().stream().filter(List::isEmpty).count();
        List<Integer> idsByLength = timeSlots
                .keySet()
                .stream()
                .sorted(Comparator.comparingInt(timeSlot -> timeSlots.get(timeSlot).size()))
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
        solution.setTimeSlots(copyMap);
    }

    public static Optional<TimeSlot> getExamIndex(CustomSolution solution, int examId) {
        Map<TimeSlot, List<Exam>> timeSlots = solution.getTimeSlots();
        Set<TimeSlot> keyset= timeSlots.keySet();
        for (TimeSlot timeSlot : keyset) {
            Optional<Exam> exam = timeSlots
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

    public void fillTimeTable(CustomSolution solution) {
        Queue<Student> studentQueue = solution.students
                .values()
                .stream()
                .sorted(
                        Comparator.comparingInt(s -> s.getExamIds().size())
                )
                .collect(Collectors.toCollection(PriorityQueue::new));

        //ids of all the exams that have been added to the timeSlots
        Set<Integer> examIds = new LinkedHashSet<>();

        TimeSlot[] initialTimeSlotIterator = solution.timeSlots.keySet().toArray(new TimeSlot[0]);
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
                //extra constraint
                while(solution.timeSlots.get(timeSlot).size()+1 > 15) {
                    timeSlot =  timeSlotIterator.next();
                }
                solution.timeSlots.get(timeSlot).add(solution.exams.get(i));
                timeSlot = timeSlotIterator.next();
            }
            examIds.addAll(sEids);
        }
    }


    public boolean performTimeSlotSwitcheroo(CustomSolution currentSolution) {
        int timeslot1Index, timeslot2Index;
        Map<TimeSlot, List<Exam>> timeSlots = currentSolution.getTimeSlots();

        TimeSlot[] timeSlotKeys = timeSlots
                .keySet()
                .toArray(TimeSlot[]::new);

        int size = timeSlotKeys.length;

        int[] indexArr = randomGenerator.getRandomIndexes(size);

        timeslot1Index = indexArr[0];
        timeslot2Index = indexArr[1];

        TimeSlot timeSlot1 = timeSlotKeys[timeslot1Index];
        TimeSlot timeSlot2 = timeSlotKeys[timeslot2Index];

        Move move = new TimeslotMove(timeSlots, timeSlot1, timeSlot2);
        move.doMove();
        currentSolution.setLastMove(move);
        return true;
    }

    public boolean performExamSwitcheroo(CustomSolution currentSolution) {
        int receiverIndex, supplierIndex, packetIndex;

        Map<TimeSlot, List<Exam>> timeSlots = currentSolution.getTimeSlots();

        TimeSlot[] timeSlotKeys = timeSlots
                .keySet()
                .toArray(TimeSlot[]::new);

        int size = timeSlotKeys.length;
        Map<Integer, Student> students = currentSolution.getStudents();

        int[] indexArr = randomGenerator.getRandomIndexes(size);

        supplierIndex = indexArr[0];
        receiverIndex = indexArr[1];

        TimeSlot supplierTimeslot = timeSlotKeys[supplierIndex];
        TimeSlot receiverTimeslot = timeSlotKeys[receiverIndex];


        List<Exam> supplierExams = timeSlots.get(supplierTimeslot);
        List<Exam> receiverExams = timeSlots.get(receiverTimeslot);

        if (supplierExams.isEmpty()) {
            return false;
        }

        packetIndex = randomGenerator.next(supplierExams.size());
        Exam packet = supplierExams.get(packetIndex);

        ExamMove move = new ExamMove(supplierExams, packet, receiverExams);

        move.doMove();
        currentSolution.setLastMove(move);

        List<Integer> receiverExamIds = receiverExams.stream().map(Exam::getID).toList();

        boolean hardConstrainFail = constraint.isHardConstrainFail(students, packet, receiverExamIds);

        if (hardConstrainFail) {
            move.undoMove();
            return false;
        }
        return true;

    }


}
