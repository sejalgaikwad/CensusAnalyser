package censusanalyser;

public class IndiaCensusDTO {
    public String state;
    public double totalArea;
    public double populationDensity;
    public int population;
    public String stateCode;

    public IndiaCensusDTO(IndiaCensusCSV next) {
        state = next.state;
        totalArea = next.areaInSqKm;
        populationDensity = next.densityPerSqKm;
        population = next.population;
    }

    public IndiaCensusDTO(USCensusCSV usCensusCSV) {
        state = usCensusCSV.state;
        stateCode = usCensusCSV.stateID;
        population = usCensusCSV.population;
        totalArea = usCensusCSV.totalArea;
        populationDensity = usCensusCSV.populationDensity;
    }
}
