package be.odisee.framework;

public interface Solution extends Cloneable {
    double getObjectiveValue();

    void setScore(double value);



    Object clone();
}
