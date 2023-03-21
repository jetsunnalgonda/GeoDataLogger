package com.isikhaluk.datalogger;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.time.Instant;
import java.util.function.Consumer;

public class SerialPortLogic implements SerialPortEventListener {

    private final Consumer<String> textHandler;
    SerialPort serialPortToRead;
    Boolean chartStarted;
    Instant startTime;

    SerialPortLogic(SerialPort serialPort, Consumer<String> textHandler) {
        this.textHandler = textHandler ;
        this.serialPortToRead = serialPort;
        this.chartStarted = true;
    }

//    SerialPort serialPortToRead = serialPort;

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {
            try {
                byte[] buf = serialPortToRead.readBytes();
                if (buf == null || buf.length <= 0) {
                    return;
                }


                String buffer = new String(buf, 0, buf.length);


                textHandler.accept(buffer);


            } catch (SerialPortException ex) {
                System.out.println(ex);
            }
        }
    }

}
