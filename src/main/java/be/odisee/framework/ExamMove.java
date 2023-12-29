package be.odisee.framework;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ExamMove implements Move {
    Exam packet;
    List<Exam> supplierExams;
    List<Exam> receiverExams;

    public ExamMove(List<Exam> supplierExams, Exam packet, List<Exam> receiverExams) {
        this.packet = packet;
        this.supplierExams = supplierExams;
        this.receiverExams = receiverExams;
    }

    @Override
    public void doMove() {
        supplierExams.remove(packet);
        receiverExams.add(packet);
    }

    @Override
    public void undoMove() {
        receiverExams.remove(packet);
        supplierExams.add(packet);
    }

    @Override
    public Set<Integer> affectedStudents() {
        return new HashSet<>(packet.getSID());
    }
}
