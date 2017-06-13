/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects;

import com.mysql.cj.api.jdbc.Statement;
import connections.DBConnection;
import customers.Client;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import projects.ProjectsBillingTableViewController.BillingItems;

/**
 *
 * @author cmeehan
 */
public class Projects {

    public String projectID, clientName, projectTitle, projectDescription, projectType, managers, projectTeam;
    public int userID;
    public boolean planning, inProgress, inReview, complete, cancelled;
    private Client client = new Client();
    private Date date = new Date();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    /**
     * Here we are going to be creating a new project. This will be inserting
     * the project information into the database.
     *
     * @return String projectID
     */
    public String addNewProject() {
        String key = null;
        Connection conn = new DBConnection().connect();
        String sql = "INSERT INTO PROJECT (CLIENT_ID, TITLE, PROJECT_TYPE, DESCRIPTION, QUOTE_ID, PROJECT_MANAGER, PROJECT_TEAM, PLANNING, IN_PROGRESS, IN_REVIEW, COMPLETED, CANCELLED, CREATED_BY, DATE_CREATED) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, Integer.parseInt(client.clientID(clientName)));
            ps.setString(2, projectTitle);
            ps.setString(3, projectType);
            ps.setString(4, projectDescription);
            ps.setString(5, null);
            ps.setString(6, managers);
            ps.setString(7, projectTeam);
            ps.setBoolean(8, planning);
            ps.setBoolean(9, inProgress);
            ps.setBoolean(10, inReview);
            ps.setBoolean(11, complete);
            ps.setBoolean(12, cancelled);
            ps.setInt(13, userID);
            ps.setString(14, dateFormat.format(date));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                key = String.valueOf(rs.getInt(1));
            }
        } catch (NumberFormatException | SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Projects.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return key;
    }

    public boolean updateProject(String projectID) {
        boolean updated = false;
        Connection conn = new DBConnection().connect();
        String sql = "UPDATE PROJECT SET CLIENT_ID = ?, TITLE = ?, PROJECT_TYPE = ?, DESCRIPTION = ?, PROJECT_MANAGER = ?, PROJECT_TEAM = ?, PLANNING = ?, IN_PROGRESS = ?, IN_REVIEW = ?, COMPLETED = ?, CANCELLED = ?, UPDATED_BY = ? WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, client.clientID(clientName));
            ps.setString(2, this.projectTitle);
            ps.setString(3, this.projectType);
            ps.setString(4, this.projectDescription);
            ps.setString(5, this.managers);
            ps.setString(6, this.projectTeam);
            ps.setBoolean(7, this.planning);
            ps.setBoolean(8, this.inProgress);
            ps.setBoolean(9, this.inReview);
            ps.setBoolean(10, this.complete);
            ps.setBoolean(11, this.cancelled);
            ps.setInt(12, this.userID);
            ps.setString(13, this.projectID);
            int rs = ps.executeUpdate();
            if (rs > 0) {
                updated = true;
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return updated;
    }

    public String getID(String projectName) {
        String id = null;
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID FROM PROJECT WHERE TITLE = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, projectName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getString("ID");
                System.out.println(rs.getString("ID"));
            }else{
                System.out.println("Nothing returned");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return id;
    }

    public ObservableList<String> projects(String clientID) {
        List<String> projects = new ArrayList<>();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT TITLE FROM PROJECT WHERE CLIENT_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, clientID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    projects.add(rs.getString("TITLE"));
                } while (rs.next());
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return FXCollections.observableArrayList(projects);
    }
    
    /**
     * Here we are getting the invoices that are associated with the selected project. 
     * 
     * @param ID
     * @return ObservableList
     */
    public ObservableList<BillingItems> getInvoices(String ID) {
        ObservableList<BillingItems> items = FXCollections.observableArrayList();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT INVOICES.ID AS 'ID', INVOICES.SHORT_DESCRIPTION, INVOICES.INVOICE_TYPE, CONCAT('$', FORMAT(SUM(INVOICE_ITEMIZATION.COST * INVOICE_ITEMIZATION.COST_TIME), 2)) AS 'TOTAL_COST', CONCAT('$', FORMAT(IF(INVOICES.NO_CHARGE = true, 0, SUM(INVOICE_ITEMIZATION.COST * INVOICE_ITEMIZATION.COST_TIME) - SUM(INVOICE_ITEMIZATION.PAID)), 2)) AS 'AMOUNT_DUE'\n"
                + "FROM INVOICES JOIN INVOICE_ITEMIZATION ON INVOICE_ITEMIZATION.INVOICE_ID = INVOICES.ID \n"
                + "WHERE INVOICE_ITEMIZATION.INVOICE_ID = INVOICES.ID AND INVOICE_ITEMIZATION.INVOICE_VERSION = INVOICES.VERSION_NUMBER AND INVOICES.PROJECT_ID = ? \n"
                + "GROUP BY INVOICES.VERSION_NUMBER;";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    items.add(new ProjectsBillingTableViewController.BillingItems(String.valueOf(rs.getInt("ID")), rs.getString("SHORT_DESCRIPTION"), rs.getString("INVOICE_TYPE"), rs.getString("TOTAL_COST"), rs.getString("AMOUNT_DUE")));
                } while (rs.next());
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        items.forEach((item)->{
            System.out.println(item.invoiceID);
            System.out.println(item.invoiceTitle);
            System.out.println(item.invoiceType);
            System.out.println(item.totalCost);
            System.out.println(item.amountDue);
        });
        return items;
    }

}
