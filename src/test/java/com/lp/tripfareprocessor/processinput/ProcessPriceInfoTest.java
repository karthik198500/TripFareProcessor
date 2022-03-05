package com.lp.tripfareprocessor.processinput;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.util.opencsv.CustomMappingStrategy;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProcessPriceInfoTest {

    @InjectMocks
    private ProcessPriceInfo processPriceInfo;


    @Mock
    private CustomMappingStrategy<PriceInfo> customMappingStrategy;


    @TempDir
    File anotherTempDir;

    @Mock
    PriceInfo priceInfo;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void parsePriceInfo() {
        PriceInfo priceInfo = PriceInfo.builder()
                .sourceStationId("stop no1")
                .destinationStationId("stop no2")
                .fair("20.01").build();
        File priceFile = createPricesFile();
        processPriceInfo.init();
    /*    try {
            Mockito.when(customMappingStrategy.populateNewBean(Mockito.any())).thenReturn(priceInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        processPriceInfo.parsePriceInfo(priceFile.getPath());
    }


    private File createPricesFile() {
        File nemFile = new File(anotherTempDir, "route-prices.csv");
        List<String> lines = Arrays.asList("source,destination,fair\n" +
                "1,2,3.25\n" +
                "2,3,5.5\n" +
                "1,3,7.3");
        try {
            Files.write(nemFile.toPath(), lines);
        } catch (IOException e) {

        }
        return nemFile;
    }


}