/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers;

import connections.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author cmeehan
 */
public class Client {

    public Client() {
    }

    /**
     * Here we are going to check to see if the client exists. If the client
     * exists we will return true. If the client does not exist we will return
     * false.
     *
     * @param client
     * @return boolean
     */
    public boolean clientExists(String client) {
        boolean exists = false;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT COUNT(ID) AS 'COUNT' FROM ALL_CUSTOMERS WHERE COMPANY_NAME = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, client);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("COUNT") > 0) {
                    exists = true;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return exists;
    }

    public String getClientName(String id){
        Connection conn = new DBConnection().connect();
         String sql = "SELECT CONCAT(PRIMARY_CONTACT_FIRST_NAME, ' ', PRIMARY_CONTACT_LAST_NAME) AS 'NAME' FROM ALL_CUSTOMERS WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("NAME");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    /**
     * Get the client's email address
     * @param id - the client id
     * @return 
     */
    public String getClientEmail(String id){
        Connection conn = new DBConnection().connect();
        String sql = "SELECT EMAIL_ADDRESS FROM ALL_CUSTOMERS WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("EMAIL_ADDRESS");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
    /**
     * Gets the client's telephone number in order of: 
     * mobile, home, work
     * @param id - the client id
     * @return 
     */
    public String getClientTel(String id){
        Connection conn = new DBConnection().connect();
        String sql = "SELECT IF(MOBILE_PHONE != '', MOBILE_PHONE, IF(HOME_PHONE != '', HOME_PHONE, IF(WORK_PHONE != '', WORK_PHONE, 'N/A'))) AS 'PHONE' FROM ALL_CUSTOMERS WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("PHONE");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    /**
     * Get the client's URL
     * @param id
     * @return 
     */
     public String getClientURL(String id){
        Connection conn = new DBConnection().connect();
        String sql = "SELECT WEBSITE_URL FROM ALL_CUSTOMERS WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("WEBSITE_URL");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
     
      /**
     * Get the client's Web Host
     * @param id
     * @return 
     */
     public String getClientHost(String id){
        Connection conn = new DBConnection().connect();
        String sql = "SELECT WEB_HOST FROM ALL_CUSTOMERS WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("WEB_HOST");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
     
       /**
     * Get the whether the client is an individual or a business
     * A business can be a single person business. It is more reflective of
     * whether or not the web site/app is for personal or business use.
     * @param id
     * @return 
     */
     public String getClientType(String id){
        Connection conn = new DBConnection().connect();
        String sql = "SELECT IF(INDIVIDUAL_TYPE = 1, 'Individual', 'Business') AS 'CLIENT_TYPE' FROM ALL_CUSTOMERS WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("CLIENT_TYPE");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }
    
    /**
     * Here we are going to fetch the client ID based on their registered name.
     *
     * @param client
     * @return
     */
    public String clientID(String client) {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID AS 'ID' FROM ALL_CUSTOMERS WHERE COMPANY_NAME = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, client);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return String.valueOf(rs.getInt("ID"));
            }else{
                return "None";
            }
            
        } catch (SQLException ex) {
            System.out.println("Error getting client ID: " + ex.getMessage());
            System.out.println(ex.getCause());
            return "Error";
        }
    }

    /**
     *
     * @param client
     * @return boolean
     */
    public int createClient(String client) {
        Connection conn = new DBConnection().connect();
        String sql = "INSERT INTO ALL_CUSTOMERS(COMPANY_NAME) VALUES(?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, client);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error creating client");
            System.out.println(ex.getMessage());
            return 0;
        }
    }

    public ObservableList<String> clientList() {
        List<String> list = new ArrayList();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT COMPANY_NAME FROM ALL_CUSTOMERS ORDER BY COMPANY_NAME ASC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    list.add(rs.getString("COMPANY_NAME"));
                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.out.println("Error getting client list");
            System.out.println(ex.getMessage());
        }
        ObservableList<String> clientList = FXCollections.observableArrayList(list);
        return clientList;
    }

    public String getClient(String clientID) {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT COMPANY_NAME FROM ALL_CUSTOMERS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(clientID));
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
               return rs.getString("COMPANY_NAME"); 
            }else{
                return "None";
            }
        } catch (SQLException ex) {
            System.out.println("Error getting client");
            System.out.println(ex.getMessage());
            return "Error";
        }
    }
}
