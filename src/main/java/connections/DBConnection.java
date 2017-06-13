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
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException ex){
            System.out.println("Class error: " + ex.getMessage());
        }
        
        /**
        * This section will establish the connection to the database. 
        * It is important to remember to change the database login information for new clients. 
        * 
        * Required information: URL, Username, Password
        */
        String URL = "jdbc:mysql://localhost/business_solutions?JDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"; 
        String username = "root";
        String password = "root";
        try{
            connection = (Connection)DriverManager.getConnection(URL, username, password);
        }catch(SQLException ex){
            System.out.println("Connection Error:");
            System.out.println(ex.getMessage());
        }
        return connection;
    }
}
