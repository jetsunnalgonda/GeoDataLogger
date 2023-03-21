package com.isikhaluk.datalogger;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialPortLogic_old {
    public SerialPortLogic_old() {
        //In the constructor pass the name of the port with which we work
        SerialPort serialPort = new SerialPort("COM1");
        try {
            //Open port
            serialPort.openPort();
            //We expose the settings. You can also use this line - serialPort.setParams(9600, 8, 1, 0);
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            //Writes data to port
//            serialPort.writeBytes("Test");
            serialPort.writeString("Test");
            //Read the data of 10 bytes. Be careful with the method readBytes(), if the number of bytes in the input buffer
            //is less than you need, then the method will wait for the right amount. Better to use it in conjunction with the
            //interface SerialPortEventListener.
            byte[] buffer = serialPort.readBytes(10);
            //Closing the port
            serialPort.closePort();
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
}
