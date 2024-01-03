package be.odisee;

import be.odisee.data.DataReader;
import be.odisee.framework.RandomGenerator;
import be.odisee.framework.SearchAlgorithm;
import be.odisee.framework.Solution;
import be.odisee.lateAcceptance.LateAcceptanceSearch;
import be.odisee.localSearch.CustomSearch;

public class Main {
    private static DataReader dataReader;

    public static void main(String[] args) {
        dataReader = new DataReader("benchmarks/sta-f-83.crs", "benchmarks/sta-f-83.stu");
        Solution bestSolution = null;

        int seed = 34243;

        SearchAlgorithm lateAcceptanceSearch = new LateAcceptanceSearch(dataReader, new RandomGenerator(seed));
        bestSolution = lateAcceptanceSearch.execute(15000);

        SearchAlgorithm customSearch = new CustomSearch(dataReader, new RandomGenerator(seed));
        Solution solution = customSearch.execute(10000);
        System.out.println(solution.getTotalCost());

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Best score: " + bestSolution.getTotalCost());
        solution.logForBenchmark();
    }
}
