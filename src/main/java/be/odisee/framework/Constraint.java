package be.odisee.framework;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Constraint {
    public boolean isHardConstrainFail(Map<Integer, Student> students, Exam packet, List<Integer> receiverExamIds) {
        for (int studentId : packet.getSID()) {
            Student stud = students.get(studentId);
            List<Integer> studExamIds = new ArrayList<>(stud.getExamIds());
            studExamIds.retainAll(receiverExamIds);
            int endLen = studExamIds.size();
            if (endLen > 1) {
                return true;
            }
        }
        return false;
    }
    public boolean isHardConstrainFail(CustomSolution customSolution, List<Exam> examList, Exam exam2) {
        for (Exam exam1 :
                examList) {
            if (customSolution.checkConflictMatrixIsConflict(exam1, exam2)) return true;
        }

        return false;
    }
}
