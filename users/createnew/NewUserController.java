/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package users.createnew;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class NewUserController implements Initializable {

    /**
     *
     * @param event
     */
    @FXML 
    public void signUp(ActionEvent event){
        
    }
    
    @FXML
    public void validateCompanyID(ActionEvent event){
        System.out.println("validating company");
    }
    
    @FXML
    public void validateUsername(ActionEvent event){
        System.out.println("validating username");
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
