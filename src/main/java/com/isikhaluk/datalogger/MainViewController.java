package com.isikhaluk.datalogger;

import de.jangassen.MenuToolkit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jssc.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.prefs.Preferences;


public class MainViewController {
    @FXML VBox vbox;

    @FXML MenuBar menubar;
    @FXML MenuItem menu_File_Close;
    @FXML RadioMenuItem radioTransducer1, radioTransducer2, radioTransducer3, radioTransducer4;

    @FXML Label label1, labelTransducerGraph1, labelStatusNow;
    @FXML TextArea serialMonitorText;

    @FXML ChoiceBox choiceBaudRate, choicePort;

    @FXML Button buttonConnect;

    @FXML Pane pane;
    FlowGridPane flowGridPane;
    @FXML LineChart lineGraph1;

//    public static final String SEPARATOR = " ";
//    private static final String LINE_SEPARATOR = "\r\n";
//    private final StringProperty line = new SimpleStringProperty("");

    public CalibrationViewController calibrationViewController = new CalibrationViewController();

    private String[] portNames;
    private int[] baudRates = new int[] {9600, 14400, 19200, 38400, 57600, 115200, 128000, 256000};

    public String test = "This is a test";

    private boolean portConnected = false;
    private static String CONNECT = "Connect";
    private static String DISCONNECT = "Disconnect";
    private static String CONNECTING = "Connecting...";
    private static String DISCONNECTING = "Disconnecting...";
    private static String CONNECTED = "Connected";
    private static String NOTCONNECTED = "Not connected";

    public String[] transducerNames;

    SerialPort serialPort;

    public LineChart lineChart1;
    public XYChart.Series series1;
    DataChart chart1;
    DataChart[] charts = new DataChart[0];
    int numberOfTransducers = 1;
    DataChart charts1, charts2, charts3, charts4;
    DemoChart chart0;
    ExcelLogData logData;

    Instant startTime;
    Boolean chartStarted = false;
    Thread excelThread;

    Boolean skipInitialization = false;

    Preferences preferences = Preferences.userNodeForPackage(CalibrationViewController.class);
    Double[] calibrationValues = new Double[0];

    public MainViewController(Boolean skipInitialization) {
//        label1.setText("asdf");
//        menubar.useSystemMenuBarProperty().set(true);

    }
    public MainViewController() {
//        label1.setText("asdf");
//        menubar.useSystemMenuBarProperty().set(true);

    }


    @FXML
    protected void initialize() {

        menubar.useSystemMenuBarProperty().set(true);
//        Platform.runLater(() -> menubar.setUseSystemMenuBar(true));

        //NSMenuFX
//        AppMenu appMenu = new AppMenu();
//        MenuToolkit tk = MenuToolkit.toolkit();
        System.out.println("menubar = " + menubar);
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Debug information");
//        alert.setHeaderText("MenuToolkit");
//        alert.setContentText("menubar = " + menubar);
//        alert.showAndWait();



        flowGridPane = new FlowGridPane(2, 2);
        flowGridPane.prefHeightProperty().bind(pane.heightProperty());
        flowGridPane.prefWidthProperty().bind(pane.widthProperty());
//        pane.setStyle("-fx-background-color: #8b8a7c");
        pane.getChildren().add(flowGridPane);

        serialMonitorText.setEditable(false);
        // Fill in the choiceboxes
        portNames = SerialPortList.getPortNames();
        if (portNames.length>0) {
            for (String portName:portNames) choicePort.getItems().add(portName);
            System.out.println("First port name: " + portNames[0]);
            choicePort.setValue(portNames[0]);
        }
//        choicePort.setOnAction((event) -> {
//            System.out.println("Hyperlink Action");
//        });
        for (int baudRate:baudRates) choiceBaudRate.getItems().add(baudRate);
        choiceBaudRate.setValue(baudRates[5]);
        if (choicePort.getValue() != null)
            serialPort = new SerialPort(choicePort.getValue().toString());

        // Create the chart
//        createChart(lineChart1, series1);
//        chart0 = new DemoChart("Demo 1");

        // Get transducer names
        transducerNames = new String[]{"Transducer 1", "Transducer 2", "Transducer 3", "Transducer 4"};
//        chart1 = new DataChart("Transducer 1");
        // Get the number of transducers and create the charts
        createOrRemoveChart(1,0);

        // Create the local Excel log file
        try {
            logData = new ExcelLogData();
        } catch (IOException e) {
            System.out.println(e);
        }

        // User preferences
        updateCalibrations();


//        menubar.useSystemMenuBarProperty().set(true);



    }

    private void updateCalibrations() {
        for (int i=0; i<4;i++) {
            String keyCalibration = "calibration" + (i + 1);
            calibrationValues = Arrays.copyOf(calibrationValues, calibrationValues.length + 1);
            calibrationValues[i] = preferences.getDouble(keyCalibration, 375.0);
            System.out.println(" calibrationValues["+i+"] =" +  calibrationValues[i]);
        }
    }

    @FXML
    public void click_File_Close() {
        System.out.println("Menu clicked");
    }

    @FXML
    public void click_NumTransducers() {
        if (radioTransducer1.isSelected()) {
            createOrRemoveChart(1, numberOfTransducers);
            numberOfTransducers = 1;
        } else if (radioTransducer2.isSelected()) {
            createOrRemoveChart(2, numberOfTransducers);
            numberOfTransducers = 2;
        } else if (radioTransducer3.isSelected()) {
            createOrRemoveChart(3, numberOfTransducers);
            numberOfTransducers = 3;
        } else if (radioTransducer4.isSelected()) {
            createOrRemoveChart(4, numberOfTransducers);
            numberOfTransducers = 4;
        }
    }

    @FXML
    public void click_FlowPane() {
        System.out.println("Flow pane height: " + pane.getHeight());
        System.out.println("Flow pane width: " + pane.getWidth());
//        System.out.println(pane.prefRowsProperty());
    }

    @FXML
    public void click_Calibrate() throws SerialPortException {
        System.out.println("Calibration");
        Parent root = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CalibrationView.fxml"));
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Pass some variables to the other controller
        CalibrationViewController calibrationViewController = loader.getController();
        calibrationViewController.selectedBaudRate = Integer.parseInt(choiceBaudRate.getValue().toString());
        updatePortList();
        if (portNames.length>0) {

            calibrationViewController.serialPort = serialPort;
            calibrationViewController.selectedPort = choicePort.getValue().toString();

            Stage calibrationStage = new Stage();
            calibrationStage.initModality(Modality.APPLICATION_MODAL);
            Scene calibrationScene = new Scene(root);

            calibrationStage.setScene(calibrationScene);
            calibrationStage.setTitle("Calibration");
            calibrationStage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText("Serial Port Not Connected");
            alert.setContentText("Make sure the device is connected and try again.");
            alert.showAndWait();
        }


//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        });


    }

    @FXML
    public void click_Connect() {
//        if(!portConnected) {
//            connect();
//            return;
//        }
//        System.out.println(choicePort.getValue().toString());
//        SerialPort serialPortToConnect = new SerialPort(choicePort.getValue().toString());
//        serialPort = serialPortToConnect;
//        System.out.println("Connecting to port " + serialPort.getPortName() + " ...");
//        System.out.println("ChoiceBox value: " + choicePort.getValue().toString());


        //            System.out.println("try serial port: " + serialPort.getPortName());
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
                    logData.saveLog();
                    logData.closeLog();
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
            createOrRemoveChart(1, numberOfTransducers);
            createOrRemoveChart(numberOfTransducers, 1);
            updateCalibrations();
            try {
                logData = new ExcelLogData();
            } catch (IOException e) {
                System.out.println("Cannot get new ExcelLogData");
                e.printStackTrace();
            }

            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
//                    logData.createLog();
                    int i = 0;
                    for (DataChart chart:charts) {
                        chart.series.getData().clear();
                        logData.createLog(i, transducerNames[i], "Time (s)", "Force (kN)");
                        i++;
                    }

                    chartStarted = false;
                    if (choicePort.getValue().toString() == "") return null;
                    serialPort = new SerialPort(choicePort.getValue().toString());
                    try {
                        serialPort.openPort();
                        System.out.println("Port opened: " + serialPort.isOpened());
                        System.out.println("Port name: " + serialPort.getPortName());


                        serialPort.setParams(Integer.parseInt(choiceBaudRate.getValue().toString()),
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
                                new SerialPortReader(buffer -> Platform.runLater(() -> serialMonitorText.appendText(buffer))),
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

//            new Thread(() -> {
//
//            }).start();
//                Runnable task = new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            logData = new ExcelLogData();
//                            logData.createLog();
//                        } catch (IOException e) {
//                            System.out.println(e);
//                        }
//                    }
//                    void write() {
//                        logData.writeLog();
//                    }
//                };
//                excelThread = new Thread(task);
//                excelThread.start();
//                excelThread.
        }

        //        buttonConnect.setText(DISCONNECT);
//        if (portConnected) {
//            buttonConnect.setText(CONNECT);
//        }

    }

    private class SerialPortReader implements SerialPortEventListener {
        private final Consumer<String> textHandler;

        SerialPortReader(Consumer<String> textHandler) {
            this.textHandler = textHandler ;
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
//                    System.out.println(buf);
//                    CharBuffer charBuffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(buf));
//                    System.out.println(charBuffer);
                    String buffer = new String(buf, 0, buf.length);
//                    System.out.println(buffer);
//                    String buffer = serialPortToRead.readString();
//                    textHandler.accept(buffer);

//                    System.out.println("isInteger(buffer) = " + isInteger(buffer));
//                    System.out.println("buffer = " + buffer);
                    try {
                        buffer = buffer.replaceAll("(\\r|\\n)", "");
//                        System.out.println("adfgdsafadfasdfasdfasdf");
//                        System.out.println(buffer);
//                        System.out.println("A"+buffer+"B");
                        if (buffer.equals("Ready")) {
                            System.out.println("Serial ready");
                            serialPortToRead.writeString("r");
                            return;
                        }
                        String[] yValues = buffer.split("\t");
                        Double[] y = new Double[yValues.length];

//                        int[] y = new int[yValues.length];
//                        System.out.println("adfgdsafadfasdfasdfasdf");
                        for (int i = 0; i<yValues.length; i++) {
                            y[i] = Double.valueOf(Math.round(Double.parseDouble(yValues[i])));
//                            y[i] = Integer.parseInt(yValues[i]);
                        }
//                        if (y[0] > 40) {
//                            serialPortToRead.writeString("s1");
//                        }
//                        System.out.println("adfgdsafadfasdfasdfasdf");
//                        System.out.println("y[0] = " + y[0]);
//                        int y = Integer.parseInt(buffer);
                        int x = 0;
                        if (!chartStarted) {
                            chartStarted = true;
                            startTime = Instant.now();
                        } else {
                            x = (int) Duration.between(startTime, Instant.now()).toMillis() / 100;
                        }
//                        System.out.println("x = " + x + ", y = " + y);
                        Double finalX = ((double) x )/ 10.0;
                        int i = 0;
                        if (charts != null) {
                            for (DataChart chart:charts) {
                                y[i] = -y[i]/calibrationValues[i];
                                int finalI = i;
                                int finalI1 = i;
                                int finalI2 = i;
                                Platform.runLater(() -> charts[finalI1].series.getData().add(new XYChart.Data(finalX, -y[finalI1])) );
                                Platform.runLater(() -> logData.writeLog(finalI, new Object[]{finalX, y[finalI2]}));
                                i++;
                            }
                        }
                        System.out.println("yValues = " + yValues);
                        buffer = "";
                        for (i=0;i<yValues.length;i++) {
                            Double number = Double.valueOf(yValues[i]);
                            DecimalFormat df = new DecimalFormat("0.00");
                            df.setRoundingMode(RoundingMode.UP);
                            System.out.println(" calibrationValues["+i+"] =" +  calibrationValues[i]);
                            System.out.println("number = "+ number);
                            if (number != null) {
                                if (calibrationValues[i] != null) {
                                    if (calibrationValues[i] != 0.0) {
                                        number = number / calibrationValues[i];
                                    }
                                }
//                            buffer = number.toString() + "\n";
                            }

                            buffer = buffer + df.format(number) + "\t";

//                            System.out.println("buffer = " + buffer);
                        }
                        buffer += "\n";
                        textHandler.accept(buffer);

                    } catch (NumberFormatException e) {
                        System.out.println(e);
                    }

//                    System.out.println("chart 1 label: " + chart1.xAxis.getLabel());
//                    chart1.lineChart.getData().add(chart1.series);
//                    drawChart(buffer);
                } catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }

    }

    private class FlowChart extends Application {
        @Override
        public void start(Stage primaryStage) {

            Double[] data = {0.1, 0.4, 0.5, 0.7, 0.9, 1.0};

            LineChart<Number, Number> lc = createLineChart(data);
            LineChart<Number, Number> lc1 = createLineChart(data);
            LineChart<Number, Number> lc2 = createLineChart(data);

            Pane root = new Pane();
            root.getChildren().addAll(lc, lc1, lc2);

            Scene scene = new Scene(root, 800, 600);

            primaryStage.setTitle("Hello World!");
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        /**
         * @param args the command line arguments
         */
        public void main(String[] args) {
            launch(args);
        }

        private LineChart<Number, Number> createLineChart(Double[] axisValues) {
            //defining the axes
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Time");
            //creating the chart
            final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

            lineChart.setTitle("Axis' values");
            //defining a series
            XYChart.Series<Number, Number> series = new LineChart.Series<>();
            series.setName("X Axis");
            //populating the series with data
            for (int i = 0; i < axisValues.length; i++) {
                XYChart.Data<Number, Number> data = new LineChart.Data<>(i, axisValues[i]);
                series.getData().add(data);
            }
            lineChart.getData().add(series);
            return lineChart;
        }
    }

    private class DataChart {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number,Number> lineChart;
        XYChart.Series series;

        public DataChart(String seriesName) {
            xAxis.setLabel("Time (s)");
            yAxis.setLabel("Force (kN)");
            lineChart = new LineChart<Number,Number>(xAxis,yAxis);
            series = new XYChart.Series();
            series.setName(seriesName);
            Platform.runLater(() -> {
                lineChart.getData().add(series);
                lineChart.prefWidthProperty().bind(flowGridPane.widthProperty());
                lineChart.prefHeightProperty().bind(flowGridPane.heightProperty());
//                lineChart.setStyle("-fx-background-color: #ff2200");

//                Button newPane = new Button();
//                newPane.setStyle("-fx-background-color: black;");
//                newPane.setStyle("-fx-border-color: black;");
//                newPane.setStyle("-fx-border-width: 2px;");
////                newPane.setMinSize(200,200);
//                newPane.prefHeightProperty().bind(flowGridPane.heightProperty());
//                newPane.prefWidthProperty().bind(flowGridPane.widthProperty());

//                flowGridPane.setStyle("-fx-background-color: black;");
                flowGridPane.getChildren().addAll(lineChart);
//                lineChart.setMinSize(800,800);
//                lineChart.autosize();
//                pane.getChildren().add(lineChart);
            });


//            try {
//                logData = new ExcelLogData();
//                logData.createLog();
//            } catch (IOException e) {
//                System.out.println(e);
////                e.printStackTrace();
//            }


        }

    }

    public void removeLast() {
        int i = 0;
        for (Node node: flowGridPane.getChildren()) {
            i++;
            System.out.println("child node: " + node);
        }

        if (i>0)
            flowGridPane.getChildren().remove(i-1);
    }

    public void createOrRemoveChart(@NamedArg("numberOfTransducers")int newVal,
                                    @NamedArg("numberOfTransducersBefore")int oldVal) {
        System.out.println("newVal = " + newVal);
        System.out.println("oldVal = " + oldVal);
        if (newVal>oldVal) {
            for (int i = oldVal; i < newVal; i++) {
//                ArrayUtils.add(charts, new DataChart(transducerNames[i]));
//                int newLength = 0;
//                if (charts == null)
//                    newLength = 1;
//                else
//                    newLength = charts.length + 1;

                charts = Arrays.copyOf(charts, charts.length + 1);

                System.out.println("charts.length = " + charts.length);
                charts[i] = new DataChart(transducerNames[i]);
            }
        } else {
            for (int i = newVal; i < oldVal; i++) {
                charts = ArrayUtils.remove(charts, oldVal-i);
                removeLast();
                System.out.println("removed index: " + (oldVal-i));
            }
        }
    }

    private class DemoChart {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number,Number> lineChart;
        XYChart.Series series;

        public DemoChart(String seriesName) {
            xAxis.setLabel("Time (s)");
            yAxis.setLabel("Force (kN)");
            lineChart = new LineChart<Number,Number>(xAxis,yAxis);
            series = new XYChart.Series();
            series.setName(seriesName);
            //        XYChart.Series series = new XYChart.Series();
            series.getData().add(new XYChart.Data(1, 13.2));
            series.getData().add(new XYChart.Data(2, 7.9));
            series.getData().add(new XYChart.Data(3, 15.3));
            series.getData().add(new XYChart.Data(4, 20.2));
            series.getData().add(new XYChart.Data(5, 35.7));
            series.getData().add(new XYChart.Data(6, 103.8));
            series.getData().add(new XYChart.Data(7, 169.9));
            series.getData().add(new XYChart.Data(8, 178.7));
            series.getData().add(new XYChart.Data(9, 158.3));
            series.getData().add(new XYChart.Data(10, 97.2));
            series.getData().add(new XYChart.Data(11, 22.4));
            series.getData().add(new XYChart.Data(12, 5.9));
            Platform.runLater(() -> {
                lineChart.getData().add(series);
                pane.getChildren().addAll(lineChart);
                lineChart.prefWidthProperty().bind(pane.widthProperty());
                lineChart.prefHeightProperty().bind(pane.heightProperty());
//                pane.getStylesheets().add("MainStyle.css");
            });



        }

    }

    public void updatePortList() throws SerialPortException {
//        serialPort.writeString("r");
        System.out.println("Update port list");
        portNames = SerialPortList.getPortNames();
//        portNames = new String[]{"/dev/cu.wchusbserial1420"};
        choicePort.getItems().clear();
        if (portNames.length>0) {
            for (String portName:portNames) choicePort.getItems().add(portName);
            System.out.println("First port name: " + portNames[0]);
            choicePort.setValue(portNames[0]);
//            choicePort.setValue("/dev/cu.wchusbserial1420");
        }
//        serialPort = new SerialPort(choicePort.getValue().toString());
    }

    private void createChart(LineChart lineChart, XYChart.Series series) {
//        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time (s)");
        yAxis.setLabel("Force (kN)");

//        final LineChart<Number,Number> lineChart =
        lineChart = new LineChart<Number,Number>(xAxis,yAxis);

        series = new XYChart.Series();
        series.setName("Transducer 1");

        lineChart.getData().add(series);

        pane.getChildren().addAll(lineChart);

    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        str = str.replaceAll("(\\r|\\n)", "");

        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }


}
