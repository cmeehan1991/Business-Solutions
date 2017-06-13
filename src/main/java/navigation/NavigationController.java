/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation;

import accounting.AccountingMain;
import customers.Client;
import customers.CustomersMain;
import invoices.Invoice;
import invoices.InvoiceMain;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import projects.ProjectsMain;
import quotes.Quotes;
import quotes.QuotesMain;
import scheduler.SchedulerMain;

/**
 *
 * @author cmeehan
 */
public class NavigationController implements Initializable {

    public String userID;
    @FXML
    private Label label;
    @FXML
    private Hyperlink newInvoiceLink, addEditInvoiceLink, timeEntryLink;

    @FXML
    protected void scheduler(ActionEvent event) throws IOException {
        SchedulerMain scheduler = new SchedulerMain();
        scheduler.start(new Stage());
    }

    @FXML
    protected void addNewProject(ActionEvent event) throws IOException {
        ProjectsMain projectsMain = new ProjectsMain();
        projectsMain.typeNew = true;
        projectsMain.userID = this.userID;
        projectsMain.start(new Stage());
    }

    @FXML
    protected void viewEditProject(ActionEvent event) throws IOException {
        ProjectsMain projectsMain = new ProjectsMain();
        projectsMain.typeNew = false;
        projectsMain.userID = this.userID;
        projectsMain.start(new Stage());
    }

    @FXML
    protected void createNewInvoice(ActionEvent even) throws IOException, SQLException {
        InvoiceMain invoiceMain = new InvoiceMain();
        invoiceMain.userID = this.userID;
        invoiceMain.isNew = true;
        invoiceMain.start(new Stage());
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
        clientBox.valueProperty().addListener((listener) -> {
            if (clientBox.getSelectionModel().getSelectedIndex() > -1) {
                invoiceBox.setDisable(false);
                try {
                    invoiceBox.getItems().setAll(invoice.invoices(client.clientID(clientBox.getSelectionModel().getSelectedItem().toString())));
                } catch (SQLException ex) {
                    Logger.getLogger(NavigationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
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

        invoiceBox.valueProperty().addListener((listener) -> {
            if (invoiceBox.getSelectionModel().getSelectedIndex() >= 0) {
                okButton.setDisable(false);
            }
        });
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return invoiceBox.getSelectionModel().getSelectedItem().toString();
            } else {
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
        if (result != null && !result.get().equals("cancel")) {
            InvoiceMain invoiceMain = new InvoiceMain();
            invoiceMain.clientID = client.clientID(clientBox.getSelectionModel().getSelectedItem().toString());
            invoiceMain.invoiceID = invoiceBox.getSelectionModel().getSelectedItem().toString().split(" - ")[0];
            invoiceMain.isNew = false;
            invoiceMain.userID = this.userID;
            invoiceMain.start(new Stage());
        }
    }

    @FXML
    protected void newQuote(ActionEvent event) throws IOException, SQLException {
        Client client = new Client();
        ChoiceDialog dialog = new ChoiceDialog();
        dialog.getItems().setAll(client.clientList());
        Optional<String> result = dialog.showAndWait();
        if (result.get() != null && result.get().length() > 0) {
            QuotesMain quotes = new QuotesMain();
            quotes.client = client.clientID(result.get());
            quotes.quoteType = "new";
            quotes.start(new Stage());
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error!");
            alert.setHeaderText("You have to choose a client!");
            alert.setContentText("You have to choose an existing client to create a quote. If the client does not exist then create a new client first and then come back.");
            alert.showAndWait();
        }
    }

    @FXML
    protected void updateQuote(ActionEvent event) throws IOException, SQLException {
        Client client = new Client();
        Quotes quotes = new Quotes();

        ComboBox clientBox = new ComboBox();
        ComboBox quoteBox = new ComboBox();
        quoteBox.setDisable(true);
        quoteBox.getSelectionModel().select(-1);
        clientBox.getItems().addAll(client.clientList());
        clientBox.getSelectionModel().select(-1);
        clientBox.valueProperty().addListener((listener) -> {
            if (clientBox.getSelectionModel().getSelectedIndex() > -1) {
                quoteBox.setDisable(false);
                try {
                    quoteBox.getItems().setAll(quotes.quotes(client.clientID(clientBox.getSelectionModel().getSelectedItem().toString())));
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                quoteBox.setDisable(true);
                quoteBox.getSelectionModel().select(-1);
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

        quoteBox.valueProperty().addListener((listener) -> {
            if (quoteBox.getSelectionModel().getSelectedIndex() >= 0) {
                okButton.setDisable(false);
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return quoteBox.getSelectionModel().getSelectedItem().toString();
            } else {
                return "cancel";
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10.0);
        grid.setVgap(10.0);

        grid.add(new Label("Client:"), 0, 0);
        grid.add(clientBox, 1, 0);

        grid.add(new Label("Invoice:"), 0, 1);
        grid.add(quoteBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<String> result = dialog.showAndWait();
        if (result != null && !result.get().equals("cancel")) {
            QuotesMain quotesMain = new QuotesMain();
            quotesMain.client = client.clientID(clientBox.getSelectionModel().getSelectedItem().toString());
            quotesMain.quoteID = quoteBox.getSelectionModel().getSelectedItem().toString().split(" - ")[0];
            quotesMain.quoteType = "update";
            quotesMain.start(new Stage());
        }
    }

    @FXML
    protected void addNewCustomer(ActionEvent event) throws IOException, SQLException {
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
    protected void viewEditCustomer(ActionEvent event) throws IOException, SQLException {

        ChoiceDialog dialog = new ChoiceDialog();
        dialog.setTitle("Edit/View Client");
        dialog.setHeaderText("Edit/View an Existing Client");
        dialog.setContentText("Client Name:");
        dialog.getItems().setAll(new Client().clientList());
        Optional<String> result = dialog.showAndWait();
        Client client = new Client();
        if (client.clientExists(result.get())) {
            CustomersMain main = new CustomersMain();
            main.companyName = result.get();
            main.loadClient();
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Client Not Found");
            alert.setHeaderText(("Client Not Found"));
            alert.setContentText("The client you entered does not match anything we have on record. Please try again or add a new client.");
            alert.showAndWait();
        }
    }

    @FXML
    private void accounting(ActionEvent event) throws IOException{
        AccountingMain accountingMain = new AccountingMain();
        accountingMain.start(new Stage());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
