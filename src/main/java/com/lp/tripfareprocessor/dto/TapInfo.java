package com.lp.tripfareprocessor.dto;

import com.lp.tripfareprocessor.dto.opencsv.TrimStringBeforeRead;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.processor.PreAssignmentProcessor;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class TapInfo {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "ID", required = true)
    private String id;

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "DateTimeUTC", required = true)
    @CsvDate("dd-MM-yyyy hh:mm:ss")
    private Date tapTimeStamp;

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "TapType", required = true)
    private String tapType;

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "StopId", required = true)
    private String stopId;

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "CompanyId", required = true)
    private String companyId;

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "BusID", required = true)
    private String busId;

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "PAN", required = true)
    private String pan;


    @Override
    public String toString() {
        return "TapInfo{" +
                " pan='" + pan + '\'' +
                ", id='" + id + '\'' +
                ", tapTimeStamp=" + dateFormat.format(tapTimeStamp) +
                ", tapType='" + tapType + '\'' +
                ", stopId='" + stopId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", busId='" + busId + '\'' +
                '}';
    }
}
