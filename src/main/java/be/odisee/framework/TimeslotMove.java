package be.odisee.framework;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;
import be.odisee.domain.TimeSlot;

import java.util.*;
import java.util.stream.Collectors;

public class TimeslotMove implements Move{
    Map<TimeSlot, List<Exam>> oldTimeSlots;
    Map<TimeSlot, List<Exam>> newTimeSlots;
    int[] newIndexArr;

    public TimeslotMove(Map<TimeSlot, List<Exam>> timeSlots, int[] newIndexArr) {
        this.oldTimeSlots = timeSlots;
        this.newIndexArr = newIndexArr;
    }


    @Override
    public void doMove() {
        Map<TimeSlot, List<Exam>> newTimeslots = new HashMap<>();

        for (int i = 0; i < newIndexArr.length; i++) {
            int index = newIndexArr[i];
            TimeSlot ts = new TimeSlot(i);
            TimeSlot newTs = new TimeSlot(index);
            newTimeslots.put(ts, new ArrayList<>(oldTimeSlots.get(newTs)));
        }
        this.newTimeSlots = newTimeslots;
    }

    @Override
    public void undoMove() {
        newTimeSlots = oldTimeSlots;
    }

    @Override
    public Set<Integer> affectedStudents() {
        return null;
    }

}
