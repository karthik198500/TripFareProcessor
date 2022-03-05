package com.lp.tripfareprocessor.processinput;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.util.opencsv.CustomMappingStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProcessTapInfoTest {
    @InjectMocks
    private ProcessTapInfo processTapInfo;

    @Mock
    private CustomMappingStrategy<PriceInfo> customMappingStrategy;

    @TempDir
    File anotherTempDir;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void parseTapInfo() {
        File tapsFile = createTapsFile();
        processTapInfo.init();
        List<TapInfo> tapInfoList = processTapInfo.parseTapInfo(tapsFile.getPath());

        Assertions.assertTrue(tapInfoList.size() ==2);
        Assertions.assertTrue(tapInfoList.get(0).getTapType().equalsIgnoreCase("ON"));
        Assertions.assertTrue(tapInfoList.get(0).getPan().equalsIgnoreCase("5500005555555559"));
        Assertions.assertTrue(tapInfoList.get(0).getCompanyId().equalsIgnoreCase("Company1"));

    }

    private File createTapsFile() {
        File nemFile = new File(anotherTempDir, "tap-info.csv");
        List<String> lines = Arrays.asList("ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN\n" +
                "1, 22-01-2018 13:00:00, ON, Stop1, Company1, Bus37, 5500005555555559\n" +
                "2, 22-01-2018 13:05:00, OFF, Stop3, Company1, Bus37, 5500005555555559");
        try {
            Files.write(nemFile.toPath(), lines);
        } catch (IOException e) {
        }
        return nemFile;
    }

    @Test
    void parsePriceInfoWithEmptyFile() {
        File tapFile = createEmptyFile();
        processTapInfo.init();
        List<TapInfo> priceInfoList = processTapInfo.parseTapInfo(tapFile.getPath());
        Assertions.assertTrue(priceInfoList.size() ==0);
    }

    private File createEmptyFile() {
        File nemFile = new File(anotherTempDir, "tap-info.csv");
        try {
            Files.write(nemFile.toPath(), Arrays.asList(""));
        } catch (IOException e) {
        }
        return nemFile;
    }
}