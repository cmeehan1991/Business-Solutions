/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.customers.Client;
import com.cbmwebdevelopment.invoices.Invoice;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class CreditMain extends Application {
    public boolean isNew;
    public String transactionId, amount, status, notes, dateCreated, paymentMethod, receivedFrom, appliedTo;
    private final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance();
    Client client = new Client();
    Invoice invoices = new Invoice();
    Credit credit = new Credit();
    Accounting accounting = new Accounting();
    
    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/CreditFXML.fxml"));
        Parent root = (Parent) loader.load();
        CreditFXMLController controller = (CreditFXMLController) loader.getController();
        
        if(isNew){
            controller.isNew = true;
            controller.creditIdLabel.setVisible(false);
            controller.creditIdLabelLabel.setVisible(false);
            controller.amountDueLabel.setText("$0.00");
            controller.amountAppliedLabel.setText("$0.00");
            controller.currentBalanceLabel.setText("$0.00");
            controller.discountsLabel.setText("$0.00");
        }else{
            controller.isNew = false;
            String companyName = client.getClient(this.receivedFrom);
            Double totalDueOnAccount = credit.getTotalAmountInvoiced(this.receivedFrom) - credit.getTotalTransactions(this.receivedFrom, "Credit");
            Double totalDueOnInvoice = accounting.getInvoiceAmount(this.receivedFrom, this.appliedTo) - accounting.getInvoiceAmountPaid(this.appliedTo);
            controller.creditIdLabel.setText(this.transactionId);
            controller.receiveFromComboBox.getSelectionModel().select(client.getClient(this.receivedFrom));
            controller.applyToComboBox.getItems().setAll(invoices.getInvoices(this.receivedFrom, false));
            controller.applyToComboBox.getSelectionModel().select(this.appliedTo + " - " + invoices.getInvoiceName(this.appliedTo));
            controller.amountAppliedLabel.setText(this.amount);
            controller.amountTextField.setText(this.amount);
            controller.amountDueLabel.setText(CURRENCY.format(totalDueOnInvoice));
            controller.currentBalanceLabel.setText(CURRENCY.format(totalDueOnAccount));
            controller.memoTextField.setText(this.notes);
        }
        
        
        Scene scene = new Scene(root);
        
        String title = this.isNew ? "New Credit": transactionId;
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
