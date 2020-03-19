package censusanalyser;

import java.util.Map;

public class USCensusAdaptor extends CensusAdaptor {
    public <E> Map<String, CensusDTO> loadCensusData(String... csvFilePath) {
        Map<String, CensusDTO> censusStateMap = super.loadCensusData(USCensusCSV.class, csvFilePath[0]);
        return  censusStateMap;
    }
}
