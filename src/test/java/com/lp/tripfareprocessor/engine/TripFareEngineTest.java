package com.lp.tripfareprocessor.engine;

import com.lp.tripfareprocessor.processinput.ProcessPriceInfo;
import com.lp.tripfareprocessor.processinput.ProcessTapInfo;
import com.lp.tripfareprocessor.util.BaseTest;
import com.lp.tripfareprocessor.writeOutput.WriteTripInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

class TripFareEngineTest extends BaseTest{

    @InjectMocks
    private TripFareEngine tripFareEngine;

    @Mock
    private ProcessPriceInfo processPriceInfo;
    @Mock
    private ProcessTapInfo processTapInfo;
    @Mock
    private WriteTripInfo writeTripInfo;


    @Mock
    private TapBucketEngine tapBucketEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void run() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(processPriceInfo.parsePriceInfo(Mockito.any())).thenReturn(priceInfoList);
        Mockito.when(processTapInfo.parseTapInfo(Mockito.any())).thenReturn(customerTapInfoBucket);
        //Mockito.doNothing().when(writeTripInfo.writeTripInfoToOutput(Mockito.anyList(),Mockito.anyString()));
        Mockito.when(tapBucketEngine.convertToTripInfoForCustomer(Mockito.anyString(),Mockito.anyList(),Mockito.anyList())).thenReturn(tripInfoList);
        tripFareEngine.run();
   }

    @Test
    void runWhenEmpty() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(processPriceInfo.parsePriceInfo(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(processTapInfo.parseTapInfo(Mockito.any())).thenReturn(new ArrayList<>());
        //Mockito.doNothing().when(writeTripInfo.writeTripInfoToOutput(Mockito.anyList(),Mockito.anyString()));
        Mockito.when(tapBucketEngine.convertToTripInfoForCustomer(Mockito.anyString(),Mockito.anyList(),Mockito.anyList())).thenReturn(new ArrayList<>());
        tripFareEngine.run();
    }

    @Test
    void testRun() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(processPriceInfo.parsePriceInfo(Mockito.any())).thenReturn(priceInfoList);
        Mockito.when(processTapInfo.parseTapInfo(Mockito.any())).thenReturn(customerTapInfoBucket);
        //Mockito.doNothing().when(writeTripInfo.writeTripInfoToOutput(Mockito.anyList(),Mockito.anyString()));
        Mockito.when(tapBucketEngine.convertToTripInfoForCustomer(Mockito.anyString(),Mockito.anyList(),Mockito.anyList())).thenReturn(tripInfoList);
        tripFareEngine.run("/Users/kkasiraju/dev/MyMusings/TripFareProcessor/src/main/resources/input/tap-info.csv",
                "/Users/kkasiraju/dev/MyMusings/TripFareProcessor/src/main/resources/input/route-prices.csv",
                "/Users/kkasiraju/dev/MyMusings/TripFareProcessor/src/main/resources/output/trips-customer.csv");
    }
}