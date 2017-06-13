/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projects;

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
public class ProjectsMain extends Application {

    public boolean typeNew;
    public String userID;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/ProjectsFXML.fxml"));
        Parent root = (Parent) loader.load();
        ProjectsFXMLController controller = (ProjectsFXMLController) loader.getController();
        controller.userID = this.userID;
        if (!typeNew) {
            controller.isNew = false;
            controller.projectIDTextField.setEditable(true);
        } else {
            controller.isNew = true;
            controller.projectIDTextField.setEditable(false);
            primaryStage.setTitle("New Project");
        }

        Scene scene = new Scene(root);

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
