/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quotes;

import customers.Client;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import notifications.Toast;
import output.QuotePDF;
import quotes.QuoteItemizationTable.QuoteItems;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class QuotesFXMLController implements Initializable {

    private Client client = new Client();
    public boolean saved = false;
    private NumberFormat currency = NumberFormat.getCurrencyInstance();

    @FXML
    TextField quoteIDTextField, typeDescriptionTextField, webHostTextField, urlTextField, budgetTextField;

    @FXML
    ComboBox clientComboBox, projectTypeComboBox;

    @FXML
    DatePicker startDatePicker, completionDatePicker;

    @FXML
    CheckBox deadlineCheckBox, ownedCheckBox, flexibleCheckBox;

    @FXML
    TextArea notesTextArea;

    @FXML
    TableView quoteItemizationTable;

    @FXML
    RadioButton typePersonal, typeBusiness;

    @FXML
    ToggleGroup siteTypeGroup;

    /**
     * Check for the required inputs.
     *
     * @return
     */
    private boolean confirmRequiredFields() {
        // Show alert box
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Missing required items");
        alert.setHeaderText("You are missing a required field.");
        // Check the items
        if (clientComboBox.getSelectionModel().getSelectedIndex() == -1) {
            alert.setContentText("Missing Field: Client");
            alert.showAndWait();
            return false;
        }
        if (projectTypeComboBox.getSelectionModel().getSelectedIndex() == -1) {
            alert.setContentText("Missing Field: Project Type");
            alert.showAndWait();
            return false;
        }
        if (budgetTextField.getText().isEmpty()) {
            budgetTextField.setText("N/A");
        }
        if (startDatePicker.getValue() == null) {
            alert.setContentText("Missing Field: Start Date");
            alert.showAndWait();
            return false;
        }
        if (completionDatePicker.getValue() == null) {
            alert.setContentText("Missing Field: Client");
            alert.showAndWait();
            return false;
        }
        if (!typePersonal.isSelected() && !typeBusiness.isSelected()) {
            alert.setContentText("Missing Field: Type");
            alert.showAndWait();
            return false;
        }
        if (quoteItemizationTable.getItems().isEmpty()) {
            alert.setContentText("Missing Field: Quote Table");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Create a new quote from the quotes page.
     *
     * @param event
     * @throws IOException
     * @throws SQLException
     */
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

    /**
     * Delete the quote.
     *
     * @param event
     */
    @FXML
    protected void deleteQuote(ActionEvent event) {

    }

    /**
     * Email the quote.
     *
     * @param event
     * @throws SQLException
     */
    @FXML
    protected void emailQuote(ActionEvent event) throws SQLException {
        // Get the quote input values
        String quoteID = this.quoteIDTextField.getText();
        String projectType = (projectTypeComboBox.getSelectionModel().getSelectedIndex() > -1) ? projectTypeComboBox.getSelectionModel().getSelectedItem().toString() : "N/A";
        String clientName = (clientComboBox.getSelectionModel().getSelectedIndex() > -1) ? clientComboBox.getSelectionModel().getSelectedItem().toString() : "N/A";
        String startDate = (startDatePicker.getValue() != null) ? startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A";
        String completionDate = (completionDatePicker.getValue() != null) ? completionDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "N/A";
        boolean deadline = deadlineCheckBox.isSelected();
        String type = (typePersonal.isSelected()) ? "Personal" : "Business";
        String typeDescription = typeDescriptionTextField.getText();
        String webHost = webHostTextField.getText();
        String url = urlTextField.getText();
        String owned = (ownedCheckBox.isSelected()) ? "Owned" : "Not Owned";
        String budget = (!budgetTextField.getText().isEmpty()) ? currency.format(Double.parseDouble(this.budgetTextField.getText())) : "N/A";
        boolean flexible = flexibleCheckBox.isSelected();
        TableView itemizationTable = quoteItemizationTable;
        String notes = notesTextArea.getText();

        if (confirmRequiredFields()) {

            // Use a text dialog to get the email information
            // Only allow one email recipient
            TextInputDialog recipient = new TextInputDialog();
            recipient.setTitle("Main To");
            recipient.setHeaderText("Set the recipient.");
            recipient.setContentText("To:");
            recipient.getEditor().setText(client.getClientEmail(client.clientID(clientName)));

            // Show the dialog and wait for the user to confirm entry
            Optional<String> response = recipient.showAndWait();
            if (!response.get().isEmpty() && response.get().contains("@")) {
                QuotePDF quotePDF = new QuotePDF();
                quotePDF.quoteID = quoteID;
                quotePDF.projectType = projectType;
                quotePDF.clientName = clientName;
                quotePDF.startDate = startDate;
                quotePDF.completionDate = completionDate;
                quotePDF.deadLine = deadline;
                quotePDF.siteType = type;
                quotePDF.siteDescription = typeDescription;
                quotePDF.host = webHost;
                quotePDF.url = url;
                quotePDF.owned = owned;
                quotePDF.budget = budget;
                quotePDF.flexible = flexible;
                quotePDF.notes = notes;
                quotePDF.quoteTableView = quoteItemizationTable;

                // Email the PDF
                quotePDF.emailPDF(response.get());
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Invalid Email Address");
                alert.setHeaderText("Invalid Email Address");
                alert.setContentText("It looks like you entered an invalide email address. Please try again.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    protected void printQuote(ActionEvent event) {

    }

    /**
     * Save the quote.
     *
     * @param event
     * @throws SQLException
     */
    @FXML
    protected void saveQuote(ActionEvent event) throws SQLException {
        Quotes quotes = new Quotes();
        quotes.quoteID = quoteIDTextField.getText();
        quotes.projectType = projectTypeComboBox.getSelectionModel().getSelectedItem().toString();
        quotes.clientName = clientComboBox.getSelectionModel().getSelectedItem().toString();
        quotes.startDate = startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        quotes.completionDate = completionDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        quotes.deadline = this.deadlineCheckBox.isSelected();
        quotes.type = (typePersonal.isSelected()) ? "personal" : "business";
        quotes.typeDescription = typeDescriptionTextField.getText();
        quotes.webHost = this.webHostTextField.getText();
        quotes.url = this.urlTextField.getText();
        quotes.owned = this.ownedCheckBox.isSelected();
        quotes.budget = this.budgetTextField.getText();
        quotes.flexible = this.flexibleCheckBox.isSelected();
        quotes.itemizationTable = this.quoteItemizationTable;
        quotes.notes = this.notesTextArea.getText();
        if (confirmRequiredFields()) {
            if (quotes.saveQuote()) {
                Stage ownerStage = (Stage) quoteIDTextField.getScene().getWindow();
                Toast.makeText(ownerStage, "The invoice has been saved.", 2500, 500, 500);
                String quoteID = (quoteIDTextField.getText().contains("-")) ? quoteIDTextField.getText().split("-")[0].trim() + String.valueOf(Integer.parseInt(quoteIDTextField.getText().split("-")[1]) + 1) : quoteIDTextField.getText() + "-1";
                this.quoteIDTextField.setText(quoteID);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error saving the quote.");
                alert.setContentText("The quote did not save successfully. Please try again.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Export the quote to the desktop. Note that this does not save the quote.
     *
     * @param event
     * @throws IOException
     * @throws java.io.FileNotFoundException
     * @throws java.net.MalformedURLException
     * @throws java.sql.SQLException
     */
    @FXML
    protected void exportQuote(ActionEvent event) throws IOException, FileNotFoundException, MalformedURLException, SQLException {
        QuotePDF pdf = new QuotePDF();
        pdf.quoteID = this.quoteIDTextField.getText();
        pdf.clientName = this.clientComboBox.getSelectionModel().getSelectedItem().toString();
        pdf.projectType = this.projectTypeComboBox.getSelectionModel().getSelectedItem().toString();
        pdf.startDate = this.startDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        pdf.completionDate = this.completionDatePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        pdf.siteType = (this.typePersonal.isSelected()) ? "Personal" : "Business";
        pdf.siteDescription = this.typeDescriptionTextField.getText();
        pdf.host = this.webHostTextField.getText();
        pdf.url = this.urlTextField.getText();
        pdf.owned = (this.ownedCheckBox.isSelected()) ? "Owned" : "Not Owned";
        pdf.budget = (!this.budgetTextField.getText().equals("") && this.budgetTextField.getText() != null) ? currency.format(Double.parseDouble(this.budgetTextField.getText())) : "N/A";
        pdf.flexible = this.flexibleCheckBox.isSelected();
        pdf.notes = this.notesTextArea.getText();
        pdf.quoteTableView = this.quoteItemizationTable;
        if (confirmRequiredFields()) {
            pdf.createPDF();
        }

    }

    /**
     * Remove the currently selected row from the table.
     *
     * @param event
     */
    @FXML
    protected void removeRow(ActionEvent event) {
        ObservableList<QuoteItems> items = this.quoteItemizationTable.getSelectionModel().getSelectedItems();
        this.quoteItemizationTable.getItems().removeAll(items);
    }

    /**
     * Add a new row to the table.
     *
     * @param event
     */
    @FXML
    protected void addNewRow(ActionEvent event) {
        ObservableList<QuoteItems> items = FXCollections.observableArrayList(new QuoteItems(null, "0.00", null, "0.00", null));
        this.quoteItemizationTable.getItems().addAll(items);
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        TableView tableView = this.quoteItemizationTable.getSelectionModel().getTableView();
        tableView.setEditable(true);
        new QuoteItemizationTable(tableView);

        try {
            this.clientComboBox.getItems().setAll(client.clientList());
        } catch (SQLException ex) {
            Logger.getLogger(QuotesFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.projectTypeComboBox.getItems().setAll(FXCollections.observableArrayList("Mobile Application - iOS", "Mobile Application - Android", "Other", "Social Media Marketing", "Software - New", "Software - Existing", "Website - New", "Website - Existing"));
    }

}
