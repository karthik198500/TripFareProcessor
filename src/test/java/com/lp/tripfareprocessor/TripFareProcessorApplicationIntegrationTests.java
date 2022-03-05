package com.lp.tripfareprocessor;

import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import com.lp.tripfareprocessor.engine.TripFareEngine;
import com.lp.tripfareprocessor.processinput.ProcessTapInfo;
import com.lp.tripfareprocessor.util.opencsv.CustomMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@SpringBootTest
@ActiveProfiles("test")
class TripFareProcessorApplicationIntegrationTests {

    @Autowired
    TripFareProcessorApplication tripFareProcessorApplication;

    @Value("${trip-customer}")
    private String tripsForCustomer;


    private ProcessTapInfo processTapInfo;

    @TempDir
    File anotherTempDir;

    @Test
    void contextLoads() {
        File file = new File(tripsForCustomer);

        Map<String, String> tripInfoMap = new HashMap<>();
        //Mapping between bean property names and header names in CSV
        tripInfoMap.put("Started","started");
        tripInfoMap.put("Finished","finished");
        tripInfoMap.put("DurationSecs","durationSecs");
        tripInfoMap.put("FromStopId","fromStopId");
        tripInfoMap.put("ToStopId","toStopId");
        tripInfoMap.put("ChargeAmount","chargeAmount");
        tripInfoMap.put("CompanyId","companyId");
        tripInfoMap.put("BusId","busId");
        tripInfoMap.put("PAN","pan");
        tripInfoMap.put("Status","status");

        CustomMappingStrategy<TripInfo> customMappingStrategy = new CustomMappingStrategy<>();
        customMappingStrategy.setType(TripInfo.class);
        customMappingStrategy.setColumnMapping(tripInfoMap);
        List<TripInfo> tripInfoList = new ArrayList<>();
        if(file.exists()){
            try(FileReader tripInfoFileReader = new FileReader(tripsForCustomer)) {

                // Read line by line to avoid out of memory error.
                CsvToBean<TripInfo> csvToBean = new CsvToBeanBuilder<TripInfo>(tripInfoFileReader)
                        .withType(TripInfo.class)
                        .withMappingStrategy(customMappingStrategy)
                        .build();

                Iterator<TripInfo> it = csvToBean.iterator();
                while(it.hasNext()){
                    tripInfoList.add(it.next());
                }
                Assertions.assertTrue(tripInfoList.size()>0);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    @BeforeEach
    void init(){

    }

    @Test
    void testRunMethodThreeArgument(){
        String[] args = {createTapsFile().getPath(),createPricesFile().getPath(),createEmptyFile().getPath()};
        tripFareProcessorApplication.run(args);

    }

    private File createPricesFile() {
        File nemFile = new File(anotherTempDir, "route-prices.csv");
        List<String> lines = Arrays.asList("source,destination,fair\n" +
                "Stop1,Stop2,3.25\n" +
                "Stop2,Stop3,5.5\n" +
                "Stop1,Stop3,7.3");
        try {
            Files.write(nemFile.toPath(), lines);
        } catch (IOException e) {

        }
        return nemFile;
    }

    private File createTapsFile() {
        File nemFile = new File(anotherTempDir, "tap-info.csv");
        List<String> lines = Arrays.asList("ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN\n" +
                "1, 22-01-2018 13:00:00, ON, Stop1, Company1, Bus37, 5500005555555559\n" +
                "2, 22-01-2018 13:05:00, OFF, Stop3, Company1, Bus37, 5500005555555559");
        try {
            Files.write(nemFile.toPath(), lines);
        } catch (IOException e) {
        }
        return nemFile;
    }

    private File createEmptyFile() {
        File nemFile = new File(anotherTempDir, "trips-customer.csv");
        try {
            Files.write(nemFile.toPath(), Arrays.asList(""));
        } catch (IOException e) {
        }
        return nemFile;
    }

}
