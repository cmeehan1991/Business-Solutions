/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoices;

import customers.Client;
import invoices.InvoiceItemizationTable.InvoiceItems;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import notifications.Toast;
import output.InvoicePDF;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class InvoiceController implements Initializable {
    private final Client client = new Client();
    private final List<String> projectType = new ArrayList<>();
    public boolean isNew;
    TableView tableView;
    @FXML
    TextField invoiceIDTextField, projectNameTextField;
    
    @FXML
    ComboBox clientNameComboBox, projectTypeComboBox;
    
    @FXML
    TableView invoiceTableView;
    
    
    /**
     * When the user clicks on the export buttin this will cause the data to be 
     * written to a pdf and exported to their desktop.
     * 
     * @param event 
     */
    @FXML
    protected void export(ActionEvent event) throws IOException{
        InvoicePDF invoicePDF = new InvoicePDF();
        invoicePDF.invoiceID = this.invoiceIDTextField.getText();
        invoicePDF.projectName = this.projectNameTextField.getText();
        invoicePDF.clientName = this.clientNameComboBox.getSelectionModel().getSelectedItem().toString();
        invoicePDF.invoiceType = "Invoice";
        invoicePDF.projectType = this.projectTypeComboBox.getSelectionModel().getSelectedItem().toString();
        invoicePDF.invoiceTableView = this.invoiceTableView;
        invoicePDF.createPDF();
    }
    
    @FXML 
    protected void print(ActionEvent event){
        
    }
    
    @FXML 
    protected void save(ActionEvent event) throws SQLException{
        Invoice invoice = new Invoice();
        invoice.clientName = clientNameComboBox.getSelectionModel().getSelectedItem().toString();
        invoice.invoiceID = invoiceIDTextField.getText();
        invoice.invoiceType = projectTypeComboBox.getSelectionModel().getSelectedItem().toString();
        invoice.isNew = this.isNew;
        invoice.invoiceItemizationTable = this.invoiceTableView;
        invoice.projectShortDescription = this.projectNameTextField.getText();
        if(invoice.saveInvoice()){
            Stage ownerStage = (Stage)invoiceIDTextField.getScene().getWindow();
            Toast.makeText(ownerStage, "The invoice has been saved.", 2500, 500, 500);
            String invoiceID = (invoiceIDTextField.getText().contains("-")) ? invoiceIDTextField.getText() + String.valueOf(Integer.parseInt(invoiceIDTextField.getText().split("-")[1]) + 1) : invoiceIDTextField.getText() + "-1";
            this.invoiceIDTextField.setText(invoiceID);
        }else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving the invoice");
            alert.setContentText("The invoice did not save successfully. Please try again.");
            alert.showAndWait();
        }
    }
    
    @FXML
    protected void addRow(ActionEvent event){
        System.out.println("Add Row");
        ObservableList<InvoiceItems> invoiceRow = FXCollections.observableArrayList(new InvoiceItems(false, null, null, null, null, null));
        invoiceTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        invoiceTableView.getItems().addAll(invoiceRow);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String[] projectTypes = {"Mobile Application - iOS", "Mobile Application - Android", "Other", "Social Media Marketing", "Software - New", "Software - Existing", "Website - New", "Website - Existing" };
        for (String type : projectTypes){
            this.projectType.add(type);
        }
        List<String> clientName = client.clientList();
        this.projectTypeComboBox.getItems().addAll(this.projectType);
        this.clientNameComboBox.getItems().addAll(clientName);
        
        this.tableView = invoiceTableView.getSelectionModel().getTableView();
        tableView.setEditable(true);
        new InvoiceItemizationTable(tableView);
        
    }    
    
}
