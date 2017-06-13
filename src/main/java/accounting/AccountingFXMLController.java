/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accounting;

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
    private void receivePayment(ActionEvent event) throws IOException {
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
        this.fromDatePicker.setValue(LocalDate.now().minusDays(30));
        this.toDatePicker.setValue(LocalDate.now());
        AccountingTableController accountingTableController = new AccountingTableController();
        accountingTableController.accountingTable(accountingTable);
        try {
            accountingTableController.fillAccountingTable(null, this.fromDatePicker.getValue().format(formatter), this.toDatePicker.getValue().format(formatter), null, null, accountingTable);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // TODO
    }

}
