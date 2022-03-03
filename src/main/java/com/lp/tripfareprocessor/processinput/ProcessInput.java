package com.lp.tripfareprocessor.processinput;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.opencsv.CustomMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
public class ProcessInput {

    @Value("${tap-info}")
    private String tripInput;

    @Value("${price-info}")
    private String priceInput;

    @Value("${trip-customer}")
    private String tripsForCustomer;

    @Autowired
    private ResourceLoader resourceLoader;

    private static Map<String, String> tapInfoMap = new HashMap<>();
    private static Map<String, String> priceInfoMap = new HashMap<>();
    private CustomMappingStrategy<TapInfo> tapInfoCustomMappingStrategy = new CustomMappingStrategy<>();
    private CustomMappingStrategy<PriceInfo> priceInfoCustomMappingStrategy = new CustomMappingStrategy<>();


    @PostConstruct
    private void init(){
        tapInfoMap.put("ID","id");
        tapInfoMap.put("DateTimeUTC","tapTimeStamp");
        tapInfoMap.put("TapType","tapType");
        tapInfoMap.put("StopId","stopId");
        tapInfoMap.put("CompanyId","companyId");
        tapInfoMap.put("BusID","busId");
        tapInfoMap.put("PAN","pan");

        priceInfoMap.put("source","sourceStationId");
        priceInfoMap.put("destination","destinationStationId");
        priceInfoMap.put("fair","fair");

        tapInfoCustomMappingStrategy.setType(TapInfo.class);
        tapInfoCustomMappingStrategy.setColumnMapping(tapInfoMap);

        priceInfoCustomMappingStrategy.setType(PriceInfo.class);
        priceInfoCustomMappingStrategy.setColumnMapping(priceInfoMap);

    }


    public  void run(){
        log.info(tripInput);
        log.info(priceInput);
        log.info(tripsForCustomer);


        try(FileReader tripInfoFileReader = new FileReader(resourceLoader.getResource("classpath:"+tripInput).getFile());
            FileReader priceInfoFileReader = new FileReader(resourceLoader.getResource("classpath:"+priceInput).getFile())){


            List<TapInfo> tapInfoList = new CsvToBeanBuilder<TapInfo>(tripInfoFileReader )
                    .withType(TapInfo.class)
                    .withMappingStrategy(tapInfoCustomMappingStrategy)
                    .build()
                    .parse();

        /*   tapInfoList = tapInfoList
                    .stream()
                    .sorted(Comparator.comparing(TapInfo::getTapTimeStamp))
                    .collect(Collectors.toList());
            */
            Map<String, List<TapInfo>> groupByCustomer = tapInfoList
                    .stream()
                    .parallel()
                    .collect(Collectors.groupingByConcurrent(TapInfo::getPan))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> e.getValue()
                                    .stream()
                                    .sorted(Comparator.comparing(TapInfo::getTapTimeStamp))
                                    .collect(Collectors.toList())
                    ));

            /*tapInfoList.forEach(tapInfo -> {
                log.info(tapInfo.toString());
            });*/
            List<TapInfo> listCustomerGroup =groupByCustomer
                    .values()
                    .stream()
                    .flatMap(List:: stream)
                    .collect(Collectors.toList());

            listCustomerGroup.forEach(tapInfo -> {
                log.info(tapInfo.toString());
            });

            List<PriceInfo> priceInfoList = new CsvToBeanBuilder<PriceInfo>(priceInfoFileReader )
                    .withType(PriceInfo.class)
                    .withMappingStrategy(priceInfoCustomMappingStrategy)
                    .build()
                    .parse();

            priceInfoList.forEach(priceInfo -> {
                log.info(priceInfo.toString());
            });

        }catch (Exception e){
            log.error("Exception message",e);
        }
    }
}
