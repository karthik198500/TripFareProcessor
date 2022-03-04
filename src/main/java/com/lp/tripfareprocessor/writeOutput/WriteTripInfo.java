package com.lp.tripfareprocessor.writeOutput;

import com.lp.tripfareprocessor.dto.TripInfo;
import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategyBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Log4j2
public class WriteTripInfo {

    public void writeTripInfoToOutput(List<TripInfo> tripInfoArrayList, String tripsForCustomer) {

        File file = new File(tripsForCustomer);
        //Writer writer = new FileWriter(resourceLoader.getResource("classpath:"+tripsForCustomer).getFile())
        try( Writer writer = new FileWriter(file);
             CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, '"', "\n");

        ){
            HeaderColumnNameMappingStrategy<TripInfo> strategy = new HeaderColumnNameMappingStrategyBuilder<TripInfo>().build();
            strategy.setType(TripInfo.class);
            strategy.setColumnOrderOnWrite(new OrderedComparatorIgnoringCase(TripInfo.FIELDS_ORDER));
            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(csvWriter)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(tripInfoArrayList);
            tripInfoArrayList.forEach(tripInfo -> {
                log.info(tripInfo.toString());
            });
        }catch (Exception e){
            log.error("Exception while writing",e);
            throw new RuntimeException(e);
        }
    }

    public class OrderedComparatorIgnoringCase implements Comparator<String> {
        private List<String> predefinedOrder;

        public OrderedComparatorIgnoringCase(String[] predefinedOrder) {
            this.predefinedOrder = new ArrayList<>();
            for (String item : predefinedOrder) {
                this.predefinedOrder.add(item.toLowerCase());
            }
        }
        @Override
        public int compare(String o1, String o2) {
            return predefinedOrder.indexOf(o1.toLowerCase()) - predefinedOrder.indexOf(o2.toLowerCase());
        }
    }
}
