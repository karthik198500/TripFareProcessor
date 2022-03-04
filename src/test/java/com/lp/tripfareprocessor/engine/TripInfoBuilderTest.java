package com.lp.tripfareprocessor.engine;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripInfoBuilderTest {

    @InjectMocks
    private TripInfoBuilder tripInfoBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void constructTripInfo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        TripInfo tripInfo = TripInfo.builder()
                .busId("Bus1")
                .status("COMPLETED")
                .started(simpleDateFormat.format(new Date(122,02,01,9,0,0)))
                .finished(simpleDateFormat.format(new Date(122,02,01,9,10,0)))
                .fromStopId("stop no1")
                .toStopId("stop no2")
                .chargeAmount(new BigDecimal(20.01))
                .durationSecs(600)
                .pan("1111111")
                .companyId("google")
                .build();

        TapInfo top =  TapInfo
                .builder()
                .tapType("ON")
                .busId("Bus1")
                .id("1")
                .pan("1111111")
                .companyId("google")
                .stopId("stop no1")
                .tapTimeStamp(new Date(122,02,01,9,0,0))
                .build();

        TapInfo current = TapInfo
                .builder()
                .tapType("OFF")
                .busId("Bus1")
                .id("1")
                .pan("1111111")
                .companyId("google")
                .stopId("stop no2")
                .tapTimeStamp(new Date(122,02,01,9,10,0))
                .build();

        List<PriceInfo> priceInfoList = new ArrayList<>();

        priceInfoList.add(PriceInfo.builder()
                .sourceStationId("stop no1")
                .destinationStationId("stop no2")
                .fair("20.01").build());

        TripInfo tripInfoResult = tripInfoBuilder.constructTripInfo(top,current,priceInfoList);

        Assertions.assertTrue(tripInfoResult.getPan().equalsIgnoreCase(tripInfo.getPan()));
        Assertions.assertTrue(tripInfoResult.getBusId().equalsIgnoreCase(tripInfo.getBusId()));
        Assertions.assertTrue(tripInfoResult.getStatus().equalsIgnoreCase(tripInfo.getStatus()));
        Assertions.assertTrue(tripInfoResult.getChargeAmount().compareTo(tripInfo.getChargeAmount())==0);
        Assertions.assertTrue(tripInfoResult.getCompanyId().equalsIgnoreCase(tripInfo.getCompanyId()));
        Assertions.assertTrue(tripInfoResult.getDurationSecs()==tripInfo.getDurationSecs());
        Assertions.assertTrue(tripInfoResult.getFinished().equalsIgnoreCase(tripInfo.getFinished()));
        Assertions.assertTrue(tripInfoResult.getStarted().equalsIgnoreCase(tripInfo.getStarted()));
        Assertions.assertTrue(tripInfoResult.getFromStopId().equalsIgnoreCase(tripInfo.getFromStopId()));
        Assertions.assertTrue(tripInfoResult.getToStopId().equalsIgnoreCase(tripInfo.getToStopId()));
    }

    @Test
    void constructTripInfoWithError() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        TripInfo tripInfo = TripInfo.builder()
                .busId("Bus1")
                .status("COMPLETED")
                .started(simpleDateFormat.format(new Date(122,02,01,9,0,0)))
                .finished(simpleDateFormat.format(new Date(122,02,01,9,10,0)))
                .fromStopId("stop no1")
                .toStopId("stop no2")
                .chargeAmount(new BigDecimal(20.01))
                .durationSecs(600)
                .pan("1111111")
                .companyId("google")
                .build();

        TapInfo top =  TapInfo
                .builder()
                .tapType("ON")
                .busId("Bus1")
                .id("1")
                .pan("1111111")
                .companyId("google")
                .stopId("stop no1")
                .tapTimeStamp(new Date(122,02,01,9,0,0))
                .build();

        TapInfo current = TapInfo
                .builder()
                .tapType("OFF")
                .busId("Bus1")
                .id("1")
                .pan("1111111")
                .companyId("google")
                .stopId("stop no2 not match")
                .tapTimeStamp(new Date(122,02,01,9,10,0))
                .build();

        List<PriceInfo> priceInfoList = new ArrayList<>();

        priceInfoList.add(PriceInfo.builder()
                .sourceStationId("stop no1")
                .destinationStationId("stop no2")
                .fair("20.01").build());

        try {
            TripInfo tripInfoResult = tripInfoBuilder.constructTripInfo(top,current,priceInfoList);
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
        }
    }

    @Test
    void calculateChargeAmountEmpty() {
        List<PriceInfo> priceInfoList = new ArrayList<>();

        try {
            tripInfoBuilder
                    .calculateChargeAmount(
                            TapInfo.builder().stopId("stop1").build(),
                            TapInfo.builder().stopId("stop4").build(),
                            priceInfoList);
        }catch (Exception e){
            Assertions.assertTrue( e instanceof RuntimeException);
        }
    }

    @Test
    void calculateChargeAmount() {
        List<PriceInfo> priceInfoList = new ArrayList<>();

        priceInfoList.add(PriceInfo.builder().sourceStationId("stop1").destinationStationId("stop2").fair("10.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop1").destinationStationId("stop3").fair("20.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop1").destinationStationId("stop4").fair("40.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop2").destinationStationId("stop3").fair("15.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop2").destinationStationId("stop4").fair("25.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop3").destinationStationId("stop4").fair("10.01").build());


        Assertions.assertTrue(
                new Double(40.01)
                        .compareTo(tripInfoBuilder
                                .calculateChargeAmount(
                                        TapInfo.builder().stopId("stop1").build(),
                                        TapInfo.builder().stopId("stop4").build(),
                                        priceInfoList)
                                .doubleValue())==0);

        Assertions.assertTrue(
                new Double(25.01)
                        .compareTo(tripInfoBuilder
                                .calculateChargeAmount(
                                        TapInfo.builder().stopId("stop2").build(),
                                        TapInfo.builder().stopId("stop4").build(),
                                        priceInfoList)
                                .doubleValue())==0);

        Assertions.assertTrue(
                new Double(40.01)
                        .compareTo(tripInfoBuilder
                                .calculateChargeAmount(
                                        TapInfo.builder().stopId("stop4").build(),
                                        TapInfo.builder().stopId("stop1").build(),
                                        priceInfoList)
                                .doubleValue())==0);

        Assertions.assertTrue(
                new Double(15.01)
                        .compareTo(tripInfoBuilder
                                .calculateChargeAmount(
                                        TapInfo.builder().stopId("stop3").build(),
                                        TapInfo.builder().stopId("stop2").build(),
                                        priceInfoList)
                                .doubleValue())==0);

        Assertions.assertTrue(
                new Double(0.0)
                        .compareTo(tripInfoBuilder
                                .calculateChargeAmount(
                                        TapInfo.builder().stopId("stop1").build(),
                                        TapInfo.builder().stopId("stop1").build(),
                                        priceInfoList)
                                .doubleValue())==0);

    }

    @Test
    void findTheMaximumFareWhenCustomerForgotToTAPOFF() {

        List<PriceInfo> priceInfoList = new ArrayList<>();

        priceInfoList.add(PriceInfo.builder().sourceStationId("stop1").destinationStationId("stop2").fair("10.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop1").destinationStationId("stop3").fair("20.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop1").destinationStationId("stop4").fair("40.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop2").destinationStationId("stop3").fair("15.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop2").destinationStationId("stop4").fair("25.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop3").destinationStationId("stop4").fair("10.01").build());

        Assertions.assertTrue(new Double(40.01).compareTo(tripInfoBuilder.findTheMaximumFareWhenCustomerForgotToTAPOFF("stop1",priceInfoList).doubleValue())==0);
        Assertions.assertTrue(new Double(25.01).compareTo(tripInfoBuilder.findTheMaximumFareWhenCustomerForgotToTAPOFF("stop2",priceInfoList).doubleValue())==0);
        Assertions.assertTrue(new Double(20.01).compareTo(tripInfoBuilder.findTheMaximumFareWhenCustomerForgotToTAPOFF("stop3",priceInfoList).doubleValue())==0);
        Assertions.assertTrue(new Double(40.01).compareTo(tripInfoBuilder.findTheMaximumFareWhenCustomerForgotToTAPOFF("stop4",priceInfoList).doubleValue())==0);

    }

    @Test
    void findTheMaximumFareWhenCustomerForgotToTAPOFFWhenListIsEmpty() {
        List<PriceInfo> priceInfoList = new ArrayList<>();
        Assertions.assertTrue(new Double(0.0).compareTo(tripInfoBuilder.findTheMaximumFareWhenCustomerForgotToTAPOFF("stop1",priceInfoList).doubleValue())==0);
    }

    @Test
    void findTheMaximumFareWhenCustomerForgotToTAPOFFWhenStopIdIsEmpty() {
        List<PriceInfo> priceInfoList = new ArrayList<>();
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop1").destinationStationId("stop2").fair("10.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop1").destinationStationId("stop3").fair("20.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop1").destinationStationId("stop4").fair("40.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop2").destinationStationId("stop3").fair("15.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop2").destinationStationId("stop4").fair("25.01").build());
        priceInfoList.add(PriceInfo.builder().sourceStationId("stop3").destinationStationId("stop4").fair("10.01").build());

        Assertions.assertTrue(new Double(0.0).compareTo(tripInfoBuilder.findTheMaximumFareWhenCustomerForgotToTAPOFF(null,priceInfoList).doubleValue())==0);
    }
}