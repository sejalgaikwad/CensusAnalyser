package censusanalyser;

import com.google.gson.Gson;

import java.lang.IllegalStateException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class CensusAnalyser {
    List<IndiaCensusDTO> censusCSVList = null;

    public CensusAnalyser() {
        this.censusCSVList = new ArrayList<IndiaCensusDTO>();
    }

    public int loadIndiaCensusData(String csvFilePath) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaCensusCSV> csvFileIterator =  csvBuilder.getCSVFileIterator(reader, IndiaCensusCSV.class);
            while (csvFileIterator.hasNext()) {
                this.censusCSVList.add(new IndiaCensusDTO(csvFileIterator.next()));
            }
            return censusCSVList.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (IllegalStateException e) {
            throw new CensusAnalyserException(e.getMessage(), CensusAnalyserException.ExceptionType.UNABLE_TO_PARSE);
        }
    }

    public int loadIndiaStateCode(String stateCode) throws CensusAnalyserException {
        try (Reader reader = Files.newBufferedReader(Paths.get(stateCode));) {
            Iterator<IndiaStateCodeCSV> censusCSVIterator = new OpenCSVBuilder().getCSVFileIterator(reader, IndiaStateCodeCSV.class);
            Iterable<IndiaStateCodeCSV> csvIterable = () -> censusCSVIterator;
            int namOfEateries = (int) StreamSupport.stream(csvIterable.spliterator(), false).count();
            return namOfEateries;
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
        if (censusCSVList == null || censusCSVList.size() == 0) {
            throw new CensusAnalyserException("NO CENSUS DATA", CensusAnalyserException.ExceptionType.NO_CENSUS_DATA);
        }
        Comparator<IndiaCensusDTO> censusComparator = Comparator.comparing(census -> census.state);
        this.sort(censusCSVList, censusComparator);
        String sortedStateCensus = new Gson().toJson(censusCSVList);
        return sortedStateCensus;
    }

    private void sort(List<IndiaCensusDTO> censusCSVList, Comparator<IndiaCensusDTO> censusComparator) {
        for (int i = 0; i < censusCSVList.size() - 1; i++) {
            for (int j = 0; j < censusCSVList.size() - 1 - i; j++) {
                IndiaCensusDTO census1 = censusCSVList.get(j);
                IndiaCensusDTO census2 = censusCSVList.get(j + 1);
                if (censusComparator.compare(census1, census2) > 0) {
                    censusCSVList.set(j, census2);
                    censusCSVList.set(j + 1, census1);
                }
            }
        }
    }

    public String getPopulateState() {
        Comparator<IndiaCensusDTO> censusComparator = Comparator.comparing(census -> census.population);
        this.sort(censusCSVList, censusComparator);
        String sortedStateCensus = new Gson().toJson(censusCSVList);
        return sortedStateCensus;
    }
}
