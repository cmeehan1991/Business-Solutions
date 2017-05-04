/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import connections.DBConnection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import javafx.application.Application;
import javafx.stage.Stage;
import userLogin.UserLoginMain;

/**
 *
 * @author cmeehan
 */
public class Main extends Application {
    public HashMap<String, Boolean> DATABASES;
    private Connection conn = new DBConnection().connect();

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        InetAddress IP = InetAddress.getLocalHost();        
        String userHome = System.getProperty("user.home");
        System.out.println(userHome + " " + IP);
        new UserLoginMain().start(new Stage());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
