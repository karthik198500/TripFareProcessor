package com.lp.tripfareprocessor;

import com.lp.tripfareprocessor.engine.TripFareEngine;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
@Getter
@Setter
public class TripFareProcessorApplication implements CommandLineRunner {

    @Autowired
    private TripFareEngine tripFareEngine;

    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION");
        SpringApplication.run(TripFareProcessorApplication.class, args);
        log.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        if(null!= args && args.length >=2){
            String tapInformationSrc = args[0];
            String priceInformationSrc = args[1];
            String outputFile = args[2];
            log.info("EXECUTING : command line runner");
            tripFareEngine.run(tapInformationSrc,priceInformationSrc,outputFile);
        }else{
            log.error("Please run the program with atleast three input argument. Please correct and try again.");
            throw new RuntimeException("Please run the program with atleast three input argument. Please correct and try again.");
        }

    }
}

