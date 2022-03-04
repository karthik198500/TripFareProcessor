package com.lp.tripfareprocessor.engine;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import com.lp.tripfareprocessor.processinput.ProcessPriceInfo;
import com.lp.tripfareprocessor.processinput.ProcessTapInfo;

import com.lp.tripfareprocessor.writeOutput.WriteTripInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
public class TripFareEngine {

    private final ProcessPriceInfo processPriceInfo;
    private final ProcessTapInfo processTapInfo;
    private final WriteTripInfo writeTripInfo;
    private final CustomerTapBucketEngine customerTapBucketEngine;

    public TripFareEngine(ProcessPriceInfo processPriceInfo, ProcessTapInfo processTapInfo, WriteTripInfo writeTripInfo, CustomerTapBucketEngine customerTapBucketEngine) {
        this.processPriceInfo = processPriceInfo;
        this.processTapInfo = processTapInfo;
        this.writeTripInfo = writeTripInfo;
        this.customerTapBucketEngine = customerTapBucketEngine;
    }


    public  void run(){
        List<PriceInfo> priceInfoList = processPriceInfo.parsePriceInfo();
        List<TapInfo> tapInfoList = processTapInfo.parseTapInfo();

            //Group by Customer and then sort the Tap Information based on the timestamp
            Map<String, List<TapInfo>> groupByCustomer = tapInfoList
                    .stream()
                    .parallel()
                    .collect(Collectors.groupingByConcurrent(TapInfo::getPan))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> e.getValue()
                                    .stream()
                                    .sorted(Comparator.comparing(TapInfo::getTapTimeStamp)) // Sort based on timestamp
                                    .collect(Collectors.toList())
                    ));

            printGroupedTapInformation(groupByCustomer);


            List<TripInfo> tripInfoArrayList =groupByCustomer
                    .entrySet()
                    .stream()
                    .map( e-> {
                        // convert the tapInfo grouped by customer to TripInfo for each customer.
                         return customerTapBucketEngine.
                                         convertToTripInfoForCustomer(e.getKey(),e.getValue(),priceInfoList);
                    })
                    .collect(
                            ArrayList::new,(arrayList,
                            tripInfoList) ->arrayList.addAll(tripInfoList),
                            ArrayList::addAll);// Combine all the values which will be return to output CSV



        //Write the Trip Information to the Output Stream
            writeTripInfo.writeTripInfoToOutput(tripInfoArrayList);
    }



    private void printGroupedTapInformation(Map<String, List<TapInfo>> groupByCustomer){
        List<TapInfo> listCustomerGroup =groupByCustomer
                .values()
                .stream()
                .flatMap(List:: stream)
                .collect(Collectors.toList());
        listCustomerGroup.forEach(tapInfo -> {
            log.info(tapInfo.toString());
        });
    }
}
