package com.lp.tripfareprocessor.util.opencsv;

import com.opencsv.CSVReader;
import com.opencsv.bean.FieldMapByNameEntry;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class CustomMappingStrategy<T> extends HeaderColumnNameTranslateMappingStrategy<T> {

    /*
        We have to write this function because provided CSV contain spaces after commas. While spaces in
        CSV is debatable, current class handles by trimming it.
    * */
    @Override
    public void captureHeader(CSVReader reader) throws IOException, CsvRequiredFieldEmptyException {
        if (this.type == null) {
            throw new IllegalStateException(ResourceBundle.getBundle("opencsv", this.errorLocale).getString("type.unset"));
        } else {
            String[] header = ArrayUtils.nullToEmpty(reader.readNextSilently());
            header = Arrays.stream(header).map(String::trim).toArray(String[]::new);

            for(int i = 0; i < header.length; ++i) {
                if (header[i] == null) {
                    header[i] = "";
                }
            }

            this.headerIndex.initializeHeaderIndex(header);
            List<FieldMapByNameEntry<T>> missingRequiredHeaders = this.fieldMap.determineMissingRequiredHeaders(header);
            if (!missingRequiredHeaders.isEmpty()) {
                String[] requiredHeaderNames = new String[missingRequiredHeaders.size()];
                List<Field> requiredFields = new ArrayList(missingRequiredHeaders.size());

                for(int i = 0; i < missingRequiredHeaders.size(); ++i) {
                    FieldMapByNameEntry<T> fme = (FieldMapByNameEntry)missingRequiredHeaders.get(i);
                    if (fme.isRegexPattern()) {
                        requiredHeaderNames[i] = String.format(ResourceBundle.getBundle("opencsv", this.errorLocale).getString("matching"), fme.getName());
                    } else {
                        requiredHeaderNames[i] = fme.getName();
                    }

                    requiredFields.add(fme.getField().getField());
                }

                String missingRequiredFields = String.join(", ", requiredHeaderNames);
                String allHeaders = String.join(",", header);
                CsvRequiredFieldEmptyException e = new CsvRequiredFieldEmptyException(this.type, requiredFields, String.format(ResourceBundle.getBundle("opencsv", this.errorLocale).getString("header.required.field.absent"), missingRequiredFields, allHeaders));
                e.setLine(header);
                throw e;
            }
        }
    }

}
