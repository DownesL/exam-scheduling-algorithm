package be.odisee;

import be.odisee.data.DataReader;
import be.odisee.framework.SearchAlgorithm;
import be.odisee.lateAcceptance.LateAcceptanceSearch;
import be.odisee.localSearch.CustomSearch;

public class Main {
    private static DataReader dataReader;

    public static void main(String[] args) {
        dataReader = new DataReader("benchmarks/sta-f-83.crs","benchmarks/sta-f-83.stu");
        SearchAlgorithm customSearch = new CustomSearch(dataReader);
//        SearchAlgorithm lateAcceptanceSearch = new LateAcceptanceSearch(dataReader);
        customSearch.execute(10000);
//        lateAcceptanceSearch.execute(10000);
    }
}
