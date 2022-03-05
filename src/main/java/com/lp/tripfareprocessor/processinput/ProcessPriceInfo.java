package com.lp.tripfareprocessor.processinput;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.util.opencsv.CustomMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
@Log4j2
public class ProcessPriceInfo {

    private static Map<String, String> priceInfoMap = null ;
    private CustomMappingStrategy<PriceInfo> priceInfoCustomMappingStrategy = null;
    private List<PriceInfo> priceInfoList;

    @PostConstruct
    public void init(){
        //Mapping between bean property names and header names in CSV
        priceInfoMap = new HashMap<>() ;
        priceInfoCustomMappingStrategy = new CustomMappingStrategy<>();
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

