/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoices;

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
    public String invoiceID, clientID, userID;
    public boolean isNew;
    
    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/InvoiceFXML.fxml"));
        Parent root = (Parent) loader.load();
        InvoiceController controller = (InvoiceController)loader.getController();
        
        Invoice invoice = new Invoice();
        controller.isNew = this.isNew;
        
        // If this is not a new invoice then lets get all of the invoice data and 
        // pass it along to the controller.
        if(!this.isNew){
            invoice.getInvoice(controller, this.invoiceID);
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
