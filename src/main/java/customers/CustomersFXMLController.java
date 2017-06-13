/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers;

import connections.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class CustomersFXMLController implements Initializable {

    int companyID, userID;
    String[] stateList = {"AK", "AL", "AR", "AZ", "CA", "CO", "CT", "DC", "DE", "FL", "GA", "GU", "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME", "MH", "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", "NH", "NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "PR", "PW", "RI", "SC", "SD", "TN", "TX", "UT", "VA", "VI", "VT", "WA", "WI", "WV", "WY"};

    @FXML
    TextField companyNameTextField, firstNameTextField, lastNameTextField, primaryStreetAddressTextField, secondaryStreetAddressTextField, cityTextField, zipCodeTextField, emailAddressTextField, mobileTextField, homeTextField, workTextField, faxTextField, websiteURLTextField, webHostTextField;

    @FXML
    ComboBox stateComboBox, countryComboBox;

    @FXML
    RadioButton individualTypeRadioButton, businessTypeRadioButton;

    @FXML
    ToggleGroup clientTypeGroup;
    
    @FXML
    TableView invoiceTable;

    /**
     * Here we are going to save the basic client information. This does not
     * save any adjustments to invoices or billings, only contact information,
     * etc.
     *
     * @param event
     */
    @FXML
    protected void saveClientInformation(ActionEvent event) {
        Connection conn = new DBConnection().connect();
        String sql = "UPDATE ALL_CUSTOMERS SET COMPANY_NAME = ?, PRIMARY_CONTACT_FIRST_NAME = ?, PRIMARY_CONTACT_LAST_NAME = ?, PRIMARY_STREET_ADDRESS = ?, SECONDARY_STREET_ADDRESS = ?, CITY = ?, STATE = ?, ZIPCODE = ?, COUNTRY = ?, EMAIL_ADDRESS = ?, MOBILE_PHONE = ?, HOME_PHONE = ?, WORK_PHONE = ?, FAX_NUMBER = ?, WEBSITE_URL = ?, WEB_HOST = ?, INDIVIDUAL_TYPE = ?, BUSINESS_TYPE = ? WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, companyNameTextField.getText());
            ps.setString(2, firstNameTextField.getText());
            ps.setString(3, lastNameTextField.getText());
            ps.setString(4, primaryStreetAddressTextField.getText());
            ps.setString(5, secondaryStreetAddressTextField.getText());
            ps.setString(6, cityTextField.getText());
            ps.setString(7, stateComboBox.getSelectionModel().getSelectedItem().toString());
            ps.setString(8, zipCodeTextField.getText());
            ps.setString(9, countryComboBox.getSelectionModel().getSelectedItem().toString());
            ps.setString(10, emailAddressTextField.getText());
            ps.setString(11, mobileTextField.getText());
            ps.setString(12, homeTextField.getText());
            ps.setString(13, workTextField.getText());
            ps.setString(14, faxTextField.getText());
            ps.setString(15, websiteURLTextField.getText());
            ps.setString(16, webHostTextField.getText());
            ps.setBoolean(17, individualTypeRadioButton.isSelected());
            ps.setBoolean(18, businessTypeRadioButton.isSelected());
            ps.setInt(19, this.companyID);
            System.out.println("Company ID: " + this.companyID);
            int rs = ps.executeUpdate();

            // Notify the user whether or not the update was successful.
            if (rs > 0) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Saved!");
                alert.setHeaderText("Customer Data Saved");
                alert.setContentText("The client's data was saved successfully.");
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Error saving client's data");
            alert.setContentText("There was an error updating the client's data.");
            alert.showAndWait();
        }
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
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            this.countryComboBox.getItems().add(obj.getDisplayCountry());
        }

        // Set state list items
        this.stateComboBox.getItems().addAll(Arrays.asList(this.stateList));

    }

}
