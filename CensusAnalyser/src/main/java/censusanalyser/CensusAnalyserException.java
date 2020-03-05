package censusanalyser;

public class CensusAnalyserException extends Exception {

    enum ExceptionType {
        UNABLE_TO_PARSE, NO_CENSUS_DATA, CENSUS_FILE_PROBLEM,INVALID_COUNTRY;
    }

    ExceptionType type;

    public CensusAnalyserException(String message, ExceptionType type) {
        super(message);
        this.type = type;
    }

    public CensusAnalyserException(String message, ExceptionType type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }
}
