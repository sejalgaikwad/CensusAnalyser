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
    List<CensusDTO> collect = null;
    Map<String, CensusDTO> censusCSVMap = null;

    public CensusAnalyser() {
        this.censusCSVMap = new TreeMap<String, CensusDTO>();
    }

    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        return loadCensusData(csvFilePath, IndiaCensusCSV.class);
    }

    public int loadUSCensusData(String csvFilePath) throws CensusAnalyserException {
        return loadCensusData(csvFilePath, USCensusCSV.class);
    }

    private <E> int loadCensusData(String csvFilePath, Class<E> censusCSVClass) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<E> csvFileIterator = csvBuilder.getCSVFileIterator(reader, censusCSVClass);
            Iterable<E> csvIterable = () -> csvFileIterator;
            if (censusCSVClass.getName().equals("censusanalyser.IndiaCensusCSV")) {
                StreamSupport.stream(csvIterable.spliterator(), false)
                        .map(IndiaCensusCSV.class::cast)
                        .forEach(csvState -> censusCSVMap.put(csvState.state, new CensusDTO(csvState)));
            }
            else if (censusCSVClass.getName().equals("censusanalyser.USCensusCSV")) {
                StreamSupport.stream(csvIterable.spliterator(), false)
                        .map(USCensusCSV.class::cast)
                        .forEach(csvState -> censusCSVMap.put(csvState.state, new CensusDTO(csvState)));
            }
            collect = censusCSVMap.values().stream().collect(Collectors.toList());
            return censusCSVMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (IllegalStateException e) {
            throw new CensusAnalyserException(e.getMessage(), CensusAnalyserException.ExceptionType.UNABLE_TO_PARSE);
        }
    }

    public int loadIndiaStateCode(String csvFilePath) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {

            Iterator<IndiaStateCodeCSV> censusCSVIterator = new OpenCSVBuilder().getCSVFileIterator(reader, IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> csvIterable = () -> censusCSVIterator;
            StreamSupport.stream(csvIterable.spliterator(), false)
                    .filter(csvState -> censusCSVMap.get(csvState.state) != null)
                    .forEach(csvState -> censusCSVMap.get(csvState.state).stateCode = csvState.stateCode);
            return censusCSVMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (IllegalStateException e) {
            throw new CensusAnalyserException(e.getMessage(), CensusAnalyserException.ExceptionType.UNABLE_TO_PARSE);
        }
    }

    private <E> int getCount(Iterator<E> iterator) {
        Iterable<E> csvIterable = () -> iterator;
        int namOfEateries = (int) StreamSupport.stream(csvIterable.spliterator(), false).count();
        return namOfEateries;
    }


    public String getStateWiseSortedData() throws CensusAnalyserException {
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
        Comparator<CensusDTO> censusComparator = Comparator.comparing(census -> census.population);
        this.sort(censusComparator);
        String sortedStateCensus = new Gson().toJson(collect);
        return sortedStateCensus;
    }

}
