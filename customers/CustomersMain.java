/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customers;

import connections.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class CustomersMain extends Application {

    public int customerID, userID;
    public String companyName;

    /**
     * Here we are going to load the basic client information. We are only
     * limiting our results to one entry in the event of multiple clients with
     * the same name.
     *
     * @throws java.io.IOException
     */
    public void loadClient() throws IOException {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, COMPANY_NAME, PRIMARY_CONTACT_FIRST_NAME, PRIMARY_CONTACT_LAST_NAME, PRIMARY_STREET_ADDRESS, SECONDARY_STREET_ADDRESS, CITY, STATE, ZIPCODE, COUNTRY, EMAIL_ADDRESS, MOBILE_PHONE, HOME_PHONE, WORK_PHONE, FAX_NUMBER, WEBSITE_URL, WEB_HOST, INDIVIDUAL_TYPE, BUSINESS_TYPE FROM ALL_CUSTOMERS WHERE COMPANY_NAME = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, this.companyName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Since we are only returning one result then we do not need to
                // loop the result set in a while loop. 
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomersFXML.fxml"));
                Parent root = (Parent) loader.load();
                CustomersFXMLController controller = (CustomersFXMLController) loader.getController();

                // Send the company ID to the controller
                controller.companyID = rs.getInt("ID");

                // Assign all of the values to the appropriate fields in the fxml
                controller.companyNameTextField.setText(rs.getString("COMPANY_NAME"));
                controller.firstNameTextField.setText(rs.getString("PRIMARY_CONTACT_FIRST_NAME"));
                controller.lastNameTextField.setText(rs.getString("PRIMARY_CONTACT_LAST_NAME"));
                controller.primaryStreetAddressTextField.setText(rs.getString("PRIMARY_STREET_ADDRESS"));
                controller.secondaryStreetAddressTextField.setText(rs.getString("SECONDARY_STREET_ADDRESS"));
                controller.cityTextField.setText(rs.getString("CITY"));
                controller.stateComboBox.getSelectionModel().select(rs.getString("STATE"));
                controller.zipCodeTextField.setText(rs.getString("ZIPCODE"));
                controller.countryComboBox.getSelectionModel().select(rs.getString("COUNTRY"));
                controller.emailAddressTextField.setText(rs.getString("EMAIL_ADDRESS"));
                controller.mobileTextField.setText(rs.getString("MOBILE_PHONE"));
                controller.homeTextField.setText(rs.getString("HOME_PHONE"));
                controller.workTextField.setText(rs.getString("WORK_PHONE"));
                controller.faxTextField.setText(rs.getString("FAX_NUMBER"));
                controller.websiteURLTextField.setText(rs.getString("WEBSITE_URL"));
                controller.webHostTextField.setText(rs.getString("WEB_HOST"));
                controller.businessTypeRadioButton.setSelected(rs.getBoolean("BUSINESS_TYPE"));
                controller.individualTypeRadioButton.setSelected(rs.getBoolean("INDIVIDUAL_TYPE"));

                // Set the default country to the United States
                if (controller.countryComboBox.getSelectionModel().getSelectedIndex() == -1) {
                    controller.countryComboBox.getSelectionModel().select("United States");
                }

                // Set the scene
                Scene scene = new Scene(root);

                // Set the stage
                Stage stage = new Stage();
                stage.setTitle("Edit/View Client Information");
                stage.setScene(scene);
                stage.show();

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error loading client data");
            alert.setContentText("There was an error loading the client data. Please try again.");
            alert.showAndWait();

        }
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomersFXML.fxml"));
        Parent root = (Parent) loader.load();

        CustomersFXMLController controller = (CustomersFXMLController) loader.getController();
        controller.companyNameTextField.setText(companyName);
        
        // Set the default country to the United States
        if (controller.countryComboBox.getSelectionModel().getSelectedIndex() == -1) {
            controller.countryComboBox.getSelectionModel().select("United States");
        }

        Scene scene = new Scene(root);

        primaryStage.setTitle("Client");
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
