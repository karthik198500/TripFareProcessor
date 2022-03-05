package com.lp.tripfareprocessor.processinput;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProcessTapInfoTest {

    @TempDir
    File anotherTempDir;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void parseTapInfo() {
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
}