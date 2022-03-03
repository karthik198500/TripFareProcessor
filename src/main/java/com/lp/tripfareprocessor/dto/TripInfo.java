package com.lp.tripfareprocessor.dto;

import com.lp.tripfareprocessor.dto.opencsv.TrimStringBeforeRead;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.processor.ConvertEmptyOrBlankStringsToDefault;
import com.opencsv.bean.processor.PreAssignmentProcessor;
import lombok.*;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripInfo {

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "Started", required = true)
    private String started;

    @CsvBindByName(column = "Finished", required = false)
    private String finished;

    @CsvBindByName(column = "DurationSecs", required = false)
    private long durationSecs;

    @CsvBindByName(column = "FromStopId", required = false)
    private String fromStopId;

    @CsvBindByName(column = "ToStopId", required = false)
    private String toStopId;

    @CsvBindByName(column = "ChargeAmount", required = false)
    private String chargeAmount;

    @CsvBindByName(column = "CompanyId", required = true)
    private String companyId;

    @CsvBindByName(column = "BusId", required = true)
    private String busId;

    @CsvBindByName(column = "PAN", required = true)
    private String pan;

    @CsvBindByName(column = "Status", required = true)
    private String status;

    @Override
    public String toString() {
        return "TripInfo{" +
                "started='" + started + '\'' +
                ", finished='" + finished + '\'' +
                ", durationSecs='" + durationSecs + '\'' +
                ", fromStopId='" + fromStopId + '\'' +
                ", toStopId='" + toStopId + '\'' +
                ", chargeAmount='" + chargeAmount + '\'' +
                ", companyId='" + companyId + '\'' +
                ", busId='" + busId + '\'' +
                ", pan='" + pan + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
