/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.quotes;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.customers.Client;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import com.cbmwebdevelopment.quotes.QuoteItemizationTable.QuoteItems;

/**
 *
 * @author cmeehan
 */
public class Quotes {

    private final Client client = new Client();
    public String quoteID, type, webHost, url, budget, clientName, typeDescription, projectType, startDate, completionDate, notes;
    private String id, version;
    public TableView itemizationTable;
    public boolean deadline, owned, flexible;

    public ObservableList<String> quotes(String clientID) throws SQLException {
        List<String> list = new ArrayList<>();
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, IF(TYPE_DESCRIPTION IS NULL, '', CONCAT(' - ', TYPE_DESCRIPTION)) AS 'TYPE_DESCRIPTION' FROM PROJECT_QUOTES WHERE CLIENT_ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, clientID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    list.add(rs.getString("ID") + rs.getString("TYPE_DESCRIPTION"));
                    System.out.println(rs.getString("ID"));
                } while (rs.next());
            } else {
                System.out.println("No Results");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }

        return FXCollections.observableArrayList(list);
    }

    public boolean saveQuote() throws SQLException {
        boolean quoteSaved = false;
        System.out.println(this.clientName);
        this.id = (quoteID.contains("-")) ? quoteID.split("-")[0] : this.quoteID;
        this.version = (this.getVersion(this.id) == null) ? "1" : String.valueOf(Integer.parseInt(this.getVersion(id)) + 1);

        Connection conn = new DBConnection().connect();
        String sql = "UPDATE PROJECT_QUOTES SET QUOTE_VERSION = ?, CLIENT_ID = ?, PROJECT_TYPE = ?, START_DATE = ?, COMPLETION_DATE = ?, IS_DEADLINE = ?, CLIENT_TYPE = ?, TYPE_DESCRIPTION = ?, WEB_HOST = ?, WEB_URL = ?, URL_IS_OWNED = ?, BUDGET = ?, BUDGET_IS_FLEXIBLE = ?, NOTES = ? WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, this.version);
            ps.setInt(2, Integer.parseInt(client.clientID(this.clientName)));
            ps.setString(3, this.projectType);
            ps.setString(4, this.startDate);
            ps.setString(5, this.completionDate);
            ps.setBoolean(6, this.deadline);
            ps.setString(7, this.type);
            ps.setString(8, this.typeDescription);
            ps.setString(9, this.webHost);
            ps.setString(10, this.url);
            ps.setBoolean(11, this.owned);
            ps.setString(12, this.budget);
            ps.setBoolean(13, this.flexible);
            ps.setString(14, this.notes);
            ps.setString(15, this.id);
            int rs = ps.executeUpdate();
            if (rs >= 1) {
                if (saveTable()) {
                    quoteSaved = true;
                }
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return quoteSaved;
    }

    private boolean saveTable() throws SQLException {
        boolean tableSaved;
        Connection conn = new DBConnection().connect();
        String sql = "INSERT INTO PROJECT_QUOTE_ITEMIZATION(PROJECT_ID, PROJECT_VERSION, CATEGORY, PRICE, PRICE_UNITS, TOTAL_UNITS, DESCRIPTION) VALUES(?,?,?,?,?,?,?)";
        ObservableList<QuoteItems> items = this.itemizationTable.getItems();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            System.out.println("ID: " + this.id);
            System.out.println("Version: " + this.version);
            items.forEach((item) -> {
                try {
                    ps.setInt(1, Integer.parseInt(this.id));
                    ps.setInt(2, Integer.parseInt(this.version));
                    ps.setString(3, item.getCategory());
                    ps.setDouble(4, Double.parseDouble(item.getCost()));
                    ps.setString(5, item.getCostUnit());
                    ps.setDouble(6, Double.parseDouble(item.getCostUnitTotal()));
                    ps.setString(7, item.getShortDescription());
                    ps.addBatch();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            });
            ps.executeBatch();
            ps.close();
            tableSaved = true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            tableSaved = false;
        } finally {
            conn.close();
        }

        return tableSaved;
    }

    public String createQuote(int clientID) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "INSERT INTO PROJECT_QUOTES (CLIENT_ID) VALUES(?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, clientID);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "Error";
        } finally {
            conn.close();
        }
        return null;
    }

    public String getProjectType(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT PROJECT_TYPE FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("PROJECT_TYPE");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public String getStartDate(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT DATE_FORMAT(START_DATE, '%Y-%m-%d') AS 'START_DATE'FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("START_DATE");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public String getCompletionDate(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT DATE_FORMAT(COMPLETION_DATE, '%Y-%m-%d') AS 'COMPLETION_DATE' FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("COMPLETION_DATE");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public boolean hasDeadline(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT IS_DEADLINE FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("IS_DEADLINE");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return false;
    }

    public boolean isPersonal(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT IF(CLIENT_TYPE = 'personal', true, false) as CLIENT_TYPE FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println(rs.getString("CLIENT_TYPE"));
                return rs.getBoolean("CLIENT_TYPE");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return false;
    }

    public boolean isBusiness(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT IF(CLIENT_TYPE = 'personal', true, false) as CLIENT_TYPE FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("CLIENT_TYPE");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return false;
    }

    public String getTypeDescription(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT TYPE_DESCRIPTION FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("TYPE_DESCRIPTION");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public String getWebHost(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT WEB_HOST FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("WEB_HOST");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public String getURL(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT WEB_URL FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("WEB_URL");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public boolean isOwned(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT URL_IS_OWNED FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("URL_IS_OWNED");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return false;
    }

    public String getBudget(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT BUDGET FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("BUDGET");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public boolean isFlexible(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT BUDGET_IS_FLEXIBLE FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("BUDGET_IS_FLEXIBLE");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return false;
    }

    public String getVersion(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT QUOTE_VERSION FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("QUOTE_VERSION");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }

    public ObservableList<QuoteItems> getItemizationTable(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        ObservableList<QuoteItems> data = FXCollections.observableArrayList();
        String version = getVersion(id);
        String projectVersion = (version == null) ? "IS NULL" : "=" + version;
        String sql = "SELECT CATEGORY, PRICE, PRICE_UNITS, TOTAL_UNITS, DESCRIPTION FROM PROJECT_QUOTE_ITEMIZATION WHERE PROJECT_ID = ? AND PROJECT_VERSION " + projectVersion;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                do {
                    data.add(new QuoteItems(rs.getString("CATEGORY"), rs.getString("PRICE"), rs.getString("PRICE_UNITS"), rs.getString("TOTAL_UNITS"), rs.getString("DESCRIPTION")));
                } while (rs.next());
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
        return data;
    }

    public String getNotes(String id) throws SQLException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT NOTES FROM PROJECT_QUOTES WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("NOTES");
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println("Get Project: " + ex.getMessage());
        } finally {
            conn.close();
        }
        return null;
    }
    
    
    public void getQuote(String quoteId, String quoteVersion, QuotesFXMLController controller){
        
    }
}
