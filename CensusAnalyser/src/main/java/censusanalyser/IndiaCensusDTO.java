package censusanalyser;

public class IndiaCensusDTO {
    public  String state;
    public  int areaInSqKm;
    public  int densityPerSqKm;
    public  int population;
    public String stateCode;
    public IndiaCensusDTO(IndiaCensusCSV next) {
        state=next.state;
        areaInSqKm =next.areaInSqKm;
        densityPerSqKm=next.densityPerSqKm;
        population=next.population;

    }
}
