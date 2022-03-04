package com.lp.tripfareprocessor.engine;

import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import com.lp.tripfareprocessor.util.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class TapBucketEngineTest extends BaseTest {


    @InjectMocks
    private TapBucketEngine tapBucketEngine;

    @Mock
    private TripInfoBuilder tripInfoBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        //customerTapBucketEngine = new CustomerTapBucketEngine(tripInfoBuilder);
    }

    @Test
    void convertToTripInfoForCustomer() {

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

        Mockito.when(tripInfoBuilder.constructTripInfo(Mockito.any(TapInfo.class),
                Mockito.any(TapInfo.class),Mockito.anyList())).thenReturn(tripInfo);

        /*
        priceInfoList.add(PriceInfo.builder()
                .sourceStationId("stop no3")
                .destinationStationId("stop no4")
                .fair("50.01").build());

       customerTapInfoBucket.add(TapInfo
                .builder()
                .tapType("ON")
                .busId("Bus1")
                .id("1")
                .pan("222222")
                .companyId("Facebook")
                .stopId("stop no4 ")
                .tapTimeStamp(new Date(2022,02,01,10,0,0))
                .build());

        customerTapInfoBucket.add(TapInfo
                .builder()
                .tapType("ON")
                .busId("Bus1")
                .id("1")
                .pan("222222")
                .companyId("Facebook")
                .stopId("stop no5 ")
                .tapTimeStamp(new Date(2022,02,01,10,30,0))
                .build());*/

        List<TripInfo> tripInfoArrayList = tapBucketEngine.convertToTripInfoForCustomer("1111111",customerTapInfoBucket,priceInfoList);
        Assertions.assertTrue(tripInfoArrayList.size()==1);
        Assertions.assertSame(tripInfoArrayList.get(0).getPan(),"1111111");
        Assertions.assertSame(tripInfoArrayList.get(0).getStatus(),"COMPLETED");
    }

    @Test
    public void convertToTripInfoForCustomerEmpty() {


        List<TripInfo> tripInfoArrayList = tapBucketEngine.
                convertToTripInfoForCustomer("1111111",new ArrayList<>(),new ArrayList<>());
        Assertions.assertTrue(tripInfoArrayList.size()==0);

    }

}