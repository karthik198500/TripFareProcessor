package com.lp.tripfareprocessor.engine;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Log4j2
public class TripInfoBuilder {

    private static BigDecimal ZERO_FAIR = new BigDecimal(0.0);

    public TripInfo constructTripInfo(TapInfo top, TapInfo currentTapInfo, List<PriceInfo> priceInfoList) {
        TripInfo tripInfo = TripInfo
                .builder()
                .pan(top.getPan())
                .busId(top.getBusId())
                .companyId(top.getCompanyId())
                .fromStopId(top.getStopId())
                .toStopId(currentTapInfo != null? currentTapInfo.getStopId():"")
                .started(top.dateFormat.format(top.getTapTimeStamp()))
                .finished(currentTapInfo != null?currentTapInfo.dateFormat.format(currentTapInfo.getTapTimeStamp()):"")
                .chargeAmount(currentTapInfo != null?calculateChargeAmount(top,currentTapInfo,priceInfoList):findTheMaximumFareWhenCustomerForgotToTAPOFF(top.getStopId(),priceInfoList))
                .durationSecs(currentTapInfo != null?(currentTapInfo.getTapTimeStamp().getTime()-top.getTapTimeStamp().getTime())/1000:0)
                .status(currentTapInfo != null ? calculateChargeAmount(top,currentTapInfo,priceInfoList).compareTo(ZERO_FAIR)==0?"CANCELLED":"COMPLETED":"INCOMPLETE")
                .build();
        return tripInfo;
    }

    public BigDecimal calculateChargeAmount(TapInfo top, TapInfo currentTapInfo,List<PriceInfo> priceInfoList) {
        if (null!= top && null!= currentTapInfo && null!=priceInfoList && !priceInfoList.isEmpty()) {
            String source = top.getStopId();
            String destination = currentTapInfo.getStopId();

            // source and destination are same. No fare
            if(source.equalsIgnoreCase(destination)){
                return ZERO_FAIR;
            }
            for (PriceInfo priceInfo : priceInfoList) {
                if(null != priceInfo &&
                        (priceInfo.getSourceStationId().equalsIgnoreCase(source) && priceInfo.getDestinationStationId().equalsIgnoreCase(destination))||
                        (priceInfo.getDestinationStationId().equalsIgnoreCase(source) && priceInfo.getSourceStationId().equalsIgnoreCase(destination))){
                    return new BigDecimal(Double.parseDouble(priceInfo.getFair()));
                }
            }
        }
        log.error("Unable to find a match in the priceInfoList for the two trips. Top "+ top.toString()+" current "+currentTapInfo.toString() );
        throw new RuntimeException("Unable to find a match in the priceInfoList for trips.");
    }

    public BigDecimal findTheMaximumFareWhenCustomerForgotToTAPOFF(String stopId,List<PriceInfo> priceInfoList) {

        if (priceInfoList!=null && priceInfoList.size()>0 && null!= stopId) {
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
                        return BigDecimal.valueOf(Math.max(fare, Double.parseDouble(priceInfo.getFair())));
                    }
                }
            }
            if(fare != null){
                //Cases when the stopId is at the beginning or ending of the journey
                return BigDecimal.valueOf(fare);
            }
        }
        return ZERO_FAIR;
    }
}
