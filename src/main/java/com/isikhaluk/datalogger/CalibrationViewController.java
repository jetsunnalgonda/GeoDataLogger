package com.isikhaluk.datalogger;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalibrationViewController {
    @FXML TextArea serialMonitorText;
    @FXML Button buttonConnect, set1, set2, set3, set4;
    @FXML Label labelStatusNow;
    @FXML CheckBox checkAutoScroll;

    @FXML RadioButton radioTransducer1, radioTransducer2, radioTransducer3, radioTransducer4;

    @FXML TextField text1, text2, text3, text4;
//    @FXML ArrayList<TextField> textFields;

    Double[] calibrationValues = new Double[0];

    Preferences preferences = Preferences.userNodeForPackage(CalibrationViewController.class);

//    MainViewController mainApp;

    private boolean portConnected = false;
    private static String CONNECT = "Connect";
    private static String DISCONNECT = "Disconnect";
    private static String CONNECTING = "Connecting...";
    private static String DISCONNECTING = "Disconnecting...";
    private static String CONNECTED = "Connected";
    private static String NOTCONNECTED = "Not connected";

    SerialPort serialPort;

    public int selectedBaudRate;
    public String selectedPort;

    Boolean autoscroll;
    String command = "s1";
    int activeTransducer = 1;

//    public void setSelectedBaudRate(int value) {this.selectedBaudRate = value;}

    FXMLLoader loader = null;

    public CalibrationViewController() {
    }

    @FXML
    public void initialize() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                serialMonitorText.setEditable(false);

//                loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
//                try {
//                    loader.load();
//                } catch (IOException e) {
//                    System.out.println(e);
//                }
//                mainApp = loader.getController();
//                mainApp = new MainViewController();
                System.out.println("Selected baudrate = " + selectedBaudRate);
                autoscroll = checkAutoScroll.isSelected();

                set1.setOnAction(event -> click_Set(set1));
                set2.setOnAction(event -> click_Set(set2));
                set3.setOnAction(event -> click_Set(set3));
                set4.setOnAction(event -> click_Set(set4));


                for (int i = 0; i<4; i++) {
                    String keyCalibration = "calibration" + (i+1);
                    calibrationValues = Arrays.copyOf(calibrationValues, calibrationValues.length+1);
                    calibrationValues[i] = preferences.getDouble(keyCalibration, 375.0);;
                }

                text1.setText(calibrationValues[0].toString());
                text2.setText(calibrationValues[1].toString());
                text3.setText(calibrationValues[2].toString());
                text4.setText(calibrationValues[3].toString());

            }
        });



    }

    @FXML
    public void toggleAutoScroll() {
        autoscroll = !autoscroll;
    }

    @FXML
    public void click_NumTransducers() {
        if (radioTransducer1.isSelected()) {
            command = "s1";
            activeTransducer = 1;
        } else if (radioTransducer2.isSelected()) {
            command = "s2";
            activeTransducer = 2;
        } else if (radioTransducer3.isSelected()) {
            command = "s3";
            activeTransducer = 3;
        } else if (radioTransducer4.isSelected()) {
            command = "s4";
            activeTransducer = 4;
        }
    }

    @FXML
    public void click_Set(Button button) {
        String buttonID = button.getId();

        Pattern pattern = Pattern.compile("set(\\d)");
        Matcher matcher = pattern.matcher(buttonID);
        matcher.matches();

        int idNo = Integer.parseInt(matcher.group(1));
        Scene scene = button.getScene();
        String selectorText = "#text" + idNo;
        TextField textField = (TextField) scene.lookup(selectorText);
        System.out.println("text field value = " + textField.getText());

        // Check
        calibrationValues[idNo-1] = Double.valueOf(textField.getText());

        String keyCalibration = "calibration" + (idNo);
        preferences.putDouble(keyCalibration, calibrationValues[idNo-1]);

    }

    public void click_Connect() {

        if (serialPort != null && serialPort.isOpened()) {
//            if (portConnected) {
            if (!portConnected) return;
            buttonConnect.setText(CONNECT);
            labelStatusNow.setText(DISCONNECTING);
            portConnected = false;

            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    System.out.println("serialPort.isOpened() value: " + serialPort.isOpened());
                    System.out.println("Closing port");
                    try {
                        serialPort.closePort();
                    } catch (SerialPortException e) {
                        System.out.println(e);
                    }
                    System.out.println("Port closed port");
                    portConnected = false;
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                labelStatusNow.setText(NOTCONNECTED);
                labelStatusNow.setId("notConnected");
            });
            new Thread(task).start();
//            new Thread(() -> {
//
//            }).start();
//                return;
        } else {
            if (portConnected) return;
            buttonConnect.setText(DISCONNECT);
            labelStatusNow.setText(CONNECTING);
            portConnected = true;
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    if (selectedPort == "") return null;
                    serialPort = new SerialPort(selectedPort);
                    try {
                        serialPort.openPort();
                        System.out.println("Port opened: " + serialPort.isOpened());
                        System.out.println("Port name: " + serialPort.getPortName());


                        serialPort.setParams(selectedBaudRate,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
                        int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
                        serialPort.setEventsMask(mask);//Set mask
                        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                                SerialPort.FLOWCONTROL_RTSCTS_OUT);


//                int mask = SerialPort.MASK_RXCHAR;
//                //Set the prepared mask
//                serialPort.setEventsMask(mask);
                        //Add an interface through which we will receive information about events
                        serialPort.addEventListener(
                                new SerialPortReader(buffer -> Platform.runLater(() -> {
                                    int length = serialMonitorText.getCaretPosition();
                                    serialMonitorText.appendText(buffer);
                                    if (!autoscroll) {
                                        serialMonitorText.selectPositionCaret(length);
                                        serialMonitorText.deselect();
                                    }
                                })),
                                SerialPort.MASK_RXCHAR);

                    } catch (SerialPortException e) {
                        System.out.println(e);
                    }
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                labelStatusNow.setText(CONNECTED);
                labelStatusNow.setId("connected");
//                try {
//                    serialPort.writeString("adfasdfa");
//                    byte[] buffer = new byte[]{67,123,65,78};
//                    serialPort.writeBytes(buffer);
//                } catch (SerialPortException serialPortException) {
//                    System.out.println(serialPortException);
//                }
//                labelStatusNow.setStyle("-fx-background-color: #ADFF2F");
            });
            new Thread(task).start();


        }



    }

    @FunctionalInterface
    interface Function6<One, Two, Three, Four, Five, Six> {
        public Six apply(One one, Two two, Three three, Four four, Five five);
    }
    @FunctionalInterface
    interface Function2<One, Two> {
        public Two apply(One one, Two two);
    }

    private class SerialPortReader implements SerialPortEventListener {
        private final Consumer<String> textHandler;

        SerialPortReader(Consumer<String> textHandler) {
            this.textHandler = textHandler;
        }

        SerialPort serialPortToRead = serialPort;

        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {
                try {
                    byte[] buf = serialPortToRead.readBytes();
                    if (buf == null || buf.length <= 0) {
                        return;
                    }

                    String buffer = new String(buf, 0, buf.length);

//                    textHandler.accept(buffer);

                    try {
                        buffer = buffer.replaceAll("(\\r|\\n)", "");

                        if (buffer.equals("Ready")) {
                            System.out.println("Serial ready");
                            serialPortToRead.writeString(command);
                            return;
                        }
                        Double number = Double.valueOf(buffer);
                        DecimalFormat df = new DecimalFormat("0.00");
                        df.setRoundingMode(RoundingMode.UP);
                        if (number != null) {
                            number = number/ calibrationValues[activeTransducer-1];
                                    buffer = df.format(number) + "\n";
//                            buffer = number.toString() + "\n"
                        }
//                        buffer = "asd"
                        textHandler.accept(buffer);

                    } catch (NumberFormatException e) {
                        System.out.println(e);
                    }

                } catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }

    }



}
