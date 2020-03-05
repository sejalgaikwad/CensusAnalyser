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
    List<IndiaCensusDTO> collect = null;
    Map<String, IndiaCensusDTO> censusCSVMap = null;

    public CensusAnalyser() {
        this.censusCSVMap = new TreeMap<String, IndiaCensusDTO>();
    }

    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaCensusCSV> csvFileIterator = csvBuilder.getCSVFileIterator(reader, IndiaCensusCSV.class);
            while (csvFileIterator.hasNext()) {
                IndiaCensusCSV censusCSVIterator = csvFileIterator.next();
                this.censusCSVMap.put(censusCSVIterator.state, new IndiaCensusDTO(censusCSVIterator));
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
            while(censusCSVIterator.hasNext()){
                IndiaStateCodeCSV indiaStateCode =censusCSVIterator.next();
                IndiaCensusDTO indiaCensusDTO =censusCSVMap.get(indiaStateCode.state);
                if(indiaCensusDTO == null){
                    continue;
                }
            }
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
        Comparator<IndiaCensusDTO> censusComparator = Comparator.comparing(census -> census.state);
        this.sort(censusComparator);
        String sortedStateCensus = new Gson().toJson(collect);
        return sortedStateCensus;
    }

    private void sort(Comparator<IndiaCensusDTO> censusComparator) {
        for (int i = 0; i < collect.size() - 1; i++) {
            for (int j = 0; j < collect.size() - 1 - i; j++) {
                IndiaCensusDTO census1 = collect.get(j);
                IndiaCensusDTO census2 = collect.get(j + 1);
                if (censusComparator.compare(census1, census2) > 0) {
                    collect.set(j, census2);
                    collect.set(j + 1, census1);
                }
            }
        }
    }

    public String getPopulateState() {
        Comparator<IndiaCensusDTO> censusComparator = Comparator.comparing(census -> census.population);
        this.sort(censusComparator);
        String sortedStateCensus = new Gson().toJson(collect);
        return sortedStateCensus;
    }

    public String getPopulateDensity() {
        Comparator<IndiaCensusDTO> censusComparator = Comparator.comparing(census -> census.densityPerSqKm);
        this.sort(censusComparator);
        String sortedStateCensus = new Gson().toJson(collect);
        return sortedStateCensus;
    }
}
