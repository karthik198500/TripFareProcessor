package com.lp.tripfareprocessor.dto.opencsv;

import com.opencsv.bean.processor.ConvertEmptyOrBlankStringsToDefault;

public class TrimStringBeforeRead extends ConvertEmptyOrBlankStringsToDefault {

    public String processString(String value) {
        return value != null && !value.trim().isEmpty() ? value.trim() : "";
    }
}
