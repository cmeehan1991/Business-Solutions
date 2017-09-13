/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.invoices;

import java.sql.Statement;
import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.customers.Client;
import com.cbmwebdevelopment.invoices.InvoiceItemizationTable.InvoiceItems;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import static com.cbmwebdevelopment.main.Main.CURRENCY;

/**
 *
 * @author cmeehan
 */
public class Invoice {

    private boolean didSave;
    private String id, versionNumber;
    private final Client client = new Client();
    public boolean isNew, noCharge;
    public String userID, invoiceID, clientName, invoiceType, invoiceTitle, invoiceDescription, projectID, paymentDueDate, billTo;
    public TableView invoiceItemizationTable;
    public InvoiceController invoiceController;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Calendar DEFAULT_DUE_DATE = new GregorianCalendar();
    

    /**
     * This method will save the invoice. It checks if it is a new invoice or if
     * it is an existing invoice. If this is a new invoice it will insert it
     * into the database, otherwise it will write over the existing invoice as
     * an update.
     *
     * @return
     * @throws SQLException
     */
    protected boolean saveInvoice() throws SQLException {
        Connection conn = new DBConnection().connect();
        // If this is the first time saving the invoice we will update the new one that was just created. 
        // If this is not, we will insert a new invoice. 

        if (this.isNew) {
            this.id = this.invoiceID;
            String sql = "INSERT INTO INVOICES (PROJECT_ID, INVOICE_TITLE, CLIENT_ID, INVOICE_TYPE, DESCRIPTION, NO_CHARGE, CREATED_BY, DATE_CREATED, PAYMENT_DUE_BY, BILL_TO) VALUES(?,?,?,?,?,?,?,?,?,?)";
            try {
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, this.projectID);
                ps.setString(2, this.invoiceTitle);
                ps.setString(3, client.clientID(this.clientName));
                ps.setString(4, this.invoiceType);
                ps.setString(5, this.invoiceDescription);
                ps.setBoolean(6, this.noCharge);
                ps.setString(7, this.userID);
                ps.setString(8, format.format(new Date()));
                ps.setString(9, this.paymentDueDate);
                ps.setString(10, this.billTo);
                ps.executeUpdate();
                ResultSet key = ps.getGeneratedKeys();
                if (key.next()) {
                    if (insertItemizedTable(String.valueOf(key.getLong(1)), null)) {
                        this.didSave = true;
                    }
                }
                ps.closeOnCompletion();
            } catch (SQLException ex) {
                System.out.println("Save Invoice: " + ex.getMessage());
            } finally {
                conn.close();
            }
        } else {
            this.id = (this.invoiceID.contains("-")) ? this.invoiceID.split("-")[0].trim() : this.invoiceID;
            this.versionNumber = (this.invoiceID.contains("-")) ? String.valueOf(Integer.parseInt(this.invoiceID.split("-")[1]) + 1).trim() : "1";
            String sql = "UPDATE INVOICES SET VERSION_NUMBER = ?, PROJECT_ID = ?, INVOICE_TITLE = ?, CLIENT_ID = ?, INVOICE_TYPE = ?, DESCRIPTION = ?, NO_CHARGE = ?, LAST_UPDATED_BY = ?, PAYMENT_DUE_BY = ?, BILL_TO = ? WHERE ID = ?";
            try {
                if (archiveInvoice(this.id) >= 1) {
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, this.versionNumber);
                    ps.setString(2, this.projectID);
                    ps.setString(3, this.invoiceTitle);
                    ps.setString(4, client.clientID(this.clientName));
                    ps.setString(5, this.invoiceType);
                    ps.setString(6, this.invoiceDescription);
                    ps.setBoolean(7, this.noCharge);
                    ps.setString(8, this.userID);
                    ps.setString(9, this.paymentDueDate);
                    ps.setString(10, this.billTo);
                    ps.setString(11, this.id);
                    ps.executeUpdate();
                    if (insertItemizedTable(this.id, this.versionNumber)) {
                        this.didSave = true;
                    }
                    ps.closeOnCompletion();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            } finally {
                conn.close();
            }
        }

        return this.didSave;
    }

    /**
     * Archive the previous invoice before inserting the new updated invoice.
     * This function exists in the absence of the ability to use stored procedures.
     * 
     * @param invoiceId
     * @return 
     */
    private int archiveInvoice(String invoiceId){
        Connection conn = new DBConnection().connect();
        String sql = "INSERT INTO INVOICES_ARCHIVE(INVOICE_ID, VERSION_NUMBER, PROJECT_ID, INVOICE_TITLE, CLIENT_ID, INVOICE_TYPE, DESCRIPTION, ASSIGNED_TO, NO_CHARGE, CREATED_BY, DATE_CREATED, LAST_UPDATED_BY, LAST_UPDATED, PAYMENT_DUE_BY, BILL_TO, INVOICE_STATUS) SELECT ID, VERSION_NUMBER, PROJECT_ID, INVOICE_TITLE, CLIENT_ID, INVOICE_TYPE, DESCRIPTION, ASSIGNED_TO, NO_CHARGE, CREATED_BY, DATE_CREATED, LAST_UPDATED_BY, LAST_UPDATED, PAYMENT_DUE_BY, BILL_TO, INVOICE_STATUS FROM INVOICES WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceId);
            int rows = ps.executeUpdate();
            return rows;
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            return 0;
        }finally{
            try{
                conn.close();
            }catch(SQLException ex){
               System.out.println(ex.getMessage());
            }
        }
    }
    
    /**
     * Delete the invoice.
     * @param id
     * @return
     * @throws SQLException 
     */
    protected boolean deleteInvoice(String id) throws SQLException {
        boolean deleted = false;
        Connection conn = new DBConnection().connect();
        String sql = "DELETE FROM INVOICES WHERE ID = ?";
        try {
            if (archiveDeletedInvoice(id) >= 1) {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, id);
                deleted = ps.executeUpdate() >= 1 ? true : false;
                ps.closeOnCompletion();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return deleted;
    }
    
    /**
     * Arvhice the invoice into the deleted archive table before deleting the 
     * invoice from the invoices table.
     * @param invoiceId
     * @return 
     */
    private int archiveDeletedInvoice(String invoiceId){
        Connection conn = new DBConnection().connect();
        String sql = "INSERT INTO DELETED_INVOICES(INVOICE_ID, INVOICE_VERSION_NUMBER, PROJECT_ID, INVOICE_TITLE, CLIENT_ID, INVOICE_TYPE, DESCRIPTION, ASSIGNED_TO, NO_CHARGE, CREATED_BY, DATE_CREATED, LAST_UPDATED_BY, LAST_UPDATED, PAYMENT_DUE_BY, BILL_TO, INVOICE_STATUS) SELECT ID, VERSION_NUMBER, PROJECT_ID, INVOICE_TITLE, CLIENT_ID, INVOICE_TYPE, DESCRIPTION, ASSIGNED_TO, NO_CHARGE, CREATED_BY, DATE_CREATED, LAST_UPDATED_BY, LAST_UPDATED, PAYMENT_DUE_BY, BILL_TO, INVOICE_STATUS FROM INVOICES WHERE ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceId);
            int rows = ps.executeUpdate();
            return rows;
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            return 0;
        }finally{
            try{
                conn.close();
            }catch(SQLException ex){
               System.out.println(ex.getMessage());
            }
        }
    }

    private boolean insertItemizedTable(String id, String versionNumber) throws SQLException {
        Connection conn = new DBConnection().connect();
        ObservableList<InvoiceItems> invoice = this.invoiceItemizationTable.getItems();
        String sql = "INSERT INTO INVOICE_ITEMIZATION(INVOICE_ID, INVOICE_VERSION, PAID, CATEGORY, COST, COST_EVALUATION, COST_TIME, DESCRIPTION) VALUES(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            invoice.forEach((item) -> {
                try {
                    ps.setString(1, id);
                    ps.setString(2, versionNumber);
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
            ps.executeBatch();
            return true;
        } catch (SQLException ex) {
            System.out.println("Batch Update Error: " + ex.getMessage());
            return false;
        } finally {
            conn.close();
        }
    }

    private boolean checkTable(String ID, String version) throws SQLException {
        boolean changes = false;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT CATEGORY, COST, COST_EVALUATION, COST_TIME, DESCRIPTION FROM INVOICE_ITEMIZATION WHERE INVOICE_ID = ? AND INVOICE_VERSION = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ID);
            ps.setString(2, version);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ObservableList<Invoice> invoice = invoiceItemizationTable.getItems();
                invoice.forEach((item) -> {
                    try {
                        do {

                        } while (rs.next());
                    } catch (SQLException ex) {
                        System.out.println("Error");
                    }
                });
            } else {
                changes = true;
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return changes;
    }

    public boolean noCharge(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT NO_CHARGE FROM INVOICES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("NO_CHARGE");
            }
            ps.closeOnCompletion();
        } catch (SQLException ex) {

        } finally {
            conn.close();
        }
        return false;
    }

    /**
     * Here we are creating a new invoice. At this time we are inserting only
     * the client ID into the INVOICES table.
     *
     * @param clientID
     * @return
     * @throws java.sql.SQLException
     */
    public String createInvoice(String clientID) throws SQLException {
        String invoice = null;
        Connection conn = new DBConnection().connect();
        String sql = "INSERT INTO INVOICES(CLIENT_ID) VALUES(?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, Integer.parseInt(clientID));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                invoice = String.valueOf(rs.getInt(1));
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Error creating invoice");
            System.out.println(ex.getMessage());
            System.out.println(ex.getCause());
            invoice = "Error";
        } finally {
            conn.close();
        }
        return invoice;
    }

    public ObservableList<String> invoices(String clientID) throws SQLException {
        List<String> list = new ArrayList<>();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT INVOICES.ID, INVOICES.INVOICE_TITLE FROM INVOICES WHERE CLIENT_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, clientID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    list.add(rs.getString("ID") + " - " + rs.getString("INVOICE_TITLE"));
                } while (rs.next());
            }
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            ex.getMessage();
        } finally {
            conn.close();
        }

        return FXCollections.observableArrayList(list);
    }

    public String getInvoiceType(String invoiceID) throws SQLException {
        Connection conn = new DBConnection().connect();

        String sql = "SELECT INVOICE_TYPE, MAX(VERSION_NUMBER) FROM INVOICES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("INVOICE_TYPE");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public String getLastVersion(String invoiceID) throws SQLException {
        Connection conn = new DBConnection().connect();

        String sql = "SELECT VERSION_NUMBER AS 'VERSION' FROM INVOICES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("VERSION");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public String getInvoiceName(String invoiceID) throws SQLException {
        Connection conn = new DBConnection().connect();

        String sql = "SELECT INVOICE_TITLE FROM INVOICES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("INVOICE_TITLE");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public ObservableList<InvoiceItems> getInvoiceItems(String invoiceID, String versionNumber) throws SQLException {
        ObservableList<InvoiceItems> data = FXCollections.observableArrayList();
        Connection conn = new DBConnection().connect();

        String version = (versionNumber == null || versionNumber.equals("null")) ? "INVOICE_VERSION IS NULL" : "INVOICE_VERSION = " + versionNumber;

        String sql = "SELECT PAID, CATEGORY, COST, COST_EVALUATION, COST_TIME, DESCRIPTION FROM INVOICE_ITEMIZATION WHERE INVOICE_ID = ? AND " + version;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.add(new InvoiceItems(rs.getBoolean("PAID"), rs.getString("CATEGORY"), rs.getString("COST"), rs.getString("COST_EVALUATION"), rs.getString("COST_TIME"), rs.getString("DESCRIPTION")));
                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return data;
    }

    public String getInvoiceNotes(String invoiceID) throws SQLException {
        Connection conn = new DBConnection().connect();

        String sql = "SELECT DESCRIPTION FROM INVOICES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("DESCRIPTION");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public void getInvoice(InvoiceController controller, String ID) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT INVOICES.VERSION_NUMBER, IF(VERSION_NUMBER IS NULL, INVOICES.ID, CONCAT(INVOICES.ID, '-', INVOICES.VERSION_NUMBER)) AS 'INVOICE_ID', PROJECT.TITLE, INVOICE_TITLE, IF(ALL_CUSTOMERS.COMPANY_NAME IS NULL, CONCAT(ALL_CUSTOMERS.PRIMARY_CONTACT_FIRST_NAME, ' ', ALL_CUSTOMERS.PRIMARY_CONTACT_LAST_NAME), ALL_CUSTOMERS.COMPANY_NAME) AS 'CLIENT', INVOICES.INVOICE_TYPE, INVOICES.DESCRIPTION, INVOICES.NO_CHARGE, DATE_FORMAT(INVOICES.PAYMENT_DUE_BY, '%d/%m/%Y') AS 'PAYMENT_DUE_BY', IF(SUM(TRANSACTIONS.AMOUNT) < SUM(INVOICE_ITEMIZATION.COST * INVOICE_ITEMIZATION.COST_TIME), IF(NOW() < INVOICES.PAYMENT_DUE_BY, '', 'OVERDUE'), 'PAID') AS 'INVOICE_STATUS', IF(INVOICES.BILL_TO IS NULL, CONCAT(ALL_CUSTOMERS.PRIMARY_CONTACT_FIRST_NAME, ' ', ALL_CUSTOMERS.PRIMARY_CONTACT_LAST_NAME,'\\n', ALL_CUSTOMERS.PRIMARY_STREET_ADDRESS, IF(ALL_CUSTOMERS.SECONDARY_STREET_ADDRESS IS NOT NULL, CONCAT('\\n', ALL_CUSTOMERS.SECONDARY_STREET_ADDRESS), ''), '\\n', ALL_CUSTOMERS.CITY, ', ', ALL_CUSTOMERS.STATE,' ', ALL_CUSTOMERS.ZIPCODE) , INVOICES.BILL_TO) AS 'BILL_TO' FROM INVOICES JOIN ALL_CUSTOMERS ON ALL_CUSTOMERS.ID = INVOICES.CLIENT_ID LEFT JOIN PROJECT ON INVOICES.PROJECT_ID = PROJECT.ID JOIN TRANSACTIONS ON TRANSACTIONS.INVOICE_ID = INVOICES.ID JOIN INVOICE_ITEMIZATION ON INVOICE_ITEMIZATION.INVOICE_ID = INVOICES.ID AND INVOICE_ITEMIZATION.INVOICE_VERSION = INVOICES.VERSION_NUMBER WHERE INVOICES.ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                controller.invoiceIDTextField.setText(rs.getString("INVOICE_ID"));
                controller.projectComboBox.getSelectionModel().select(rs.getString("TITLE"));
                controller.invoiceNameTextField.setText(rs.getString("INVOICE_TITLE"));
                controller.clientNameComboBox.getSelectionModel().select(rs.getString("CLIENT"));
                controller.invoiceTypeComboBox.getSelectionModel().select(rs.getString("INVOICE_TYPE"));
                controller.descriptionTextArea.setText(rs.getString("DESCRIPTION"));
                controller.billToTextArea.setText(rs.getString("BILL_TO"));
                controller.noChargeCheckBox.setSelected(rs.getBoolean("NO_CHARGE"));
                LocalDate date = (rs.getString("PAYMENT_DUE_BY").equals("00/00/0000")) ? LocalDate.now().plusMonths(1) : LocalDate.parse(rs.getString("PAYMENT_DUE_BY"), dateTimeFormatter);
                controller.paymentDueDatePicker.setValue(date);
                controller.paymentsReceivedList.setItems(paymentsReceived(ID));
                controller.invoiceStatusLabel.setText(rs.getString("INVOICE_STATUS"));
                controller.tableView.getItems().setAll(getInvoiceItems(ID, rs.getString("VERSION_NUMBER")));
            }
            ps.closeOnCompletion();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
    }
    
    /**
     * Get all of the payments associated with the particular 
     * @param invoiceId
     * @return 
     */
    ObservableList<String> paymentsReceived(String invoiceId) throws SQLException{
        ObservableList<String> payments = FXCollections.observableArrayList();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, AMOUNT FROM TRANSACTIONS WHERE INVOICE_ID = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                do{
                    payments.add(rs.getString("ID") + " - " + CURRENCY.format(rs.getDouble("AMOUNT")));
                }while(rs.next());
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }finally{
            conn.close();
        }
        return payments;
    }

    /**
     * Get the invoices associated with the selected client.
     *
     * @param clientId
     * @param paid
     * @return
     * @throws SQLException
     */
    public ObservableList<String> getInvoices(String clientId, boolean paid) throws SQLException {
        ObservableList<String> invoices = FXCollections.observableArrayList();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, INVOICE_TITLE FROM INVOICES WHERE CLIENT_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, clientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    invoices.add(rs.getString("ID") + " - " + rs.getString("INVOICE_TITLE"));
                } while (rs.next());
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return invoices;
    }
}
