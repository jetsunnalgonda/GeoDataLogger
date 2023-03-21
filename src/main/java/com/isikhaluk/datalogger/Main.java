package com.isikhaluk.datalogger;

import de.jangassen.MenuToolkit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;


public class Main extends Application {

//    private MainViewController controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        setIconImage(ImageIO.read(getClass().getResourceAsStream("/images/world.png")));
//        System.setProperty("apple.laf.useScreenMenuBar", "true");
//        System.setProperty("apple.laf.useScreenMenuBar", "false");
//        Image image = new Image(getClass().getResource("Transducer.png").toExternalForm());
//        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("Transducer.png")));
//        primaryStage.getIcons().add(new Image("/Transducer.png"));

//        MenuToolkit tk = MenuToolkit.toolkit();
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Debug information");
//        alert.setHeaderText("Application");
//        alert.setContentText("Application Started");
//        alert.showAndWait();

        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("/images/Transducer.png"))));
        Parent root = FXMLLoader.load(getClass().getResource("/splash.fxml"));
        primaryStage.setTitle("Geo Data Logger");
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

//        controller = loader.<MainViewController>getController();

        Platform.setImplicitExit(true);
        primaryStage.setOnCloseRequest((ae) -> {
            Platform.exit();
            System.exit(0);
        });
    }


    public static void main(String[] args) {
//        JOptionPane.showMessageDialog(null, "main method");
        launch(args);
    }
    /**
     * This method is called when the application should stop,
     * and provides a convenient place to prepare for application exit and destroy resources.
     */
//    @Override
//    public void stop() throws Exception
//    {
//        super.stop();
//        if(controller != null)
//        {
//            controller.startHousekeeping();
//        }
//
//        Platform.exit();
//        System.exit(0);
//    }
}

