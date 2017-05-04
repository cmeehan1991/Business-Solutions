/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author cmeehan
 */
public class NavigationMain extends Application {

    public String USER_ID;

    public NavigationMain(String userID) {
        this.USER_ID = userID;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("NavigationFXML.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("styles/GlobalStyle.css");
        scene.getStylesheets().add("styles/NavigationStyle.css");

        stage.setResizable(false);
        stage.setY(10.00);
        stage.setX(screenWidth() - stage.getWidth() / 2);
        stage.setTitle("Business Solutions");
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Get the width of the screen to set the location of the navigation box.
     * 
     * @return double
     */
    private double screenWidth() {
        double width;
        Rectangle2D rectangle = Screen.getPrimary().getBounds();
        width = rectangle.getWidth();
        return width;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
