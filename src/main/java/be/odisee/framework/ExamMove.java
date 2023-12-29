package be.odisee.framework;

import be.odisee.domain.Exam;

import java.util.List;

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
}
