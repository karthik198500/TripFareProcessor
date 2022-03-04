package com.lp.tripfareprocessor.dto.opencsv;

import com.opencsv.bean.processor.ConvertEmptyOrBlankStringsToDefault;

public class TrimStringBeforeRead extends ConvertEmptyOrBlankStringsToDefault {

    /*
    We have to write this function because provided CSV contain spaces after commas. While spaces in
        CSV is debatable, current class handles by trimming it.
    * */
    public String processString(String value) {
        return value != null && !value.trim().isEmpty() ? value.trim() : "";
    }
}
