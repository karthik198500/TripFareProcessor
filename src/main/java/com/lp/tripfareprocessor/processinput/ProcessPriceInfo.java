package com.lp.tripfareprocessor.processinput;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.opencsv.CustomMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Log4j2
public class ProcessPriceInfo {



    @Autowired
    private ResourceLoader resourceLoader;

    private static Map<String, String> priceInfoMap = new HashMap<>();
    private CustomMappingStrategy<PriceInfo> priceInfoCustomMappingStrategy = new CustomMappingStrategy<>();
    private List<PriceInfo> priceInfoList;

    @PostConstruct
    private void init(){
        //Mapping between bean property names and header names in CSV
        priceInfoMap.put("source","sourceStationId");
        priceInfoMap.put("destination","destinationStationId");
        priceInfoMap.put("fair","fair");

        priceInfoCustomMappingStrategy.setType(PriceInfo.class);
        priceInfoCustomMappingStrategy.setColumnMapping(priceInfoMap);
    }

    public List<PriceInfo> parsePriceInfo(String priceInput){
        try(FileReader priceInfoFileReader =
                    new FileReader(priceInput)) {
            priceInfoList = new CsvToBeanBuilder<PriceInfo>(priceInfoFileReader)
                    .withType(PriceInfo.class)
                    .withMappingStrategy(priceInfoCustomMappingStrategy)
                    .build()
                    .parse();
            priceInfoList = priceInfoList.stream()
                    .sorted(Comparator.comparing(PriceInfo::getSourceStationId))
                    .collect(Collectors.toList());

            priceInfoList.forEach(priceInfo -> {
                log.info(priceInfo.toString());
            });
            return priceInfoList;


        }catch (Exception e){
                log.error("Exception message",e);
            throw new RuntimeException(e);
        }
    }
}

