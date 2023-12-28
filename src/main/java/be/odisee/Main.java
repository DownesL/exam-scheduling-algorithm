package be.odisee;

import be.odisee.data.DataReader;
import be.odisee.framework.SearchAlgorithm;
import be.odisee.searchImplementation.CustomSearch;

public class Main {
    private static DataReader dataReader;

    public static void main(String[] args) {
        dataReader = new DataReader("benchmarks/sta-f-83.crs","benchmarks/sta-f-83.stu");
        SearchAlgorithm searchAlgorithm = new CustomSearch(dataReader);
        searchAlgorithm.execute(100000);
        System.out.println(dataReader.getExams());
    }
}
