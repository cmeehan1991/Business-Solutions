/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quotes;

import customers.Client;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author cmeehan
 */
public class QuotesMain extends Application {

    public String quoteID, quoteType, client;
    private QuotesFXMLController controller;
    private Client clients = new Client();

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/QuotesFXML.fxml"));
        Parent root = (Parent) loader.load();
        this.controller = (QuotesFXMLController) loader.getController();

        // If we are editing a quote then we will pass data to the controller before we open it. 
        if (!quoteType.equals("new")) {
            String id = (quoteID.contains("-")) ? quoteID.split("-")[0] : quoteID;
            String version = (quoteID.contains("-")) ? quoteID.split("-")[1] : null;

            Quotes quotes = new Quotes();
            this.controller.quoteIDTextField.setText(quoteID);
            this.controller.clientComboBox.getSelectionModel().select(clients.getClient(client));
            this.controller.projectTypeComboBox.getSelectionModel().select(quotes.getProjectType(id));
            if (quotes.getStartDate(id) != null) {
                this.controller.startDatePicker.setValue(LocalDate.parse(quotes.getStartDate(id)));
            }
            if (quotes.getCompletionDate(id) != null) {
                this.controller.completionDatePicker.setValue(LocalDate.parse(quotes.getCompletionDate(id)));
            }
            this.controller.deadlineCheckBox.setSelected(quotes.hasDeadline(id));
            this.controller.typePersonal.setSelected(quotes.isPersonal(id));
            this.controller.typeBusiness.setSelected(quotes.isBusiness(id));
            this.controller.typeDescriptionTextField.setText(quotes.getTypeDescription(id));
            this.controller.webHostTextField.setText(quotes.getWebHost(id));
            this.controller.urlTextField.setText(quotes.getURL(id));
            this.controller.ownedCheckBox.setSelected(quotes.isOwned(id));
            this.controller.budgetTextField.setText(quotes.getBudget(id));
            this.controller.flexibleCheckBox.setSelected(quotes.isFlexible(id));
            this.controller.quoteItemizationTable.getItems().setAll(quotes.getItemizationTable(id));
            this.controller.notesTextArea.setText(quotes.getNotes(id));

        } else {
            Quotes quotes = new Quotes();
            this.controller.quoteIDTextField.setText(quotes.createQuote(Integer.parseInt(this.client)));
            this.controller.clientComboBox.getSelectionModel().select(clients.getClient(client));
        }

        Scene scene = new Scene(root);

        primaryStage.setTitle("Quote");
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            if (!this.controller.saved) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Close Window");
                alert.setHeaderText("Are you sure you want to exit this window?");
                alert.setContentText("Are you sure you want to exit this window? Any unsaved changes cannot be recovered.");
                alert.getButtonTypes().clear();
                alert.getButtonTypes().add(ButtonType.YES);
                alert.getButtonTypes().add(ButtonType.NO);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.NO) {
                    event.consume();
                }
            }
        });
    }

    @Override
    public void stop() {
        if (!this.controller.saved) {

        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
