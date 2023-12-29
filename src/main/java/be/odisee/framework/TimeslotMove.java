package be.odisee.framework;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;

import java.util.*;
import java.util.stream.Collectors;

public class TimeslotMove implements Move{
    Map<TimeSlot, List<Exam>> timeSlots;
    TimeSlot timeSlot1;
    TimeSlot timeSlot2;

    public TimeslotMove(Map<TimeSlot, List<Exam>> timeSlots, TimeSlot timeSlot1, TimeSlot timeSlot2) {
        this.timeSlots = timeSlots;
        this.timeSlot1 = timeSlot1;
        this.timeSlot2 = timeSlot2;
    }

    @Override
    public void doMove() {
        timeslotSwap(timeSlots, timeSlot1, timeSlot2);
    }

    @Override
    public void undoMove() {
        timeslotSwap(timeSlots, timeSlot1, timeSlot2);
    }

    @Override
    public Set<Integer> affectedStudents() {
        List<Exam> examList = new ArrayList<>(timeSlots.get(timeSlot1));
        examList.addAll(timeSlots.get(timeSlot2));
        return examList.stream()
                .map(Exam::getSID)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    private void timeslotSwap(Map<TimeSlot, List<Exam>> timeSlots, TimeSlot timeSlot1, TimeSlot timeSlot2) {
        List<Exam> examList1 = timeSlots.get(timeSlot1);
        List<Exam> examList2 = timeSlots.get(timeSlot2);
        List<Exam> temp = new ArrayList<>(examList1);
        examList1 = new ArrayList<>(examList2);
        examList2 = new ArrayList<>(temp);
        timeSlots.put(timeSlot1, examList1);
        timeSlots.put(timeSlot2, examList2);
    }
}
