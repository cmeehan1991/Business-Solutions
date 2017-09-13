/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.customers.Client;
import com.cbmwebdevelopment.invoices.Invoice;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class CreditFXMLController implements Initializable {

    Client clients = new Client();
    Invoice invoices = new Invoice();
    Accounting accounting = new Accounting();
    String paymentMethod;

    boolean isNew;
    String clientId;

    @FXML
    TextField amountTextField, memoTextField;

    @FXML
    ComboBox receiveFromComboBox, applyToComboBox;

    @FXML
    ToggleGroup underpaymentGroup, paymentMethodGroup;

    @FXML
    RadioButton leaveRadioButton, writeOffRadioButton, creditRadioButton, payPalRadioButton, checkRadioButton, cashRadioButton;

    @FXML
    Label currentBalanceLabel, creditIdLabelLabel, creditIdLabel, amountDueLabel, amountAppliedLabel, discountsLabel, charsLabel;

    @FXML
    DatePicker paymentDatePicker;

    @FXML
    TableView paymentsTabledbyq0, tableView;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String type;

    /**
     * Open a new customer payment form in a new stage.
     *
     * @param event
     * @throws java.sql.SQLException
     */
    @FXML
    protected void newPayment(ActionEvent event) throws SQLException {
        Credit credit = new Credit();
        credit.insertNewCredit(type, this.amountTextField.getText(), null, this.memoTextField.getText(), formatter.format(this.paymentDatePicker.getValue()), this.applyToComboBox.getSelectionModel().getSelectedItem().toString().split(" - ")[0], this.paymentMethod, clients.clientID(this.receiveFromComboBox.getSelectionModel().getSelectedItem().toString()), creditIdLabel);
    }

    /**
     * Export the payment receipt to the user's machine.
     *
     * @param event
     */
    @FXML
    protected void exportReceipt(ActionEvent event) {

    }

    /**
     * Email the payment receipt to a particular email address.
     *
     * @param event
     */
    @FXML
    protected void emailReceipt(ActionEvent event) {

    }

    /**
     * Save the current payment and create a new payment.
     *
     * @param event
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    @FXML
    protected void saveAndNew(ActionEvent event) throws SQLException, IOException {
        Credit credit = new Credit();
        credit.insertNewCredit(type, this.amountTextField.getText(), null, this.memoTextField.getText(), formatter.format(this.paymentDatePicker.getValue()), this.applyToComboBox.getSelectionModel().getSelectedItem().toString().split(" - ")[0], this.paymentMethod, clients.clientID(this.receiveFromComboBox.getSelectionModel().getSelectedItem().toString()), creditIdLabel);

        CreditMain creditMain = new CreditMain();
        creditMain.isNew = true;
        creditMain.start((Stage) this.amountTextField.getScene().getWindow());
        
        // Update the table
        if(tableView != null){
            
        }
    }

    /**
     * Save and close the current payment.
     *
     * @param event
     * @throws java.sql.SQLException
     */
    @FXML
    protected void saveAndClose(ActionEvent event) throws SQLException {
        Credit credit = new Credit();
        credit.insertNewCredit(type, this.amountTextField.getText(), null, this.memoTextField.getText(), formatter.format(this.paymentDatePicker.getValue()), this.applyToComboBox.getSelectionModel().getSelectedItem().toString().split(" - ")[0], this.paymentMethod, clients.clientID(this.receiveFromComboBox.getSelectionModel().getSelectedItem().toString()), creditIdLabel);
        
        // Update the table
        
        // Close the window
        Stage currentStage = (Stage) this.amountTextField.getScene().getWindow();
        currentStage.close();
    }

    private void currentBalance(String clientId) throws SQLException {
        Client client = new Client();
        this.currentBalanceLabel.setText((clientId));
    }

    /**
     *
     * @param event
     */
    @FXML
    protected void setPaymentType(ActionEvent event) {
        this.paymentMethod = ((Button)event.getSource()).getText();
        System.out.println(((Button)event.getSource()).getText());
        
    }

    /**
     * Initializes the controller class.
     *
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
        try {
            this.receiveFromComboBox.getItems().setAll(clients.clientList());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // If this is a new receipt then the apply to check combo box will be disabled
        if (this.isNew) {
            this.applyToComboBox.setDisable(true);
        }

        // When the receive from combo box value changes set the apply to check combo box
        this.receiveFromComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            try {
                this.clientId = clients.clientID(this.receiveFromComboBox.getSelectionModel().getSelectedItem().toString());
                currentBalance(this.clientId);
                this.applyToComboBox.setDisable(false);
                this.applyToComboBox.getItems().setAll(invoices.getInvoices(this.clientId, false));
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        });

        // Set the character counter
        this.memoTextField.setOnKeyReleased((KeyEvent event) -> {
            int maxLength = 200;
            int currentLength = this.memoTextField.getText().length();
            this.charsLabel.setText(String.valueOf(maxLength - currentLength));
            if (currentLength <= 200) {
                String text = this.memoTextField.getText().substring(0, currentLength);
                this.memoTextField.setText(text);
            }else{
                if(event.getCode() != KeyCode.DELETE){
                    return;
                }
            }
        });
    }

}
