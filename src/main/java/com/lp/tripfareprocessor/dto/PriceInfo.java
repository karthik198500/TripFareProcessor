package com.lp.tripfareprocessor.dto;

import com.lp.tripfareprocessor.dto.opencsv.TrimStringBeforeRead;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.processor.PreAssignmentProcessor;

public class PriceInfo {

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "source", required = true)
    private String sourceStationId;

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "destination", required = true)
    private String destinationStationId;

    @PreAssignmentProcessor(processor = TrimStringBeforeRead.class)
    @CsvBindByName(column = "fair", required = true)
    private String fair;

    @Override
    public String toString() {
        return "PriceInfo{" +
                "sourceStationId='" + sourceStationId + '\'' +
                ", destinationStationId='" + destinationStationId + '\'' +
                ", fair='" + fair + '\'' +
                '}';
    }
}
