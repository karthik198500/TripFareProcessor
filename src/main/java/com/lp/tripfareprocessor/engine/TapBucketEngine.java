package com.lp.tripfareprocessor.engine;

import com.lp.tripfareprocessor.dto.PriceInfo;
import com.lp.tripfareprocessor.dto.TapInfo;
import com.lp.tripfareprocessor.dto.TripInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
@Log4j2
@Getter
@Setter
public class TapBucketEngine {

    private final TripInfoBuilder tripInfoBuilder;

    public TapBucketEngine(TripInfoBuilder tripInfoBuilder) {
        this.tripInfoBuilder = tripInfoBuilder;
    }

    public List<TripInfo> convertToTripInfoForCustomer(String pan,
                                                       List<TapInfo> customerTapInfoBucket,
                                                       List<PriceInfo> priceInfoList) {
        Stack<TapInfo> tapInfoStack = new Stack<>();
        ArrayList<TripInfo> tripInfoArrayList = new ArrayList<>();
        customerTapInfoBucket.forEach(
                currentTapInfo -> {
                    if(tapInfoStack.isEmpty()){
                        tapInfoStack.push(currentTapInfo);
                    }else{
                        TapInfo top = tapInfoStack.peek();
                        if(canMerge(top,currentTapInfo)){
                            tripInfoArrayList.add(tripInfoBuilder.constructTripInfo(tapInfoStack.pop(),currentTapInfo,priceInfoList));
                        }else{
                            tapInfoStack.push(currentTapInfo);
                        }
                    }
                }
        );
        //If there are any remaining rows in the stack just add them to Trip Info. These are rows which have only one
        // matching TAP ON or TAP OFF.
        tapInfoStack.forEach( tapInfo -> {
            tripInfoArrayList.add(tripInfoBuilder.constructTripInfo(tapInfo,null,priceInfoList));
        });
        return tripInfoArrayList;
    }

    /*
    * Merge Tap ON and Tap OFF (convert it to a trip) when company ID and Bus ID also matches
    */
    public boolean canMerge( TapInfo top, TapInfo currentTapInfo){
        if((top.getTapType().equalsIgnoreCase("ON") && currentTapInfo.getTapType().equalsIgnoreCase("OFF"))&&
                top.getCompanyId().equalsIgnoreCase(currentTapInfo.getCompanyId()) &&
                top.getBusId().equalsIgnoreCase(currentTapInfo.getBusId())){
            return true;
        }
        return false;
    }
}
