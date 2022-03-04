package com.lp.tripfareprocessor.engine;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
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
                .started(top.getDateFormat().format(top.getTapTimeStamp()))
                .finished(currentTapInfo != null?currentTapInfo.getDateFormat().format(currentTapInfo.getTapTimeStamp()):"")
                .chargeAmount(currentTapInfo != null?calculateChargeAmount(top,currentTapInfo,priceInfoList):findTheMaximumFareWhenCustomerForgotToTAPOFF(top.getStopId(),priceInfoList))
                .durationSecs(currentTapInfo != null?(currentTapInfo.getTapTimeStamp().getTime()-top.getTapTimeStamp().getTime())/1000:0)
                .status(currentTapInfo != null ? calculateChargeAmount(top,currentTapInfo,priceInfoList).compareTo(ZERO_FAIR)==0?"CANCELLED":"COMPLETED":"INCOMPLETE")
                .build();
        return tripInfo;
    }

    public BigDecimal calculateChargeAmount(TapInfo top, TapInfo currentTapInfo,List<PriceInfo> priceInfoList) {
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
        return null;
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
        }
        return ZERO_FAIR;
    }
}
