package be.odisee.framework;

public interface SearchAlgorithm {
    Solution getBestSolution();

    Solution getCurrentSolution();


    Solution execute(int numberOfIterations) ;

    public void checkForImprovement(Move move);
}
