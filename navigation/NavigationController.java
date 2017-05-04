/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation;

import customers.Client;
import customers.CustomersMain;
import invoices.Invoice;
import invoices.InvoiceMain;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class NavigationController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private Hyperlink newInvoiceLink, addEditInvoiceLink, timeEntryLink;

    @FXML
    protected void createNewInvoice(ActionEvent even) throws IOException, SQLException {
         ChoiceDialog dialog = new ChoiceDialog();
        dialog.setTitle("Edit/View Client");
        dialog.setHeaderText("Edit/View an Existing Client");
        dialog.setContentText("Client Name:");
        dialog.getItems().setAll(new Client().clientList());
        Optional<String> result = dialog.showAndWait();
        Client client = new Client();
        if(client.clientExists(result.get())){
           InvoiceMain invoiceMain = new InvoiceMain();
           Invoice invoice = new Invoice();
           invoiceMain.invoiceID = invoice.createInvoice(client.clientID(result.get()));
           invoiceMain.clientID = client.clientID(result.get());
           invoiceMain.isNew = true;
           invoiceMain.start(new Stage());
           
        }else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Client Not Found");
            alert.setHeaderText(("Client Not Found"));
            alert.setContentText("The client you entered does not match anything we have on record. Please try again or add a new client.");
            alert.showAndWait();
        }
    }

    @FXML
    protected void updateInvoice(ActionEvent event) throws IOException, SQLException {
        Client client = new Client();
        Invoice invoice = new Invoice();
       
       ComboBox clientBox = new ComboBox();
       ComboBox invoiceBox = new ComboBox();
       invoiceBox.setDisable(true);
       invoiceBox.getSelectionModel().select(-1);
       clientBox.getItems().addAll(client.clientList());
       clientBox.getSelectionModel().select(-1);
       clientBox.valueProperty().addListener((listener)->{
           if(clientBox.getSelectionModel().getSelectedIndex() > -1){
               invoiceBox.setDisable(false);
               invoiceBox.getItems().setAll(invoice.invoices(client.clientID(clientBox.getSelectionModel().getSelectedItem().toString())));
           }else{
               invoiceBox.setDisable(true);
               invoiceBox.getSelectionModel().select(-1);
           }
       });
       
       // Set up the dialog box
       Dialog<String> dialog = new Dialog<>();
       dialog.getDialogPane().getButtonTypes().clear();
       
       ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
       dialog.getDialogPane().getButtonTypes().add(okButtonType);
       
       ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
       dialog.getDialogPane().getButtonTypes().add(cancelButton);
       
       Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
       okButton.setDisable(true);
       
       invoiceBox.valueProperty().addListener((listener)->{
           if(invoiceBox.getSelectionModel().getSelectedIndex() >= 0){
               okButton.setDisable(false);
           }
       });
       
       dialog.setResultConverter(dialogButton -> {
           if(dialogButton == okButtonType){
               return invoiceBox.getSelectionModel().getSelectedItem().toString();
           }else{
               return "cancel";
           }
       });
              
       GridPane grid = new GridPane();
       grid.setHgap(10.0);
       grid.setVgap(10.0);
       
       grid.add(new Label("Client:"), 0, 0);
       grid.add(clientBox, 1, 0);
       
       
       grid.add(new Label("Invoice:"), 0, 1);
       grid.add(invoiceBox, 1, 1);
       
       dialog.getDialogPane().setContent(grid);
       
       Optional<String> result = dialog.showAndWait();
       if(result != null && !result.get().equals("cancel")){
            InvoiceMain invoiceMain = new InvoiceMain();
            invoiceMain.clientID = client.clientID(clientBox.getSelectionModel().getSelectedItem().toString());
            invoiceMain.invoiceID = invoiceBox.getSelectionModel().getSelectedItem().toString().split(" - ")[0];
            invoiceMain.isNew = false;
            invoiceMain.start(new Stage());
       }       
    }

    @FXML
    protected void timeEntry(ActionEvent event) {

    }

    @FXML
    protected void addNewCustomer(ActionEvent event) throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Client");
        dialog.setHeaderText("Add a New Client");
        dialog.setContentText("Enter Client Name:");

        Optional<String> result = dialog.showAndWait();
        Client client = new Client();
        if (!client.clientExists(result.get())) {
            int customerID = client.createClient(result.get());
            if (customerID != 0) {
                CustomersMain customersMain = new CustomersMain();
                customersMain.customerID = customerID;
                customersMain.companyName = result.get();
                customersMain.start(new Stage());
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error Creating Client");
                alert.setContentText("An error occured when trying to create the client. Please try again.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Client Exists");
            alert.setHeaderText("Client Already Exists");
            alert.setContentText("That client already exists.");
            alert.showAndWait();
        }
    }

    @FXML
    protected void viewEditCustomer(ActionEvent event) throws IOException {

        ChoiceDialog dialog = new ChoiceDialog();
        dialog.setTitle("Edit/View Client");
        dialog.setHeaderText("Edit/View an Existing Client");
        dialog.setContentText("Client Name:");
        dialog.getItems().setAll(new Client().clientList());
        Optional<String> result = dialog.showAndWait();
        Client client = new Client();
        if(client.clientExists(result.get())){
           CustomersMain main = new CustomersMain();
           main.companyName = result.get();
           main.loadClient();
        }else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Client Not Found");
            alert.setHeaderText(("Client Not Found"));
            alert.setContentText("The client you entered does not match anything we have on record. Please try again or add a new client.");
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
