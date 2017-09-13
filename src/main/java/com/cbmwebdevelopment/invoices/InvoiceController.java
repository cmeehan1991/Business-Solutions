/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.invoices;

import com.cbmwebdevelopment.accounting.CreditMain;
import com.cbmwebdevelopment.customers.Client;
import com.cbmwebdevelopment.invoices.InvoiceItemizationTable.InvoiceItems;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.cbmwebdevelopment.notifications.Toast;
import com.cbmwebdevelopment.output.InvoicePDF;
import com.cbmwebdevelopment.projects.Projects;
import com.cbmwebdevelopment.projects.ProjectsMain;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class InvoiceController implements Initializable {

    private final Client client = new Client();
    private final Projects projects = new Projects();
    private final Invoice invoice = new Invoice();
    private final List<String> projectType = new ArrayList<>();
    public boolean isNew;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd");
    private Double sum = 0.0;
    public String clientID;
    public String userID;

    TableView tableView;

    @FXML
    TextField invoiceIDTextField, invoiceNameTextField, totalDueTextField;

    @FXML
    ComboBox clientNameComboBox, invoiceTypeComboBox, projectComboBox;

    @FXML
    TextArea descriptionTextArea, billToTextArea;

    @FXML
    DatePicker paymentDueDatePicker;

    @FXML
    TableView invoiceTableView;

    @FXML
    CheckBox noChargeCheckBox;

    @FXML
    Button invoiceSumButton;

    @FXML
    Text invoiceStatusLabel;

    @FXML
    ListView paymentsReceivedList;

    /**
     * Calculates the total amount due for the invoice.
     *
     * @param event
     */
    @FXML
    protected void calculateTotalDue(ActionEvent event) {
        ObservableList<InvoiceItems> list = this.invoiceTableView.getItems();
        this.sum = 0.0;
        if (!list.isEmpty()) {
            list.forEach((items) -> {
                this.sum += Double.parseDouble(items.getCost()) * Double.parseDouble(items.getCostUnitTotal());
            });
        }
        this.totalDueTextField.setText(currencyFormat.format(sum));
    }

    /**
     * Exports the invoices to a PDF file.
     *
     * @param event
     * @throws IOException
     * @throws FileNotFoundException
     * @throws MalformedURLException
     * @throws SQLException
     */
    @FXML
    protected void exportInvoice(ActionEvent event) throws IOException, FileNotFoundException, MalformedURLException, SQLException {
        String invoiceID = this.invoiceIDTextField.getText();
        String clientName = (this.clientNameComboBox.getSelectionModel().getSelectedIndex() > -1) ? this.clientNameComboBox.getSelectionModel().getSelectedItem().toString() : "N/A";
        String project = (this.projectComboBox.getSelectionModel().getSelectedIndex() > -1) ? this.projectComboBox.getSelectionModel().getSelectedItem().toString() : "N/A";
        String invoiceType = (this.invoiceTypeComboBox.getSelectionModel().getSelectedIndex() > -1) ? this.invoiceTypeComboBox.getSelectionModel().getSelectedItem().toString() : "N/A";
        String invoiceTitle = this.invoiceNameTextField.getText();
        String notes = this.descriptionTextArea.getText();
        String billTo = this.billToTextArea.getText();
        String paymentDueDate = this.paymentDueDatePicker.getValue().toString();
        String invoiceStatus = (this.invoiceStatusLabel.getText() == null) ? null : this.invoiceStatusLabel.getText();
        String totalDue = (this.noChargeCheckBox.isSelected()) ? "0.00" : getTotalDue();

        InvoicePDF invoicePDF = new InvoicePDF();
        invoicePDF.invoiceID = invoiceID;
        invoicePDF.clientName = clientName;
        invoicePDF.project = project;
        invoicePDF.invoiceType = invoiceType;
        invoicePDF.invoiceTitle = invoiceTitle;
        invoicePDF.notes = notes;
        invoicePDF.billTo = billTo;
        invoicePDF.paymentDueDate = paymentDueDate;
        invoicePDF.invoiceStatus = invoiceStatus;
        invoicePDF.totalDue = totalDue;
        invoicePDF.invoiceTableView = this.tableView;
        invoicePDF.createPDF();
    }

    private String getTotalDue() {
        ObservableList<InvoiceItems> list = this.invoiceTableView.getItems();
        this.sum = 0.0;
        if (!list.isEmpty()) {
            list.forEach((items) -> {
                this.sum += Double.parseDouble(items.getCost()) * Double.parseDouble(items.getCostUnitTotal());
            });
        }
        return String.valueOf(sum);
    }

    @FXML
    protected void saveAndNew(ActionEvent event) throws SQLException, IOException {
        // Get the values to be saved
        String clientName = (clientNameComboBox.getSelectionModel().getSelectedIndex() > -1) ? clientNameComboBox.getSelectionModel().getSelectedItem().toString() : null;
        String invoiceID = invoiceIDTextField.getText();
        String invoiceType = (this.invoiceTypeComboBox.getSelectionModel().getSelectedIndex() > -1) ? this.invoiceTypeComboBox.getSelectionModel().getSelectedItem().toString() : null;
        String invoiceTitle = this.invoiceNameTextField.getText();
        String invoiceDescription = this.descriptionTextArea.getText();
        String projectID = (this.projectComboBox.getSelectionModel().getSelectedIndex() > -1) ? projects.getID(this.projectComboBox.getSelectionModel().getSelectedItem().toString()) : null;
        String paymentDueDate = (this.paymentDueDatePicker.getValue().toString() != null && !this.paymentDueDatePicker.getValue().toString().equals("")) ? this.paymentDueDatePicker.getValue().toString() : "0000-00-00 00:00:00";
        String billTo = this.billToTextArea.getText();
        boolean noCharge = this.noChargeCheckBox.isSelected();

        Invoice invoice = new Invoice();
        invoice.clientName = clientName;
        invoice.invoiceID = invoiceID;
        invoice.invoiceType = invoiceType;
        invoice.isNew = this.isNew;
        invoice.invoiceItemizationTable = this.invoiceTableView;
        invoice.invoiceTitle = invoiceTitle;
        invoice.invoiceDescription = invoiceDescription;
        invoice.projectID = projectID;
        invoice.paymentDueDate = paymentDueDate;
        invoice.billTo = billTo;
        invoice.noCharge = noCharge;

        // Let the user know if the invoice was saved successfully.
        if (invoice.saveInvoice()) {
            InvoiceMain invoiceMain = new InvoiceMain();
            invoiceMain.isNew = true;
            invoiceMain.userID = this.userID;
            invoiceMain.start(new Stage());
            Stage ownerStage = (Stage) invoiceIDTextField.getScene().getWindow();
            ownerStage.close();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving the invoice");
            alert.setContentText("The invoice did not save successfully. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    protected void saveAndClose(ActionEvent event) throws SQLException {
        // Get the values to be saved
        String clientName = (clientNameComboBox.getSelectionModel().getSelectedIndex() > -1) ? clientNameComboBox.getSelectionModel().getSelectedItem().toString() : null;
        String invoiceID = invoiceIDTextField.getText();
        String invoiceType = (this.invoiceTypeComboBox.getSelectionModel().getSelectedIndex() > -1) ? this.invoiceTypeComboBox.getSelectionModel().getSelectedItem().toString() : null;
        String invoiceTitle = this.invoiceNameTextField.getText();
        String invoiceDescription = this.descriptionTextArea.getText();
        String projectID = (this.projectComboBox.getSelectionModel().getSelectedIndex() > -1) ? projects.getID(this.projectComboBox.getSelectionModel().getSelectedItem().toString()) : null;
        String paymentDueDate = (this.paymentDueDatePicker.getValue().toString() != null && !this.paymentDueDatePicker.getValue().toString().equals("")) ? this.paymentDueDatePicker.getValue().toString() : "0000-00-00 00:00:00";
        String billTo = this.billToTextArea.getText();
        boolean noCharge = this.noChargeCheckBox.isSelected();

        Invoice invoice = new Invoice();
        invoice.clientName = clientName;
        invoice.invoiceID = invoiceID;
        invoice.invoiceType = invoiceType;
        invoice.isNew = this.isNew;
        invoice.invoiceItemizationTable = this.invoiceTableView;
        invoice.invoiceTitle = invoiceTitle;
        invoice.invoiceDescription = invoiceDescription;
        invoice.projectID = projectID;
        invoice.paymentDueDate = paymentDueDate;
        invoice.billTo = billTo;
        invoice.noCharge = noCharge;

        // Let the user know if the invoice was saved successfully.
        if (invoice.saveInvoice()) {
            Stage ownerStage = (Stage) invoiceIDTextField.getScene().getWindow();
            ownerStage.close();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving the invoice");
            alert.setContentText("The invoice did not save successfully. Please try again.");
            alert.showAndWait();
        }
    }

    /**
     *
     * @param event
     * @throws java.sql.SQLException
     */
    @FXML
    protected void deleteInvoice(ActionEvent event) throws SQLException {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.setDefaultButton(false);

        Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        noButton.setDefaultButton(true);

        alert.setTitle("Delete Invoice");
        alert.setHeaderText("Delete Invoice?");
        alert.setContentText("Are you sure you want to delete this invoice? This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            String invoiceID = (this.invoiceIDTextField.getText().contains("-")) ? this.invoiceIDTextField.getText().split("-")[0].trim() : this.invoiceIDTextField.getText();
            Invoice invoice = new Invoice();
            if (invoice.deleteInvoice(invoiceID)) {
                Stage stage = (Stage) this.invoiceIDTextField.getScene().getWindow();
                stage.close();
            } else {
                Alert error = new Alert(AlertType.ERROR);
                error.setTitle("Error Deleting Invoice");
                error.setHeaderText("Error encountered.");
                error.setContentText("The invoice was not successfully delete. Please try again or contact your systems administrator for assitance");
            }
        } else {
            alert.close();
        }
    }

    /**
     * Receive a payment for the invoice
     *
     * @param event
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    @FXML
    protected void receivePayment(ActionEvent event) throws IOException, SQLException {
        CreditMain creditMain = new CreditMain();
        creditMain.isNew = true;
        creditMain.start(new Stage());
    }

    /**
     * Converts the invoice to a temporary PDF stored in memory and prints it.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    protected void printInvoice(ActionEvent event) throws IOException {
        //InvoicePDF invoicePDF = new InvoicePDF();
        //invoicePDF.printPDF();
    }

    @FXML
    protected void emailInvoice(ActionEvent event) throws URISyntaxException, IOException, SQLException {
        String invoiceID = this.invoiceIDTextField.getText();
        String clientName = (this.clientNameComboBox.getSelectionModel().getSelectedIndex() > -1) ? this.clientNameComboBox.getSelectionModel().getSelectedItem().toString() : "N/A";
        String project = (this.projectComboBox.getSelectionModel().getSelectedIndex() > -1) ? this.projectComboBox.getSelectionModel().getSelectedItem().toString() : "N/A";
        String invoiceType = (this.invoiceTypeComboBox.getSelectionModel().getSelectedIndex() > -1) ? this.invoiceTypeComboBox.getSelectionModel().getSelectedItem().toString() : "N/A";
        String invoiceTitle = this.invoiceNameTextField.getText();
        String notes = this.descriptionTextArea.getText();
        String billTo = this.billToTextArea.getText();
        String paymentDueDate = this.paymentDueDatePicker.getValue().toString();
        String invoiceStatus = (this.invoiceStatusLabel.getText() == null) ? null : this.invoiceStatusLabel.getText();
        String totalDue = (this.noChargeCheckBox.isSelected()) ? "0.00" : getTotalDue();

        TextInputDialog sender = new TextInputDialog();
        sender.setTitle("Mail To");
        sender.setHeaderText("Add the recipient.");
        sender.setContentText("To:");
        sender.getEditor().setText(client.getClientEmail(client.clientID(clientName)));
        Optional<String> response = sender.showAndWait();
        if (response.get().length() > 0) {

            InvoicePDF invoicePDF = new InvoicePDF();
            invoicePDF.invoiceID = invoiceID;
            invoicePDF.clientName = clientName;
            invoicePDF.project = project;
            invoicePDF.invoiceType = invoiceType;
            invoicePDF.invoiceTitle = invoiceTitle;
            invoicePDF.notes = notes;
            invoicePDF.billTo = billTo;
            invoicePDF.paymentDueDate = paymentDueDate;
            invoicePDF.invoiceStatus = invoiceStatus;
            invoicePDF.totalDue = totalDue;
            invoicePDF.invoiceTableView = this.tableView;

            invoicePDF.emailPDF(response.get());
        }

    }

    @FXML
    protected void newInvoice(ActionEvent event) throws IOException, SQLException {
        InvoiceMain invoiceMain = new InvoiceMain();
        invoiceMain.userID = this.userID;
        invoiceMain.isNew = true;
        invoiceMain.start(new Stage());
    }

    @FXML
    protected void saveInvoice(ActionEvent event) throws SQLException {
        // Get the values to be saved
        String clientName = (clientNameComboBox.getSelectionModel().getSelectedIndex() > -1) ? clientNameComboBox.getSelectionModel().getSelectedItem().toString() : null;
        String invoiceID = invoiceIDTextField.getText();
        String invoiceType = (this.invoiceTypeComboBox.getSelectionModel().getSelectedIndex() > -1) ? this.invoiceTypeComboBox.getSelectionModel().getSelectedItem().toString() : null;
        String invoiceTitle = this.invoiceNameTextField.getText();
        String invoiceDescription = this.descriptionTextArea.getText();
        String projectID = (this.projectComboBox.getSelectionModel().getSelectedIndex() > -1) ? projects.getID(this.projectComboBox.getSelectionModel().getSelectedItem().toString()) : null;
        String paymentDueDate = (this.paymentDueDatePicker.getValue().toString() != null && !this.paymentDueDatePicker.getValue().toString().equals("")) ? this.paymentDueDatePicker.getValue().toString() : "0000-00-00 00:00:00";
        String billTo = this.billToTextArea.getText();
        boolean noCharge = this.noChargeCheckBox.isSelected();

        Invoice invoice = new Invoice();
        invoice.clientName = clientName;
        invoice.invoiceID = invoiceID;
        invoice.invoiceType = invoiceType;
        invoice.isNew = this.isNew;
        invoice.invoiceItemizationTable = this.invoiceTableView;
        invoice.invoiceTitle = invoiceTitle;
        invoice.invoiceDescription = invoiceDescription;
        invoice.projectID = projectID;
        invoice.paymentDueDate = paymentDueDate;
        invoice.billTo = billTo;
        invoice.noCharge = noCharge;

        // Let the user know if the invoice was saved successfully.
        if (invoice.saveInvoice()) {
            Stage ownerStage = (Stage) invoiceIDTextField.getScene().getWindow();
            Toast.makeText(ownerStage, "The invoice has been saved.", 2500, 500, 500);
            this.isNew = false;
            String setInvoiceID = (invoiceIDTextField.getText().contains("-")) ? invoiceIDTextField.getText().split("-")[0].trim() + "-" + String.valueOf(Integer.parseInt(invoiceIDTextField.getText().split("-")[1]) + 1) : invoiceIDTextField.getText() + "-1";
            this.invoiceIDTextField.setText(setInvoiceID);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error saving the invoice");
            alert.setContentText("The invoice did not save successfully. Please try again.");
            alert.showAndWait();
        }
    }

    @FXML
    protected void addRow(ActionEvent event) {
        ObservableList<InvoiceItems> invoiceRow = FXCollections.observableArrayList(new InvoiceItems(false, null, null, null, null, null));
        invoiceTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        invoiceTableView.getItems().addAll(invoiceRow);
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String[] projectTypes = {"Mobile Application - iOS", "Mobile Application - Android", "Other", "Social Media Marketing", "Software - New", "Software - Existing", "Website - New", "Website - Existing"};
        this.projectType.addAll(Arrays.asList(projectTypes));
        List<String> clientName;
        try {
            clientName = client.clientList();
        } catch (SQLException ex) {
            clientName = null;
            System.out.println(ex.getMessage());
        }
        this.invoiceTypeComboBox.getItems().addAll(this.projectType);
        this.clientNameComboBox.getItems().addAll(clientName);

        this.tableView = invoiceTableView.getSelectionModel().getTableView();
        tableView.setEditable(true);
        InvoiceItemizationTable invoiceItemizationTable = new InvoiceItemizationTable(tableView);

        currencyFormat.setParseIntegerOnly(true);

        this.noChargeCheckBox.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (newValue) {
                this.totalDueTextField.setText("");
                this.totalDueTextField.setEditable(false);
                this.invoiceSumButton.setDisable(true);
            } else {
                this.totalDueTextField.setText("");
                this.totalDueTextField.setEditable(true);
                this.invoiceSumButton.setDisable(false);
            }
        });

        this.clientNameComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            try {
                projectComboBox.getItems().clear();
                projectComboBox.getItems().addAll(projects.projects(client.clientID(newValue.toString())));
                projectComboBox.getItems().add(0, "<Create New Project>");

                // Get the billing information based on the client
                this.billToTextArea.setText(client.getBillingAddress(client.clientID(newValue.toString())));
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        });

        this.projectComboBox.getItems().setAll(projects.projects(this.clientID));
        this.projectComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.equals("<Create New Project>")) {
                ProjectsMain projectsMain = new ProjectsMain();
                projectsMain.userID = this.userID;
                projectsMain.typeNew = true;
                try {
                    projectsMain.start(new Stage());
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });

        // Get the payment when the mouse is double clicked
        this.paymentsReceivedList.setOnMouseReleased((MouseEvent event) -> {
            if (event.getClickCount() == 2 && (!event.isMiddleButtonDown() && !event.isSecondaryButtonDown())) {
                CreditMain creditMain = new CreditMain();
                try {
                    creditMain.isNew = false;
                    creditMain.transactionId = this.paymentsReceivedList.getSelectionModel().getSelectedItems().get(0).toString().split(" - ")[0];
                    creditMain.amount = this.paymentsReceivedList.getSelectionModel().getSelectedItems().get(0).toString().split(" - ")[1];
                    creditMain.appliedTo = this.invoiceIDTextField.getText().contains("-") ? this.invoiceIDTextField.getText().split("-")[0].trim() : this.invoiceIDTextField.getText();
                    creditMain.receivedFrom = client.clientID(this.clientNameComboBox.getSelectionModel().getSelectedItem().toString());
                    creditMain.start(new Stage());
                } catch (IOException | SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

}
