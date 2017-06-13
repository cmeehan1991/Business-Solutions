/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class SchedulerFXMLController implements Initializable {

    @FXML
    ToggleGroup scheduleRangeGroup;
    
    @FXML
    DatePicker calendarDatePicker;
    
    @FXML
    RadioButton monthRadioButton, weekRadioButton, dayRadioButton;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}