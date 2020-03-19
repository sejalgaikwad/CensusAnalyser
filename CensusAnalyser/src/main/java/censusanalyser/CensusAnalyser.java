package censusanalyser;

import com.google.gson.Gson;

import java.util.*;
import java.util.stream.Collectors;

public class CensusAnalyser {
    public enum Country {INDIA, US}

    Map<SortField, Comparator<CensusDTO>> sortMap;
    Map<String, CensusDTO> censusCSVMap = new HashMap<>();
    List<CensusDTO> collect;


    public CensusAnalyser() {
        this.sortMap = new HashMap<>();
        this.sortMap.put(SortField.STATE, Comparator.comparing(census -> census.state));
        this.sortMap.put(SortField.POPULATION, Comparator.comparing(census -> census.population));
        this.sortMap.put(SortField.POPULATION_DENSITY, Comparator.comparing(census -> census.populationDensity));
        this.sortMap.put(SortField.TOTAL_AREA, Comparator.comparing(census -> census.totalArea));
        this.sortMap.put(SortField.STATE_ID, Comparator.comparing(census -> census.stateCode));
    }

    public int loadCensusData(Country country, String... csvFilePath) {
        censusCSVMap=new CensusAdaptorFactory().getCensusAdaptor(country, csvFilePath);
        return censusCSVMap.size();
    }

    public String getStateWiseSortedData(SortField sortField) {
        if (censusCSVMap == null || censusCSVMap.size() == 0) {
            throw new CensusAnalyserException("No Census Data",
                    CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        collect = censusCSVMap.values().stream().collect(Collectors.toList());
        this.sort(this.sortMap.get(sortField).reversed());
        String sortedStateCensusJson = new Gson().toJson(collect);
        return sortedStateCensusJson;

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
}
