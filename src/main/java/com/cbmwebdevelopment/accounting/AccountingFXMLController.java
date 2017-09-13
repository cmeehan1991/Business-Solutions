/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.accounting;

import com.cbmwebdevelopment.accounting.AccountingTableController.AccountingItems;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class AccountingFXMLController implements Initializable {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    private RadioButton allRadioButton, paidRadioButton, outstandingRadioButton;

    @FXML
    private DatePicker fromDatePicker, toDatePicker;

    @FXML
    private ComboBox transactionTypeComboBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableView accountingTable;

    @FXML
    private ToggleGroup typeToggleGroup;

    @FXML
    private void receivePayment(ActionEvent event) throws IOException, SQLException {
        CreditMain creditMain = new CreditMain();
        creditMain.isNew = true;
        creditMain.start(new Stage());

    }

    @FXML
    private void payroll(ActionEvent event) {

    }

    @FXML
    private void exportData(ActionEvent event) {

    }

    @FXML
    private void searchTransactions(ActionEvent event) {

    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Set the values of the datepickers 
        this.fromDatePicker.setValue(LocalDate.now().minusDays(30));
        this.toDatePicker.setValue(LocalDate.now());

        // Set the values for the type combo box
        this.transactionTypeComboBox.getItems().setAll("All", "Credit", "Debit");

        // Set the accounting table items
        AccountingTableController accountingTableController = new AccountingTableController();
        accountingTableController.accountingTable(accountingTable);
        try {
            accountingTableController.fillAccountingTable(null, this.fromDatePicker.getValue().format(formatter), this.toDatePicker.getValue().format(formatter), null, null, accountingTable);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Listen for a change to the dates to change the table items
        this.fromDatePicker.setOnAction((ActionEvent event) -> {
            try {
                accountingTableController.fillAccountingTable(null, this.fromDatePicker.getValue().format(formatter), this.toDatePicker.getValue().format(formatter), null, null, accountingTable);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        });

        this.toDatePicker.setOnAction((ActionEvent event) -> {
            try {
                accountingTableController.fillAccountingTable(null, this.fromDatePicker.getValue().format(formatter), this.toDatePicker.getValue().format(formatter), null, null, accountingTable);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        });

        // Listen for a click event on the table
        this.accountingTable.setRowFactory(tv -> {
            TableRow<AccountingItems> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty() && !event.isSecondaryButtonDown() && !event.isMiddleButtonDown())) {
                    AccountingItems accountingItems = row.getItem();
                    switch (accountingItems.getType()) {
                        case "Credit":
                            try {
                                Credit credit = new Credit();
                                credit.getExistingCredit(accountingItems.getTransactionId());
                            } catch (SQLException ex) {
                                System.out.println(ex.getMessage());
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
            return row;
        });

        // TODO
    }

}
