package com.lp.tripfareprocessor.processinput;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import com.lp.tripfareprocessor.dto.opencsv.CustomMappingStrategy;
import com.opencsv.bean.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Path;
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
    private List<PriceInfo> priceInfoList;

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

            priceInfoList = new CsvToBeanBuilder<PriceInfo>(priceInfoFileReader )
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


            List<TapInfo> tapInfoList = new CsvToBeanBuilder<TapInfo>(tripInfoFileReader )
                    .withType(TapInfo.class)
                    .withMappingStrategy(tapInfoCustomMappingStrategy)
                    .build()
                    .parse();

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
                                    .sorted(Comparator.comparing(TapInfo::getTapTimeStamp))
                                    .collect(Collectors.toList())
                    ));

            printGroupedTapInformation(groupByCustomer);

            // convert the tapInfo to TripInfo and combine all the values which will be return to output CSV
            List<TripInfo> tripInfoArrayList =groupByCustomer
                    .entrySet()
                    .stream()
                    .map( e-> {
                         return convertToTripInfo(e.getKey(),e.getValue());
                    })
                    .collect(ArrayList::new,
                            (arrayList,tripInfoList) ->arrayList.addAll(tripInfoList)
                            , ArrayList::addAll);

            writeCsvFromBean(tripInfoArrayList);
            tripInfoArrayList.forEach(tripInfo -> {
                log.info(tripInfo.toString());
            });

        }catch (Exception e){
            log.error("Exception message",e);
        }
    }

    private List<TripInfo> convertToTripInfo(String pan, List<TapInfo> tapInfoList) {
        Stack<TapInfo> tapInfoStack = new Stack<>();
        ArrayList<TripInfo> tripInfoArrayList = new ArrayList<>();
        tapInfoList.forEach(
                currentTapInfo -> {
                    if(tapInfoStack.isEmpty()){
                        tapInfoStack.push(currentTapInfo);
                    }else{
                        TapInfo top = tapInfoStack.peek();
                        if(canMerge(top,currentTapInfo)){
                            tripInfoArrayList.add(constructTripInfo(tapInfoStack.pop(),currentTapInfo));
                        }else{
                            tapInfoStack.push(currentTapInfo);
                        }
                    }
                }
        );
        //If there are any remaining rows in the stack just add them to Trip Info
        tapInfoStack.forEach( tapInfo -> {
            tripInfoArrayList.add(constructTripInfo(tapInfo,null));
        });
        return tripInfoArrayList;
    }

    //We are making sure TAP ON and TAP OFF are set to go only when company ID and Bus ID also matches
    private boolean canMerge( TapInfo top, TapInfo currentTapInfo){
        if((top.getTapType().equalsIgnoreCase("ON") && currentTapInfo.getTapType().equalsIgnoreCase("OFF"))&&
           top.getCompanyId().equalsIgnoreCase(currentTapInfo.getCompanyId()) &&
           top.getBusId().equalsIgnoreCase(currentTapInfo.getBusId())){
            return true;
        }
        return false;
    }

    private TripInfo constructTripInfo(TapInfo top, TapInfo currentTapInfo) {
        TripInfo tripInfo = TripInfo
                .builder()
                .pan(top.getPan())
                .busId(top.getBusId())
                .companyId(top.getCompanyId())
                .fromStopId(top.getStopId())
                .toStopId(currentTapInfo != null? currentTapInfo.getStopId():"")
                .started(top.getDateFormat().format(top.getTapTimeStamp()))
                .finished(currentTapInfo != null?currentTapInfo.getDateFormat().format(currentTapInfo.getTapTimeStamp()):"")
                .chargeAmount(currentTapInfo != null?calculateChargeAmount(top,currentTapInfo):findTheMaximumFareWhenCustomerForgotToTAPOFF(top.getStopId()))
                .durationSecs(currentTapInfo != null?(currentTapInfo.getTapTimeStamp().getTime()-top.getTapTimeStamp().getTime())/1000:0)
                .status(currentTapInfo != null ? calculateChargeAmount(top,currentTapInfo) =="0.0"?"CANCELLED":"COMPLETED":"INCOMPLETE")
                .build();
        return tripInfo;
    }

    private String calculateChargeAmount(TapInfo top, TapInfo currentTapInfo) {
        String source = top.getStopId();
        String destination = currentTapInfo.getStopId();

        // source and destination are same. No fare
        if(source == destination){
            return "0.0";
        }
        for (PriceInfo priceInfo : priceInfoList) {
            if(null != priceInfo &&
                    (priceInfo.getSourceStationId().equalsIgnoreCase(source) && priceInfo.getDestinationStationId().equalsIgnoreCase(destination))||
                    (priceInfo.getDestinationStationId().equalsIgnoreCase(source) && priceInfo.getSourceStationId().equalsIgnoreCase(destination))){
                return priceInfo.getFair();
            }
        }

        return null;
    }

    private String findTheMaximumFareWhenCustomerForgotToTAPOFF(String stopId) {

        String startStopId = priceInfoList.get(0).getSourceStationId();
        String endStopId = priceInfoList.get(priceInfoList.size()-1).getDestinationStationId();
        Double fare = null;

        for (PriceInfo priceInfo : priceInfoList) {
            if(null!= priceInfo &&
                    (priceInfo.getSourceStationId().equalsIgnoreCase(stopId) && priceInfo.getDestinationStationId().equalsIgnoreCase(endStopId)) ||
                    (priceInfo.getSourceStationId().equalsIgnoreCase( startStopId) && priceInfo.getDestinationStationId().equalsIgnoreCase(stopId)) ){
                if(null == fare){
                    fare = Double.parseDouble(priceInfo.getFair());
                }else{
                    return String.valueOf(Math.max(fare, Double.parseDouble(priceInfo.getFair())));
                }
            }
        }
        return "";
    }

    private void printGroupedTapInformation(Map<String, List<TapInfo>> groupByCustomer){
        List<TapInfo> listCustomerGroup =groupByCustomer
                .values()
                .stream()
                .flatMap(List:: stream)
                .collect(Collectors.toList());

        log.info("****************************");
        listCustomerGroup.forEach(tapInfo -> {
            log.info(tapInfo.toString());
        });
        log.info("****************************");
    }

    public void writeCsvFromBean(List<TripInfo> tripInfoArrayList) {
        try( Writer writer = new FileWriter(resourceLoader.getResource("classpath:"+tripsForCustomer).getFile())){
            HeaderColumnNameMappingStrategy<TripInfo> strategy = new HeaderColumnNameMappingStrategyBuilder<TripInfo>().build();
            strategy.setType(TripInfo.class);
            //strategy.setColumnOrderOnWrite(new MyComparator());
            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(tripInfoArrayList);
        }catch (Exception e){
            log.error("Exception while writing",e);
        }
    }
}
