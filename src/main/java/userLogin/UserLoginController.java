/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userLogin;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import navigation.NavigationMain;
import users.Users;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class UserLoginController implements Initializable {

    @FXML
    TextField usernameTextField, passwordTextField;
    
    
    @FXML
    public void userSignIn(ActionEvent event) throws Exception {
        Users users = new Users();
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String userID = users.SignIn(username, password);
        
        // If the user successfully signs in then we will close this stage and 
        // open the Navigation stage.
        if(userID != null && !userID.equals("Error")){
            Stage stage = (Stage) usernameTextField.getScene().getWindow();
            stage.close();
            NavigationMain navigationMain = new NavigationMain(userID); // Pass the user ID to the main navigation class for future use
            navigationMain.start(new Stage());
        }
    }
    
        
    public void closeStage(){
        Stage stage = (Stage) usernameTextField.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void createNewUser(ActionEvent event) throws IOException{
        
    }
    
    @FXML
    public void recoverUser(ActionEvent event){
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
