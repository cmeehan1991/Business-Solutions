/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.connections.DBConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.sql.Types.DOUBLE;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author cmeehan
 */
public class Accounting {
     /**
     * 
     * @param clientId
     * @param invoiceId
     * @return
     * @throws SQLException 
     */
    public Double getInvoiceAmount(String clientId, String invoiceId) throws SQLException {
        Double total = 0.0;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT MAX(INVOICE_ITEMIZATION.INVOICE_VERSION), INVOICE_ITEMIZATION.INVOICE_ID, SUM(INVOICE_ITEMIZATION.COST * INVOICE_ITEMIZATION.COST_TIME) AS 'TOTAL' FROM INVOICE_ITEMIZATION WHERE INVOICE_ITEMIZATION.INVOICE_ID = ? GROUP BY INVOICE_ITEMIZATION.INVOICE_ID, INVOICE_ITEMIZATION.INVOICE_VERSION LIMIT 1";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceId);
            ResultSet rs = ps.executeQuery();
           if(rs.next()){
               total = rs.getDouble("TOTAL");
           }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }finally{
            try{
                conn.close();
            }catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
        return total;
    }
    
    public Double getInvoiceAmountPaid(String invoiceId){
        Double total = 0.0;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT SUM(TRANSACTIONS.AMOUNT) AS 'TOTAL' FROM TRANSACTIONS WHERE INVOICE_ID = ? AND TYPE = 'Credit' AND STATUS = 'CLEARED'";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                total = rs.getDouble("TOTAL");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }finally{
            try{
                conn.close();
            }catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
        return total;
    }
}
