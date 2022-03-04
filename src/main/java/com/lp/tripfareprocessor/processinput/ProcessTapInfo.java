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
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Log4j2
public class ProcessTapInfo {

    @Value("${tap-info}")
    private String tripInput;

    @Value("${trip-customer}")
    private String tripsForCustomer;

    @Autowired
    private ResourceLoader resourceLoader;

    private static BigDecimal ZERO_FAIR = new BigDecimal(0.0);

    private static Map<String, String> tapInfoMap = new HashMap<>();
    private CustomMappingStrategy<TapInfo> tapInfoCustomMappingStrategy = new CustomMappingStrategy<>();


    @PostConstruct
    private void init(){
        //Mapping between bean property names and header names in CSV
        tapInfoMap.put("ID","id");
        tapInfoMap.put("DateTimeUTC","tapTimeStamp");
        tapInfoMap.put("TapType","tapType");
        tapInfoMap.put("StopId","stopId");
        tapInfoMap.put("CompanyId","companyId");
        tapInfoMap.put("BusID","busId");
        tapInfoMap.put("PAN","pan");

        tapInfoCustomMappingStrategy.setType(TapInfo.class);
        tapInfoCustomMappingStrategy.setColumnMapping(tapInfoMap);
    }

    public  List<TapInfo> parseTapInfo(){
        log.info(tripInput);
        log.info(tripsForCustomer);

        try(FileReader tripInfoFileReader = new FileReader(resourceLoader.getResource("classpath:"+tripInput).getFile())) {

            List<TapInfo> tapInfoList = new CsvToBeanBuilder<TapInfo>(tripInfoFileReader)
                    .withType(TapInfo.class)
                    .withMappingStrategy(tapInfoCustomMappingStrategy)
                    .build()
                    .parse();
            return tapInfoList;
        }catch (Exception e){
            log.error("Exception message",e);
            throw new RuntimeException(e);
        }
    }
}

