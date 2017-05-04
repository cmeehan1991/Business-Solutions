/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoices;

import customers.Client;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class InvoiceMain extends Application {
    public String invoiceID, clientID;
    public boolean isNew;
    
    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("InvoiceFXML.fxml"));
        Parent root = (Parent) loader.load();
        InvoiceController controller = (InvoiceController)loader.getController();
        
        Client client = new Client();
        Invoice invoice = new Invoice();
        
        controller.clientNameComboBox.getSelectionModel().select(client.getClient(this.clientID));
        controller.isNew = this.isNew;
        
        // If this is not a new invoice then lets get all of the invoice data and 
        // pass it along to the controller.
        if(!this.isNew){
            String versionNumber = null;
            if(!invoice.getLastVersion(invoiceID).equals("0")){
                System.out.println("Equals 0");
                versionNumber = invoice.getLastVersion(invoiceID);
                controller.invoiceIDTextField.setText(invoiceID + "-" + invoice.getLastVersion(invoiceID));
            }else{
                controller.invoiceIDTextField.setText(invoiceID);
            }
            controller.projectTypeComboBox.getSelectionModel().select(invoice.getInvoiceType(invoiceID));
            controller.invoiceTableView.getItems().setAll(invoice.getInvoiceItems(invoiceID, versionNumber));
            controller.projectNameTextField.setText(invoice.getInvoiceName(invoiceID));
        }else{
            controller.invoiceIDTextField.setText(invoiceID);
        }
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle(invoiceID);
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
