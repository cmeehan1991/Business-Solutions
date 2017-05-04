/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userLogin;

import connections.DBConnection;
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
       Parent root = FXMLLoader.load(getClass().getResource("UserLoginFXML.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/GlobalStyle.css");
        primaryStage.setResizable(false);
        primaryStage.setTitle("Business Solutions");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public String SignIn(String username, String password) throws Exception{
        Connection conn = new DBConnection().connect();
        String SQL = "SELECT ID AS 'ID' FROM ALL_USERS WHERE USERNAME = ? AND PASSWORD = MD5(?)";
        try{
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("ID");
            }else{
               Alert alert = new Alert(AlertType.INFORMATION);
               alert.setTitle("Log In Error");
               alert.setHeaderText("Error Loggin In");
               alert.setContentText("The username and password do not match what we have on record.");
               alert.showAndWait();
               return null;
            }
        }catch(SQLException ex){
            return "Error";
        }
    }
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
}
