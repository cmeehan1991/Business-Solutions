/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accounting;

import customers.Client;
import invoices.Invoice;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.CheckComboBox;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class CreditFXMLController implements Initializable {

    Client clients = new Client();
    Invoice invoices = new Invoice();
    String paymentMethod;
    
    boolean isNew;
    String clientId;
    
    @FXML
    TextField amountTextField, memoTextField;
    
    @FXML
    ComboBox receiveFromComboBox, applyToComboBox;

    @FXML
    ToggleGroup underpaymentGroup;

    @FXML
    RadioButton leaveRadioButton, writeOffRadioButton;
    

    @FXML
    Label currentBalanceLabel, creditIdLabelLabel, creditIdLabel, amountDueLabel, amountAppliedLabel, discountsLabel;

    @FXML
    DatePicker paymentDatePicker;

    @FXML
    TableView paymentsTable;
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String type;

    /**
     * Open a new customer payment form in a new stage. 
     * @param event 
     */
    @FXML
    protected void newPayment(ActionEvent event) throws SQLException {
        Credit credit = new Credit();
        credit.insertNewCredit(type, this.amountTextField.getText(), null, this.memoTextField.getText(), formatter.format(this.paymentDatePicker.getValue()), this.applyToComboBox.getSelectionModel().getSelectedItem().toString().split(" - ")[0], this.paymentMethod, clients.clientID(this.receiveFromComboBox.getSelectionModel().getSelectedItem().toString()), creditIdLabel);
    }

    /**
     * Export the payment receipt to the user's machine.
     * @param event 
     */
    @FXML
    protected void exportReceipt(ActionEvent event) {

    }

    /**
     * Email the payment receipt to a particular email address. 
     * @param event 
     */
    @FXML
    protected void emailReceipt(ActionEvent event) {

    }

    /**
     * Save the current payment and create a new payment. 
     * @param event 
     */
    @FXML
    protected void saveAndNew(ActionEvent event) throws SQLException, IOException {
        Credit credit = new Credit();
        credit.insertNewCredit(type, this.amountTextField.getText(), null, this.memoTextField.getText(), formatter.format(this.paymentDatePicker.getValue()), this.applyToComboBox.getSelectionModel().getSelectedItem().toString().split(" - ")[0], this.paymentMethod, clients.clientID(this.receiveFromComboBox.getSelectionModel().getSelectedItem().toString()), creditIdLabel);
        
        CreditMain creditMain = new CreditMain();
        creditMain.isNew = true;
        creditMain.start((Stage) this.amountTextField.getScene().getWindow());
    }
    /**
     * Save and close the current payment.
     * @param event 
     */
    @FXML
    protected void saveAndClose(ActionEvent event) throws SQLException {
        Credit credit = new Credit();
        credit.insertNewCredit(type, this.amountTextField.getText(), null, this.memoTextField.getText(), formatter.format(this.paymentDatePicker.getValue()), this.applyToComboBox.getSelectionModel().getSelectedItem().toString().split(" - ")[0], this.paymentMethod, clients.clientID(this.receiveFromComboBox.getSelectionModel().getSelectedItem().toString()), creditIdLabel);
        
        
    }
    
    private void currentBalance(String clientId) throws SQLException{
       Client client = new Client();
       this.currentBalanceLabel.setText(client.getCurrentBalance(clientId));
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    protected void setPaymentType(ActionEvent event){
        switch(event.getSource().toString()){
            case "cashButton":
                this.paymentMethod = "Cash";
                break;
            case "creditButton":
                this.paymentMethod = "Credit Card";
                break;
            case "payPalButton":
                this.paymentMethod = "PayPal";
                break;
            case "checkButton":
                this.paymentMethod = "Check";
                break;
            default: break;
                
        }
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Set the default value of the date picker to today.
        this.paymentDatePicker.setValue(LocalDate.now());
                
        // Set the format of the amount textfield
        this.amountTextField.setTextFormatter(new TextFormatter<>(new NumberStringConverter(Locale.US)));
        
        // Set the custom receive from combo box
        try{
            this.receiveFromComboBox.getItems().setAll(clients.clientList());
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        
        // If this is a new receipt then the apply to check combo box will be disabled
        if(this.isNew){
            System.out.println("Is New");
            this.applyToComboBox.setDisable(true);
        }
        
        // When the receive from combo box value changes set the apply to check combo box
        this.receiveFromComboBox.valueProperty().addListener((obs,oldValue,newValue) -> {
            try{
                this.clientId = clients.clientID(this.receiveFromComboBox.getSelectionModel().getSelectedItem().toString());
                currentBalance(this.clientId);
                this.applyToComboBox.setDisable(false);
                this.applyToComboBox.getItems().setAll(invoices.getInvoices(this.clientId, false));
            }catch(SQLException ex){
                System.out.println(ex.getMessage());
            }   
        });
    }

}
