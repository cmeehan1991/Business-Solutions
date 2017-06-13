/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accounting;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class CreditMain extends Application {
    public boolean isNew;
    public String transactionId;
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/CreditFXML.fxml"));
        Parent root = (Parent) loader.load();
        CreditFXMLController controller = (CreditFXMLController) loader.getController();
        
        if(isNew){
            controller.isNew = true;
            controller.creditIdLabel.setVisible(false);
            controller.creditIdLabelLabel.setVisible(false);
            controller.amountDueLabel.setText("$0.00");
            controller.amountAppliedLabel.setText("$0.00");
            controller.currentBalanceLabel.setText("$0.00");
            controller.discountsLabel.setText("$0.00");
        }
        
        
        Scene scene = new Scene(root);
        
        String title = this.isNew ? "New Credit": transactionId;
        primaryStage.setTitle(title);
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
