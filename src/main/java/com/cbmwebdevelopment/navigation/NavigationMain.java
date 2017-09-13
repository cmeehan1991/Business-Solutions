/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.navigation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import com.cbmwebdevelopment.main.Main;

/**
 *
 * @author cmeehan
 */
public class NavigationMain extends Application {
    final String OS = System.getProperty("os.name");
    public String USER_ID;

    public NavigationMain(String userID) {
        this.USER_ID = userID;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/NavigationFXML.fxml"));
        Parent root = (Parent) loader.load();
        NavigationController controller = (NavigationController) loader.getController();
        controller.userID = this.USER_ID;
            
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/GlobalStyle.css");
        scene.getStylesheets().add("styles/NavigationStyle.css");
        
        stage.setResizable(false);
        stage.setY(10.00);
        stage.setX(screenWidth() - stage.getWidth() / 2);
        stage.setTitle("Business Solutions");
        stage.setScene(scene);
        stage.show();
        
        stage.setOnCloseRequest((WindowEvent event)->{
            Platform.exit();
        });
    }
    /**
     * Get the width of the screen to set the location of the navigation box.
     * 
     * @return double
     */
    private double screenWidth() {
        double width;
        Rectangle2D rectangle = Screen.getPrimary().getBounds();
        width = rectangle.getWidth();
        return width;
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
