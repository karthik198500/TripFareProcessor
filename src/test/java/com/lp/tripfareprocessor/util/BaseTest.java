package com.lp.tripfareprocessor.util;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    public static List<TapInfo> customerTapInfoBucket = new ArrayList<>();
    public static List<PriceInfo> priceInfoList = new ArrayList<>();
    public static List<TripInfo> tripInfoList = new ArrayList<>();

    @BeforeAll
    public static void init(){

        customerTapInfoBucket.add(TapInfo
                .builder()
                .tapType("ON")
                .busId("Bus1")
                .id("1")
                .pan("1111111")
                .companyId("google")
                .stopId("stop no1")
                .tapTimeStamp(new Date(122,02,01,9,0,0))
                .build());

        customerTapInfoBucket.add(TapInfo
                .builder()
                .tapType("OFF")
                .busId("Bus1")
                .id("1")
                .pan("1111111")
                .companyId("google")
                .stopId("stop no2")
                .tapTimeStamp(new Date(122,02,01,9,10,0))
                .build());

        priceInfoList.add(PriceInfo.builder()
                .sourceStationId("stop no1")
                .destinationStationId("stop no2")
                .fair("20.01").build());

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

        tripInfoList.add(tripInfo);

    }
}
