/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accounting;

import connections.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import main.Main;

/**
 *
 * @author cmeehan
 */
public class Credit {

    /**
     * Record a new transaction
     * 
     * @param type
     * @param amount
     * @param status
     * @param notes
     * @param dateCreated
     * @param invoiceId
     * @param paymentMethod
     * @param receivedFrom
     * @param transactionIdLabel
     * @throws SQLException 
     */
    public void insertNewCredit(String type, String amount, String status, String notes, String dateCreated, String invoiceId, String paymentMethod, String receivedFrom, Label transactionIdLabel) throws SQLException {
        Connection conn = new DBConnection().connect();

        String sql = "INSERT INTO TRANSACTIONS (VERSION, TYPE, AMOUNT, STATUS, NOTES, DATE_CREATED, USER_ID, INVOICE_ID, PAYMENT_METHOD, RECEIVED_FROM) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, "1");
            ps.setString(2, type);
            ps.setString(3, amount);
            ps.setString(4, status);
            ps.setString(5, notes);
            ps.setString(6, dateCreated);
            ps.setString(7, Main.userID);
            ps.setString(8, invoiceId);
            ps.setString(9, paymentMethod);
            ps.setString(10, receivedFrom);
            int rs = ps.executeUpdate();
            ResultSet key = ps.getGeneratedKeys();
            
            // Notify the user whether or not the payment was received by the 
            // database. 
            Alert alert = new Alert(AlertType.CONFIRMATION);
            if (key.next()) {
                alert.setTitle("Payment Received");
                alert.setHeaderText("Payment successfully saved");
                alert.setContentText("The payment has been saved in the database.");
                alert.showAndWait();
                transactionIdLabel.setText(String.valueOf(key.getInt(1)));
            }else{
                alert.setTitle("Payment Error");
                alert.setHeaderText("Payment not successful");
                alert.setContentText("The payment was not successfully saved. Please try again.");
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }

    }

    public void updateExistingCredit(String... args) {

    }

}
