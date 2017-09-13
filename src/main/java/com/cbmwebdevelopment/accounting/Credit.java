/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.connections.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.cbmwebdevelopment.main.Main;

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
            } else {
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

    /**
     * This will get the existing account credit based on the transaction ID and
     * will fill in the respective parameters in the window.
     *
     * @param transactionId
     * @param controller
     */
    public void getExistingCredit(String transactionId) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT VERSION, TYPE, AMOUNT, STATUS, NOTES, DATE_CREATED, INVOICE_ID, PAYMENT_METHOD, RECEIVED_FROM FROM TRANSACTIONS WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(transactionId));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CreditMain creditMain = new CreditMain();
                creditMain.transactionId = transactionId;
                creditMain.amount = rs.getString("AMOUNT");
                creditMain.status = rs.getString("STATUS");
                creditMain.notes = rs.getString("NOTES");
                creditMain.dateCreated = rs.getString("DATE_CREATED");
                creditMain.paymentMethod = rs.getString("PAYMENT_METHOD");
                creditMain.receivedFrom = rs.getString("RECEIVED_FROM");
                creditMain.appliedTo = rs.getString("INVOICE_ID");
                creditMain.start(new Stage());
            }
        } catch (SQLException | IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
    }

    /**
     * Return the total amount invoiced to the customer. 
     * @param id
     * @return 
     */
    public Double getTotalAmountInvoiced(String id) {
        Double amountInvoiced = 0.0;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT INVOICES.CLIENT_ID, SUM(INVOICE_ITEMIZATION.COST * INVOICE_ITEMIZATION.COST_TIME) AS 'TOTAL INVOICED' FROM INVOICES INNER JOIN INVOICE_ITEMIZATION ON INVOICES.ID = INVOICE_ITEMIZATION.INVOICE_ID AND INVOICES.VERSION_NUMBER = INVOICE_ITEMIZATION.INVOICE_VERSION  WHERE INVOICES.CLIENT_ID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                amountInvoiced = rs.getDouble("TOTAL INVOICED");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        return amountInvoiced;
    }

    /**
     * Get the total transaction paid by the customer
     * @param id The client ID.
     * @param transactionType Either Credit or Debit.
     * @return 
     */
    public Double getTotalTransactions(String id, String transactionType) {
        Double total = 0.0;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT SUM(AMOUNT) AS 'TOTAL' FROM TRANSACTIONS WHERE TYPE = ? AND CLIENT_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, transactionType);
            ps.setString(2, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("TOTAL");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        return total;
    }

}
