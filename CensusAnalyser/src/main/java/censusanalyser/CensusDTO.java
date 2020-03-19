package censusanalyser;

public class CensusDTO {
    public String state;
    public double totalArea;
    public double populationDensity;
    public int population;
    public String stateCode;

    public CensusDTO(IndiaCensusCSV next) {
        state = next.state;
        totalArea = next.areaInSqKm;
        populationDensity = next.densityPerSqKm;
        population = next.population;
    }

    public CensusDTO(USCensusCSV usCensusCSV) {
        state = usCensusCSV.state;
        stateCode = usCensusCSV.stateID;
        population = usCensusCSV.population;
        totalArea = usCensusCSV.totalArea;
        populationDensity = usCensusCSV.populationDensity;
    }


}
