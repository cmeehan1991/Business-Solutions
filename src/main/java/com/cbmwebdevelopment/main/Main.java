/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.main;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.cbmwebdevelopment.navigation.NavigationMain;

/**
 *
 * @author cmeehan
 */
public class Main extends Application {
    public static boolean isSignedIn;
    public static String userID;
    public static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(Locale.US);
    private NavigationMain navigationMain;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        if(Main.isSignedIn){
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/NavigationFXML.fxml"));
            Parent root = (Parent) loader.load();
            Scene scene = new Scene(root);
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Business Solutions");
            primaryStage.show();
        }else{   
           FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/UserLoginFXML.fxml"));
            Parent root = (Parent) loader.load();
            Scene scene = new Scene(root);
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sign In");
            primaryStage.show();
        }
        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
