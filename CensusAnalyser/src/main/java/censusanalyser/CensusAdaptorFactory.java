package censusanalyser;

import java.util.Map;

public class CensusAdaptorFactory {
    public static Map<String ,CensusDTO> getCensusAdaptor(CensusAnalyser.Country country, String... csvFilePath) throws CensusAnalyserException {
        if(country.equals(CensusAnalyser.Country.INDIA)){
            return new IndianCensusAdaptor().loadCensusData(csvFilePath);
        }
        if(country.equals(CensusAnalyser.Country.US)){
            return new USCensusAdaptor().loadCensusData(csvFilePath);
        }
        throw new CensusAnalyserException("Incorrect Country", CensusAnalyserException.ExceptionType.INVALID_COUNTRY);
    }
}



