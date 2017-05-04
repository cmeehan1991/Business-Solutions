/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoices;

import com.mysql.jdbc.Statement;
import connections.DBConnection;
import customers.Client;
import invoices.InvoiceItemizationTable.InvoiceItems;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 *
 * @author cmeehan
 */
public class Invoice {
    private boolean didSave;
    private String id, versionNumber;
    private Client client = new Client();
    public boolean isNew;
    public String invoiceID, clientName, invoiceType, projectShortDescription;
    public TableView invoiceItemizationTable;
    
    protected boolean saveInvoice() throws SQLException{
        Connection conn = new DBConnection().connect();        
        // If this is the first time saving the invoice we will update the new one that was just created. 
        // If this is not, we will insert a new invoice. 
        if(this.isNew){
            this.id = this.invoiceID;
            String sql = "UPDATE INVOICES SET SHORT_DESCRIPTION = ?, CLIENT_ID = ?, INVOICE_TYPE = ? WHERE ID = ?";
            try{
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, this.projectShortDescription);
                ps.setString(2, client.clientID(this.clientName));
                ps.setString(3, this.invoiceType);
                ps.setString(4, this.invoiceID);
                int rs = ps.executeUpdate();
                if(rs >= 0){
                    if(insertItemizedTable()){
                        this.didSave = true;
                    }
                }
            }catch(SQLException ex){
                System.out.println("Save Invoice: " + ex.getMessage());
            }finally{
                conn.close();
            }
        }else{
            this.id = (this.invoiceID.contains("-")) ? this.invoiceID.split("-")[0] : this.invoiceID;
            this.versionNumber = (this.invoiceID.contains("-")) ? String.valueOf(Integer.parseInt(this.invoiceID.split("-")[1]) + 1) : "1";
            String sql = "UPDATE INVOICES SET VERSION_NUMBER = ?, SHORT_DESCRIPTION = ?, CLIENT_ID = ?, INVOICE_TYPE = ? WHERE ID = ?";
            try{
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, versionNumber);
                ps.setString(2, this.projectShortDescription);
                ps.setString(3, client.clientID(this.clientName));
                ps.setString(4, this.invoiceType);
                ps.setString(5, id);
                ps.executeUpdate();
                        if(insertItemizedTable()){
                            this.didSave = true;
                        }
            }catch(SQLException ex){
                System.out.println(ex.getMessage());
            }finally{
                conn.close();
            }
        }
        
        return this.didSave;
    }
    
    private boolean insertItemizedTable(){
        Connection conn = new DBConnection().connect();
        ObservableList<InvoiceItems> invoice = this.invoiceItemizationTable.getItems();
        String id = null;
        String versionNumber = null;
        
        String sql = "INSERT INTO INVOICE_ITEMIZATION(INVOICE_ID, INVOICE_VERSION, PAID, CATEGORY, COST, COST_EVALUATION, COST_TIME, DESCRIPTION) VALUES(?,?,?,?,?,?,?,?)";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            invoice.forEach((item)->{
                try {
                    ps.setString(1, this.id);
                    ps.setString(2, this.versionNumber);
                    ps.setBoolean(3, item.isPaid());
                    ps.setString(4, item.getCategory());
                    ps.setDouble(5, Double.parseDouble(item.getCost()));
                    ps.setString(6, item.getCostUnit());
                    ps.setDouble(7, Double.parseDouble(item.getCostUnitTotal()));
                    ps.setString(8, item.getShortDescription());
                    ps.addBatch();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            });
            int[] rs = ps.executeBatch();
            boolean batchFailed = false;
            for(int i = 0; i < rs.length; i++){
                if(rs[i] < 1){
                    batchFailed = true;
                }
            }
            if(batchFailed){
                return false;
            }else{
                return true;
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            return false;
        }
    }
    
    private boolean hasChanges(){
        boolean changes = false;
        Connection conn = new DBConnection().connect();
        
        String id, versionNumber = null;
        
        // Parse the invoice ID
        if(this.invoiceID.contains("-")){
            id = this.invoiceID.split("-")[0];
            versionNumber = this.invoiceID.split("-")[1];
        }else{
            id = this.invoiceID;
        }
        
        // Check for changes with the invoice data
        String sql = "SELECT CLIENT_ID, INVOICE_TYPE, SHORT_DESCRIPTION FROM INVOICES WHERE ID = ? AND VERSION_NUMBER = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, versionNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                if(!client.clientID(this.clientName).equals(String.valueOf(rs.getInt("CLIENT_ID"))) || !this.invoiceType.equals(rs.getString("INVOICE_TYPE")) || !this.projectShortDescription.equals(rs.getString("SHORT_DESCRIPTION"))){
                    changes = true;
                }else{ // 
                    changes = checkTable(id, versionNumber);
                }
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        return changes;
    }
    
    private boolean checkTable(String ID, String version){
        boolean changes = false;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT CATEGORY, COST, COST_EVALUATION, COST_TIME, DESCRIPTION FROM INVOICE_ITEMIZATION WHERE INVOICE_ID = ? AND INVOICE_VERSION = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ID);
            ps.setString(2, version);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ObservableList<Invoice> invoice = invoiceItemizationTable.getItems();
                invoice.forEach((item)->{
                    try {
                        do{
                            
                        }while(rs.next());
                    } catch (SQLException ex) {
                        System.out.println("Error");
                    }
                });
            }else{
                changes = true;
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return changes;
    }
    
    /**
     * Here we are creating a new invoice. At this time we are inserting only
     * the client ID into the INVOICES table.
     *
     * @param clientID
     * @return
     */
    public String createInvoice(String clientID) {
        Connection conn = new DBConnection().connect();
        String sql = "INSERT INTO INVOICES(CLIENT_ID) VALUES(?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, Integer.parseInt(clientID));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            } else {
                return "None";
            }
        } catch (SQLException ex) {
            System.out.println("Error creating invoice");
            System.out.println(ex.getMessage());
            System.out.println(ex.getCause());
            return "Error";
        }
    }
    
    public ObservableList<String> invoices(String clientID){
        List<String> list = new ArrayList<>();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, SHORT_DESCRIPTION FROM INVOICES WHERE CLIENT_ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, clientID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                do{
                    list.add(rs.getString("ID") + " - " + rs.getString("SHORT_DESCRIPTION"));
                }while(rs.next());
            }
        }catch(SQLException ex){
            ex.getMessage();
        }
        
        return FXCollections.observableArrayList(list);
    }
    
    public String getInvoiceType(String invoiceID) throws SQLException{
        Connection conn = new DBConnection().connect();
        
        String sql = "SELECT INVOICE_TYPE, MAX(VERSION_NUMBER) FROM INVOICES WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("INVOICE_TYPE");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }finally{
            conn.close();
        }
        return null;
    }
    
    public String getLastVersion(String invoiceID) throws SQLException{
        Connection conn = new DBConnection().connect();
        
        String sql = "SELECT VERSION_NUMBER AS 'VERSION' FROM INVOICES WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("VERSION");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }finally{
            conn.close();
        }
        return null;
    }
    
    public String getInvoiceName(String invoiceID) throws SQLException{
         Connection conn = new DBConnection().connect();
        
        String sql = "SELECT SHORT_DESCRIPTION FROM INVOICES WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getString("SHORT_DESCRIPTION");
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }finally{
            conn.close();
        }
        return null;
    }
    
    public ObservableList<InvoiceItems> getInvoiceItems(String invoiceID, String versionNumber){
        ObservableList<InvoiceItems> data = FXCollections.observableArrayList();
        Connection conn = new DBConnection().connect();
        
        versionNumber = (versionNumber == null) ? "IS NULL" : " = " + versionNumber;
        
        String sql = "SELECT PAID, CATEGORY, COST, COST_EVALUATION, COST_TIME, DESCRIPTION FROM INVOICE_ITEMIZATION WHERE INVOICE_ID = ? AND INVOICE_VERSION " + versionNumber;
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceID);
            System.out.println(ps);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                do{
                    data.add(new InvoiceItems(rs.getBoolean("PAID"), rs.getString("CATEGORY"), rs.getString("COST"), rs.getString("COST_EVALUATION"), rs.getString("COST_TIME"), rs.getString("DESCRIPTION")));
                }while(rs.next());
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return data;
    }
}
