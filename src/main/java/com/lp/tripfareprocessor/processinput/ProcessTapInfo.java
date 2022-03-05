package com.lp.tripfareprocessor.processinput;

import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.util.opencsv.CustomMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

@Component
@Log4j2
public class ProcessTapInfo {

    private static BigDecimal ZERO_FAIR = new BigDecimal(0.0);

    private static Map<String, String> tapInfoMap = null;
    private CustomMappingStrategy<TapInfo> tapInfoCustomMappingStrategy = null;


    @PostConstruct
    public void init(){
        tapInfoMap = new HashMap<>();
        tapInfoCustomMappingStrategy = new CustomMappingStrategy<>();
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

    public  List<TapInfo> parseTapInfo(String tripInput){
        List<TapInfo> tapInfoList = new ArrayList<>();

        try(FileReader tripInfoFileReader = new FileReader(tripInput)) {

            // Read line by line to avoid out of memory error.
            CsvToBean<TapInfo> csvToBean = new CsvToBeanBuilder<TapInfo>(tripInfoFileReader)
                    .withType(TapInfo.class)
                    .withMappingStrategy(tapInfoCustomMappingStrategy)
                    .build();

            Iterator<TapInfo> it = csvToBean.iterator();
            while(it.hasNext()){
                tapInfoList.add(it.next());
            }
            return tapInfoList;
        }catch (Exception e){
            log.error("Exception message",e);
            throw new RuntimeException(e);
        }
    }
    /*
    Process In Memory if file size is small.
    * */
    private List<TapInfo> processInMemory(FileReader tripInfoFileReader){

        List<TapInfo> tapInfoList = new CsvToBeanBuilder<TapInfo>(tripInfoFileReader)
                .withType(TapInfo.class)
                .withMappingStrategy(tapInfoCustomMappingStrategy)
                .build()
                .parse();
        return tapInfoList;

    }
}

