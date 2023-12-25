package be.odisee.framework;

public interface SearchAlgorithm {
    Solution getBestSolution();

    Solution getCurrentSolution();


    int execute(int numberOfIterations) ;
}
