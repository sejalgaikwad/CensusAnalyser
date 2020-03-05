package censusanalyser;

import com.google.gson.Gson;

import java.lang.IllegalStateException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CensusAnalyser {
    public enum Country{India, US};
    List<CensusDTO> collect = null;
    Map<String, CensusDTO> censusCSVMap = null;
    //Map<String, CensusDTO> censusStateMap = null;

    public CensusAnalyser() {
        this.censusCSVMap = new TreeMap<String, CensusDTO>();
    }

    public int loadCensusData(Country country, String... csvFilePath) throws CensusAnalyserException {
        censusCSVMap = new CensusLoader().loadCensusData( country,csvFilePath);
        return censusCSVMap.size();
    }

    public String getStateWiseSortedData() throws CensusAnalyserException {
        collect= censusCSVMap.values().stream().collect(Collectors.toList());
        if (collect == null || collect.size() == 0) {
            throw new CensusAnalyserException("NO CENSUS DATA", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<CensusDTO> censusComparator = Comparator.comparing(census -> census.state);
        this.sort(censusComparator);
        String sortedStateCensus = new Gson().toJson(collect);
        return sortedStateCensus;
    }

    private void sort(Comparator<CensusDTO> censusComparator) {
        for (int i = 0; i < collect.size() - 1; i++) {
            for (int j = 0; j < collect.size() - 1 - i; j++) {
                CensusDTO census1 = collect.get(j);
                CensusDTO census2 = collect.get(j + 1);
                if (censusComparator.compare(census1, census2) > 0) {
                    collect.set(j, census2);
                    collect.set(j + 1, census1);
                }
            }
        }
    }

    public String getPopulateState() {
        collect= censusCSVMap.values().stream().collect(Collectors.toList());
        Comparator<CensusDTO> censusComparator = Comparator.comparing(census -> census.population);
        this.sort(censusComparator);
        String sortedStateCensus = new Gson().toJson(collect);
        return sortedStateCensus;
    }
}


//    private <E> int getCount(Iterator<E> iterator) {
//        Iterable<E> csvIterable = () -> iterator;
//        int namOfEateries = (int) StreamSupport.stream(csvIterable.spliterator(), false).count();
//        return namOfEateries;
//    }