/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.projects.search;

import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.cbmwebdevelopment.projects.ProjectsFXMLController;
import com.cbmwebdevelopment.projects.search.ProjectsSearchResultTable.ProjectsTable;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class ProjectsSearchFXMLController implements Initializable {

    private final ObservableList<String> whereValues = FXCollections.observableArrayList("Client", "Project ID", "Project Start Date", "Project Updated");
    private final ObservableList<String> parameterValues = FXCollections.observableArrayList("EQUALS", "GREATER THAN", "LESS THAN", "LIKE");
    private final ObservableList<String> includeValues = FXCollections.observableArrayList("AND", "OR");
    protected ProjectsFXMLController projectsController;
    protected TextField projectIDTextField;
    public Stage currentStage;
    @FXML
    ComboBox whereComboBoxOne, whereComboBoxTwo, whereComboBoxThree, includeOneComboBox, includeTwoComboBox, parameterComboBoxOne, parameterComboBoxTwo, parameterComboBoxThree;

    @FXML
    TextField termOneTextField, termTwoTextField, termThreeTextField;

    @FXML
    TableView resultsTableView;

    /**
     * This will close the current window without running a search.
     *
     * @param event
     */
    @FXML
    protected void cancelSearch(ActionEvent event) {
        this.currentStage.close();
    }

    /**
     * This will select the currently selected table row and pass the ID to 
     * the ProjectsFXMLController method setValues(String id) as well as the
     * project id text field. 
     * 
     * @param event 
     */
    @FXML
    protected void selectProject(ActionEvent event) {
        ObservableList<ProjectsTable> list = this.resultsTableView.getSelectionModel().getSelectedItems();
        list.forEach((item) -> {
            this.projectIDTextField.setText(item.getId());
            this.projectsController.setValues(item.getId());
        });
        this.currentStage.close();
    }

    /**
     * Perform the search for the project.
     *
     * @param event
     * @throws java.sql.SQLException
     */
    @FXML
    protected void search(ActionEvent event) throws SQLException {
        String parameterOne = parameterComboBoxOne.getSelectionModel().getSelectedItem().toString();
        String whereOne = whereComboBoxOne.getSelectionModel().getSelectedItem().toString();
        String termOne = termOneTextField.getText();

        String search = setSearchParameter(whereOne, parameterOne, termOne);

        if (includeOneComboBox.getSelectionModel().getSelectedIndex() > -1) {
            String includeOne = includeOneComboBox.getSelectionModel().getSelectedItem().toString();
            String parameterTwo = parameterComboBoxTwo.getSelectionModel().getSelectedItem().toString();
            String whereTwo = whereComboBoxTwo.getSelectionModel().getSelectedItem().toString();
            String termTwo = termTwoTextField.getText();
            search += includeOne + " " + setSearchParameter(whereTwo, parameterTwo, termTwo);
        }

        if (includeTwoComboBox.getSelectionModel().getSelectedIndex() > -1) {
            String includeTwo = includeTwoComboBox.getSelectionModel().getSelectedItem().toString();
            String parameterThree = parameterComboBoxThree.getSelectionModel().getSelectedItem().toString();
            String whereThree = whereComboBoxThree.getSelectionModel().getSelectedItem().toString();
            String termThree = termThreeTextField.getText();
            search += includeTwo + " " + setSearchParameter(whereThree, parameterThree, termThree);
        }

        ProjectsSearch projectsSearch = new ProjectsSearch();

        projectsSearch.projects(search).forEach((item) -> {
            System.out.println(item.getCompanyName());
        });

        this.resultsTableView.getItems().setAll(projectsSearch.projects(search));

    }

    private String setSearchParameter(String where, String parameter, String term) {
        String search = null;
        String whereParameter = null;

        //"Client", "Project ID", "Project Start Date", "Project Updated"
        switch (where) {
            case "Client":
                whereParameter = "ALL_CUSTOMERS.COMPANY_NAME";
                break;
            case "Project ID":
                whereParameter = "PROJECT.ID";
                break;
            case "Project Start Date":
                whereParameter = "PROJECT.DATE_CREATED";
                break;
            case "Project Updated":
                whereParameter = "PROJECT.LAST_UPDATED";
                break;
            default:
                break;
        }

        switch (parameter) {
            case "LIKE":
                List<String> termList = Arrays.asList(term.split(""));
                String likeTerm = "%" + termList.stream().map(i -> i).collect(Collectors.joining("%")) + "%";
                search = whereParameter + " LIKE " + "\"" + likeTerm + "\"";
                break;
            case "EQUALS":
                search = whereParameter + " = " + "\"" + term + "\"";
                break;
            case "GREATER THAN":
                search = whereParameter + " >= " + "\"" + term + "\"";
                break;
            case "LESS THAN":
                search = whereParameter + " <= " + "\"" + term + "\"";
                break;
            default:
                break;
        }
        return search;
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.parameterComboBoxOne.getItems().setAll(parameterValues);
        this.parameterComboBoxTwo.getItems().setAll(parameterValues);
        this.parameterComboBoxThree.getItems().setAll(parameterValues);
        this.includeOneComboBox.getItems().setAll(includeValues);
        this.includeTwoComboBox.getItems().setAll(includeValues);
        this.whereComboBoxOne.getItems().setAll(whereValues);
        this.whereComboBoxTwo.getItems().setAll(whereValues);
        this.whereComboBoxThree.getItems().setAll(whereValues);

        ProjectsSearchResultTable table = new ProjectsSearchResultTable(this.resultsTableView);

        this.resultsTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() >= 2 && (!event.isSecondaryButtonDown() && !event.isMiddleButtonDown())) {
                ObservableList<ProjectsTable> list = this.resultsTableView.getSelectionModel().getSelectedItems();
                list.forEach((item) -> {
                    this.projectIDTextField.setText(item.getId());
                    this.projectsController.setValues(item.getId());
                });
                this.currentStage.close();
            }
        });

    }

}
