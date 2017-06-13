/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package users;

import connections.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import main.Main;

/**
 *
 * @author cmeehan
 */
public class Users {

    /**
     * This method will sign the user in using their username and password. 
     * It will then store the user ID in the main class. 
     * 
     * @param username
     * @param password
     * @return userID
     * @throws Exception 
     */
    public String SignIn(String username, String password) throws Exception {
        Connection conn = new DBConnection().connect();
        String SQL = "SELECT ID AS 'ID' FROM ALL_USERS WHERE USERNAME = ? AND PASSWORD = MD5(?)";
        try {
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Main.userID = rs.getString("ID");
                Main.isSignedIn = true;
                return rs.getString("ID");
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Log In Error");
                alert.setHeaderText("Error Loggin In");
                alert.setContentText("The username and password do not match what we have on record.");
                alert.showAndWait();
            }
            ps.close();
        } catch (SQLException ex) {
            return "Error";
        } finally {
            conn.close();
        }
        return null;
    }

    public ObservableList<String> getUsers() {
        List<String> list = new ArrayList();
        ObservableList<String> users = null;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT CONCAT(FIRST_NAME, ' ', LAST_NAME) AS 'NAME' FROM ALL_USERS";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    list.add(rs.getString("NAME"));
                } while (rs.next());
            }
            users = FXCollections.observableArrayList(list);
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.getMessage();
            }
        }
        return users;
    }

    /**
     * Get the user ID by the name. Must have first and last name.
     *
     * @param name
     * @return
     */
    public String getUserIDByName(String name) {
        Connection conn = new DBConnection().connect();
        String userID = null;
        String sql = "SELECT ID FROM ALL_USERS WHERE CONCAT(FIRST_NAME, ' ', LAST_NAME) = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userID = rs.getString("ID");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("DB Error");
            alert.setHeaderText("Database Error");
            alert.setContentText("There was an error with the database. Please contact your systems administrator to have this resolved.");
            alert.showAndWait();
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return userID;
    }

    /**
     * Get the user ID by the username.
     *
     * @param name
     * @return
     */
    public String getUserIDByUserName(String name) {
        Connection conn = new DBConnection().connect();
        String userID = null;
        String sql = "SELECT ID FROM ALL_USERS WHERE USERNAME = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userID = rs.getString("ID");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("DB Error");
            alert.setHeaderText("Database Error");
            alert.setContentText("There was an error with the database. Please contact your systems administrator to have this resolved.");
            alert.showAndWait();
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return userID;
    }

    public String getNameByID(String id) {
        String user = null;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT CONCAT(FIRST_NAME, ' ', LAST_NAME) AS 'NAME' FROM ALL_USERS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = rs.getString("NAME");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("DB Error");
            alert.setHeaderText("Database Error");
            alert.setContentText("There was an error with the database. Please contact your systems administrator to have this resolved.");
            alert.showAndWait();
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return user;
    }
}
