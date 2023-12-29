package be.odisee.framework;

public interface SearchAlgorithm {
    Solution getBestSolution();

    Solution getCurrentSolution();


    int execute(int numberOfIterations) ;

    public void checkForImprovement(Move move);
}
