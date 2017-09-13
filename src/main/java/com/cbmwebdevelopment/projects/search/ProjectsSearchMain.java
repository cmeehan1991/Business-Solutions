/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.projects.search;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.cbmwebdevelopment.projects.ProjectsFXMLController;

/**
 *
 * @author cmeehan
 */
public class ProjectsSearchMain extends Application {
    public ProjectsFXMLController projectsController;
    public TextField projectIDTextField;
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/ProjectsSearchFXML.fxml"));
        Parent root = (Parent) loader.load();
        ProjectsSearchFXMLController controller = (ProjectsSearchFXMLController) loader.getController();
        controller.projectsController = this.projectsController;
        controller.projectIDTextField = this.projectIDTextField;
        controller.currentStage = primaryStage;
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Project Search");
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
