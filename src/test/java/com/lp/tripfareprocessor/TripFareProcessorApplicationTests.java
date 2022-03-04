package com.lp.tripfareprocessor;

import com.lp.tripfareprocessor.engine.TripFareEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TripFareProcessorApplicationTests {

    @Mock
    TripFareProcessorApplication tripFareProcessorApplication;

    @Mock
    TripFareEngine tripFareEngine;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void init(){
        tripFareProcessorApplication = new TripFareProcessorApplication();
        tripFareProcessorApplication.setTripFareEngine(tripFareEngine);
    }

    @Test
    void testRunMethodWithEmptyArguments(){
        boolean executedSuccessfully = true;
        try {
            tripFareProcessorApplication.run();
        } catch (Exception e) {
            executedSuccessfully = false;
        }
        Assertions.assertFalse(executedSuccessfully);
    }

    @Test
    void testRunMethodThreeArgument(){
        boolean executedSuccessfully = true;
        Mockito.doNothing().when(tripFareEngine).run(Mockito.any(String.class),Mockito.any(String.class),Mockito.any(String.class));
        try {
            tripFareProcessorApplication.run("","","");
        } catch (Exception e) {
            executedSuccessfully = false;
            //Test to see no errors are returned indicating test passed.
        }
        Assertions.assertTrue(executedSuccessfully);
    }

    @Test
    void testRunMethodTwoArgument(){
        boolean executedSuccessfully = true;
        try {
            tripFareProcessorApplication.run("","");
        } catch (Exception e) {
            executedSuccessfully = false;
            //Test to see no errors are returned indicating test passed.
        }
        Assertions.assertFalse(executedSuccessfully);
    }



}
