package com.isikhaluk.datalogger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

public class Splash {

    @FXML AnchorPane rootPane;

    @FXML
    public void initialize() {
        new SplashScreen().start();
    }

    class SplashScreen extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1500);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Parent root = null;
                        FXMLLoader loader = null;
                        try {
//                            root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
                            loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
                            root = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        rootPane.getScene().getWindow().hide();

                        Stage primaryStage = new Stage();
                        Scene mainScene = new Scene(root);
                        primaryStage.setTitle("Geo Data Logger");
                        mainScene.getStylesheets().add(String.valueOf(getClass().getResource("/MainStyle.css")));
                        mainScene.getStylesheets().add(getClass().getResource("/MainStyle.css").toExternalForm());
//                        primaryStage.getIcons().add(new Image("Transducer.png"));
//                        primaryStage.getIcons().add(new Image("Transducer.png"));
                        primaryStage.setScene(mainScene);
                        primaryStage.show();

                        Platform.setImplicitExit(true);
                        FXMLLoader finalLoader = loader;
                        primaryStage.setOnCloseRequest((ae) -> {
                            MainViewController mainApp = finalLoader.getController();
                            mainApp.logData.saveLog();
                            mainApp.logData.closeLog();
                            Platform.exit();
                            System.exit(0);
                        });
//                        primaryStage.requestFocus();
//                        rootPane.getScene().getWindow().requestFocus();

                    }
                });


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
