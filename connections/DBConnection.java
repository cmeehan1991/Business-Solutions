/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author cmeehan
 * 
 * This class is used to establish a connection to the MySql database. 
 * Be sure to change the connection logic when deploying this software to clients. 
 * 
 */
public class DBConnection {

    private Connection connection;

    public Connection connect() {
        try{
            Class.forName("com.mysql.jdbc.Drive");
        }catch(ClassNotFoundException ex){
            System.out.println(ex.getMessage());
        }
        
        /**
        * This section will establish the connection to the database. 
        * It is important to remember to change the database login information for new clients. 
        * 
        * Required information: URL, Username, Password
        */
        String URL = "jdbc:mysql://192.185.35.244/cmeehan_business_solutions"; 
        String username = "cmeehan_dev";
        String password = "Mia2016!";
        try{
            connection = (Connection)DriverManager.getConnection(URL, username, password);
        }catch(SQLException ex){
            System.out.println("Connection Error:");
            System.out.println(ex.getMessage());
        }
        return connection;
    }
}
