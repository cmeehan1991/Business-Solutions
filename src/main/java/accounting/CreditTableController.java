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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public class CreditTableController {
    
    ObservableList<AccountingItems> data = FXCollections.observableArrayList(new AccountingItems(false, null, null, null, null, null, null, null, null));
    
    public void accountingTable(TableView tableView){
        TableColumn<AccountingItems, Boolean> selectedColumn = new TableColumn("");
        TableColumn<AccountingItems, String> accountColumn = new TableColumn("Account");
        TableColumn<AccountingItems, String> enteredDateColumn = new TableColumn("Date Entered");
        TableColumn<AccountingItems, String> lastUpdatedDateColumn = new TableColumn("Last Updated");
        TableColumn<AccountingItems, String> typeColumn = new TableColumn("Type");
        TableColumn<AccountingItems, String> transactionIdColumn = new TableColumn("ID");
        TableColumn<AccountingItems, String> statusColumn = new TableColumn("Status");
        TableColumn<AccountingItems, String> memoColumn = new TableColumn("Memo");
        TableColumn<AccountingItems, String> amountColumn = new TableColumn("Amount");
        
        selectedColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectedColumn.setCellFactory(column -> new CheckBoxTableCell());
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
        enteredDateColumn.setCellValueFactory(new PropertyValueFactory<>("enteredDate"));
        lastUpdatedDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedDate"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        memoColumn.setCellValueFactory(new PropertyValueFactory<>("memo"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        
        tableView.setItems(data);
        tableView.getColumns().setAll(selectedColumn, accountColumn, enteredDateColumn, lastUpdatedDateColumn, typeColumn, transactionIdColumn, statusColumn, memoColumn, amountColumn);
    }
    
    /**
     * This will fill the accounting table with all transactions based on filters
     * @param paid
     * @param fromDate
     * @param toDate
     * @param transactionType
     * @param searchTerms
     * @param tableView
     * @throws java.sql.SQLException
     */
    public void fillAccountingTable(String paid, String fromDate, String toDate, String transactionType, String searchTerms, TableView tableView) throws SQLException{
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ALL_CUSTOMERS.COMPANY_NAME, TRANSACTIONS.DATE_CREATED, TRANSACTIONS.LAST_UPDATED, TRANSACTIONS.TYPE, TRANSACTIONS.ID, TRANSACTIONS.DATE_CREATED, TRANSACTIONS.STATUS, TRANSACTIONS.NOTES AS 'MEMO', TRANSACTIONS.AMOUNT FROM TRANSACTIONS INNER JOIN INVOICES ON INVOICES.ID = TRANSACTIONS.INVOICE_ID INNER JOIN ALL_CUSTOMERS ON INVOICES.CLIENT_ID = ALL_CUSTOMERS.ID  WHERE INVOICES.DATE_CREATED >= '" + fromDate + "' AND INVOICES.LAST_UPDATED <= '" + toDate + "'";
       
        // Set the parameters. 
        // We are not using a prepared statment here because we are setting the parameters 
        // on a case by case basis
        if((paid != null && !paid.equals("All")) || transactionType != null || searchTerms != null){
            sql += " WHERE ";
            
            if(paid != null && !paid.equals("All")){
                sql += " AND PAID = " + paid;
            }
            
            if(transactionType != null){
                sql += " AND TRANSACTION.TYPE = " + transactionType;
            }
            
            // We are going to add the search term logic later. 
            // I'm not quite sure how I wante to handle that yet. 
            
            
        }
        
        try{
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            System.out.println(ps);
            if(rs.next()){
                do{
                    this.data.addAll(new AccountingItems(false, rs.getString("COMPANY_NAME"), rs.getString("DATE_CREATED"), rs.getString("LAST_UPDATED"), rs.getString("INVOICE_TYPE"), rs.getString("ID"), rs.getString("STATUS"), rs.getString("MEMO"), rs.getString("AMOUNT")));
                }while(rs.next());
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }finally{
            conn.close();
        }
        tableView.setItems(this.data);
    }
    
    public class AccountingItems{
        
        SimpleBooleanProperty selected;
        SimpleStringProperty account;
        SimpleStringProperty enteredDate;
        SimpleStringProperty lastUpdatedDate;
        SimpleStringProperty type;
        SimpleStringProperty transactionId;
        SimpleStringProperty status;
        SimpleStringProperty memo;
        SimpleStringProperty amount;
        
        public AccountingItems(boolean selected, String account, String enteredDate, String lastUpdatedDate, String type, String transactionId, String status, String memo, String amount){
            this.selected = new SimpleBooleanProperty(selected);
            this.account = new SimpleStringProperty(account);
            this.enteredDate = new SimpleStringProperty(enteredDate);
            this.lastUpdatedDate = new SimpleStringProperty(lastUpdatedDate);
            this.type = new SimpleStringProperty(type);
            this.transactionId = new SimpleStringProperty(transactionId);
            this.status = new SimpleStringProperty(status);
            this.memo = new SimpleStringProperty(memo);
            this.amount = new SimpleStringProperty(amount);
        }
        
        public boolean getSelected(){
            return selected.get();
        } 
        
        public void setSelected(boolean selected){
            this.selected.set(selected);
        }
        
        public String getAccount(){
            return account.get();
        }
        
        public void setAccount(String account){
            this.account.set(account);
        }
        
        public String getEnteredDate(){
            return enteredDate.get();
        }
        
        public void setEnteredDate(String enteredDate){
            this.enteredDate.set(enteredDate);
        }
        
        public String getLastUpdatedDate(){
            return lastUpdatedDate.get();
        }
        
        public void setLastUpdatedDate(String lastUpdatedDate){
            this.lastUpdatedDate.set(lastUpdatedDate);
        }
        
        public String getType(){
            return type.get();
        }
        
        public void setType(String type){
            this.type.set(type);
        }
        
        public String getTransactionId(){
            return transactionId.get();
        }
        
        public void setTransactionId(String transactionId){
            this.transactionId.set(transactionId);
        }
                
        public String getStatus(){
            return status.get();
        }
        
        public void setStatus(String status){
            this.status.set(status);
        }
        
        public String getMemo(){
            return memo.get();
        }
        
        public void setMemo(String memo){
            this.memo.set(memo);
        }
        
        public String getAmount(){
            return amount.get();
        }
        
        public void setAmount(String amount){
            this.amount.set(amount);
        }
    }
    
}
