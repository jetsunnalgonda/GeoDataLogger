package com.isikhaluk.datalogger;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class ExcelLogData_Demo {
    public static void main(String[] args) throws IOException {
        Instant then = Instant.now();

        ExcelLogData logData = new ExcelLogData();
        logData.createLog(0, "Test", "x axis", "y axis");
        logData.createLog(1, "Test 2", "x axis", "y axis");
//        System.out.println("outputSteam: " + logData.outputStream);

//        logData.writeLog();
//        System.out.println("outputSteam: " + logData.outputStream);
//
//        logData.writeLog();
//        System.out.println("outputSteam: " + logData.outputStream);

        for (float i = 0; i<1000; i++) {
            logData.writeLog(1, new Object[] {i, "a"});
//            System.out.println("outputSteam: " + logData.outputStream);
        }
//        logData.writeLog();
        logData.saveLog();
        float timeElapsed = Duration.between(then, Instant.now()).toMillis();
        System.out.println("Time elapsed: " + timeElapsed + "ms");


        logData.closeLog();

    }
}
