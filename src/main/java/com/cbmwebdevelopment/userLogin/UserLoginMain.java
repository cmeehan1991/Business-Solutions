/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.userLogin;

import com.cbmwebdevelopment.connections.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class UserLoginMain extends Application {
    public UserLoginMain(){
        
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
       Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/UserLoginFXML.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/GlobalStyle.css");
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("resources/web_icon.png").toString()));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Business Solutions");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
}
