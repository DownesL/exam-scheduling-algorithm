package be.odisee.framework;

import be.odisee.domain.Exam;
import be.odisee.domain.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Constraint {
    public boolean isHardConstrainFail(CustomSolution customSolution, List<Exam> examList, Exam exam2) {
        for (Exam exam1 :
                examList) {
            if (customSolution.checkConflictMatrixIsConflict(exam1, exam2))
            {
                return true;
            }
        }

        return false;
    }
}
