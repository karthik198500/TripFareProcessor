package com.lp.tripfareprocessor;

import com.lp.tripfareprocessor.processinput.ProcessInput;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class TripFareProcessorApplication implements CommandLineRunner {

    @Autowired
    private ProcessInput processInput;

    public static void main(String[] args) {
        log.info("STARTING THE APPLICATION");
        SpringApplication.run(TripFareProcessorApplication.class, args);
        log.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        log.info("EXECUTING : command line runner");
        processInput.run();
    }
}

