package com.lp.tripfareprocessor.engine;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import com.lp.tripfareprocessor.processinput.ProcessPriceInfo;
import com.lp.tripfareprocessor.processinput.ProcessTapInfo;

import com.lp.tripfareprocessor.writeOutput.WriteTripInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
public class TripFareEngine {

    private final ProcessPriceInfo processPriceInfo;
    private final ProcessTapInfo processTapInfo;
    private final WriteTripInfo writeTripInfo;
    private final TapBucketEngine tapBucketEngine;

    public TripFareEngine(ProcessPriceInfo processPriceInfo, ProcessTapInfo processTapInfo, WriteTripInfo writeTripInfo, TapBucketEngine tapBucketEngine) {
        this.processPriceInfo = processPriceInfo;
        this.processTapInfo = processTapInfo;
        this.writeTripInfo = writeTripInfo;
        this.tapBucketEngine = tapBucketEngine;
    }
    @Value("${tap-info}")
    private String tapInformationSrc;

    @Value("${price-info}")
    private String priceInput;

    @Value("${trip-customer}")
    private String tripsForCustomer;

    public void run(String tapInformationSrc, String priceInformationSrc, String outputFile) {
        this.tapInformationSrc = tapInformationSrc;
        this.priceInput = priceInformationSrc;
        this.tripsForCustomer = outputFile;
        run();
    }

    public  void run(){
        List<PriceInfo> priceInfoList = processPriceInfo.parsePriceInfo(priceInput);
        List<TapInfo> tapInfoList = processTapInfo.parseTapInfo(tapInformationSrc);

            //Group by Customer and then sort the Tap Information based on the timestamp
            Map<String, List<TapInfo>> groupByCustomer = tapInfoList
                    .stream()
                    .parallel()
                    .collect(Collectors.groupingByConcurrent(TapInfo::getPan))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toConcurrentMap(Map.Entry::getKey,
                            e -> e.getValue()
                                    .stream()
                                    .sorted(Comparator.comparing(TapInfo::getTapTimeStamp)) // Sort based on timestamp
                                    .collect(Collectors.toList())
                    ));

            printGroupedTapInformation(groupByCustomer);


            List<TripInfo> tripInfoArrayList = groupByCustomer
                    .entrySet()
                    .stream()
                    .map( e-> {
                        // convert the "tapInfo grouped by customer" to TripInfo for each customer.
                         return tapBucketEngine.
                                         convertToTripInfoForCustomer(e.getKey(),e.getValue(),priceInfoList);
                    })
                    .collect(
                            ArrayList::new,(arrayList,
                            tripInfoList) ->arrayList.addAll(tripInfoList),
                            ArrayList::addAll);// Combine all the values which will be return to output CSV

        //Write the Trip Information to the Output Stream
            writeTripInfo.writeTripInfoToOutput(tripInfoArrayList,tripsForCustomer);
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
