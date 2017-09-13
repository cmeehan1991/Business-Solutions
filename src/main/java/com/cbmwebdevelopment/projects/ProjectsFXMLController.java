/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.projects;

import com.cbmwebdevelopment.connections.DBConnection;
import com.cbmwebdevelopment.customers.Client;
import com.cbmwebdevelopment.invoices.InvoiceMain;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import com.cbmwebdevelopment.notifications.Toast;
import org.controlsfx.control.CheckComboBox;
import com.cbmwebdevelopment.projects.ProjectsBillingTableViewController.BillingItems;
import com.cbmwebdevelopment.projects.search.ProjectsSearchMain;
import com.cbmwebdevelopment.users.Users;

/**
 * FXML Controller class
 *
 * @author cmeehan
 */
public class ProjectsFXMLController implements Initializable {

    Users users = new Users();
    Client clients = new Client();
    private final Projects projects = new Projects();
    private final InvoiceMain invoiceMain = new InvoiceMain();

    public String userID, clientID;
    public boolean isNew;
    @FXML
    CheckComboBox projectTypeCheckComboBox, managersCheckComboBox;

    @FXML
    ComboBox clientComboBox;

    @FXML
    TextField projectIDTextField, projectTitleTextField;

    @FXML
    TextArea projectNotesTextArea;

    @FXML
    RadioButton planningRadioButton, inProgressRadioButton, inReviewRadioButton, completeRadioButton, cancelledRadioButton;

    @FXML
    TableView billingTableView;

    @FXML
    ListView teamMembersListView;

    @FXML
    Button saveProjectButton;
    
    @FXML 
    MenuBar menuBar;

    @FXML 
    protected void searchForProject(ActionEvent event) throws IOException{
        ProjectsSearchMain projectsSearchMain = new ProjectsSearchMain();
        projectsSearchMain.projectIDTextField = this.projectIDTextField;
        projectsSearchMain.projectsController = this;
        Stage searchStage = new Stage();
        searchStage.setOnCloseRequest((WindowEvent evt)->{
           setValues(this.projectIDTextField.getText());
        });
        projectsSearchMain.start(searchStage);
    }
    
    @FXML
    protected void saveProject(ActionEvent event) {
        projects.userID = Integer.parseInt(userID);
        if (isNew) {
            projects.clientName = clientComboBox.getSelectionModel().getSelectedItem().toString();
            projects.projectTitle = projectTitleTextField.getText();
            projects.projectDescription = projectNotesTextArea.getText();

            // Get the project type(s).
            List<String> projectType = new ArrayList<>();
            projectTypeCheckComboBox.getCheckModel().getCheckedItems().forEach((i) -> {
                projectType.add(String.valueOf(i));
            });
            projects.projectType = projectType.stream().map(i -> i).collect(Collectors.joining(","));

            // Get the ID(s) of the manager(s).
            List<String> managerID = new ArrayList<>();
            managersCheckComboBox.getCheckModel().getCheckedItems().forEach((i) -> {
                managerID.add(users.getUserIDByName(String.valueOf(i)));
            });
            projects.managers = managerID.stream().map(i -> i).collect(Collectors.joining(","));

            // Get the team member(s) id(s).
            List<String> memberID = new ArrayList<>();
            teamMembersListView.getSelectionModel().getSelectedItems().forEach((i) -> {
                memberID.add(users.getUserIDByName(String.valueOf(i)));
            });
            projects.projectTeam = memberID.stream().map(i -> i).collect(Collectors.joining(","));

            projects.planning = planningRadioButton.isSelected();
            projects.inProgress = inProgressRadioButton.isSelected();
            projects.inReview = inReviewRadioButton.isSelected();
            projects.complete = completeRadioButton.isSelected();
            projects.cancelled = cancelledRadioButton.isSelected();

            String projectID = projects.addNewProject();
            if (projectID != null) {
                this.projectIDTextField.setText(projects.addNewProject());
                Stage stage = (Stage) this.projectIDTextField.getScene().getWindow();
                Toast.makeText(stage, "Project Added", 0, 2, 2);
                this.isNew = false;
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Project Error");
                alert.setContentText("Error: The project was not added. Please try again or contact your systems administrator for assistance.");
                alert.showAndWait();
            }
        } else if (!isNew && !this.projectIDTextField.getText().trim().isEmpty()) {
            projects.projectID = this.projectIDTextField.getText();
            projects.clientName = clientComboBox.getSelectionModel().getSelectedItem().toString();
            projects.projectTitle = projectTitleTextField.getText();
            projects.projectDescription = projectNotesTextArea.getText();

            // Get the project type(s).
            List<String> projectType = new ArrayList<>();
            projectTypeCheckComboBox.getCheckModel().getCheckedItems().forEach((i) -> {
                projectType.add(String.valueOf(i));
            });
            projects.projectType = projectType.stream().map(i -> i).collect(Collectors.joining(","));

            // Get the ID(s) of the manager(s).
            List<String> managerID = new ArrayList<>();
            managersCheckComboBox.getCheckModel().getCheckedItems().forEach((i) -> {
                managerID.add(users.getUserIDByName(String.valueOf(i)));
            });
            projects.managers = managerID.stream().map(i -> i).collect(Collectors.joining(","));

            // Get the team member(s) id(s).
            List<String> memberID = new ArrayList<>();
            teamMembersListView.getSelectionModel().getSelectedItems().forEach((i) -> {
                memberID.add(users.getUserIDByName(String.valueOf(i)));
            });
            projects.projectTeam = memberID.stream().map(i -> i).collect(Collectors.joining(","));

            projects.planning = planningRadioButton.isSelected();
            projects.inProgress = inProgressRadioButton.isSelected();
            projects.inReview = inReviewRadioButton.isSelected();
            projects.complete = completeRadioButton.isSelected();
            projects.cancelled = cancelledRadioButton.isSelected();

            if (projects.updateProject(this.projectIDTextField.getText())) {
                Stage stage = (Stage) this.projectIDTextField.getScene().getWindow();
                Toast.makeText(stage, "Project Saved", 0, 2, 2);
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Project Error");
                alert.setContentText("Error: The project was not updated. Please try again or contact your systems administrator for assistance.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    protected void getProject(ActionEvent event) {
        setValues(this.projectIDTextField.getText());
    }

    public void setValues(String ID) {
        Connection conn = new DBConnection().connect();
        String sql = "SELECT ID, CLIENT_ID, TITLE, PROJECT_TYPE, DESCRIPTION, PROJECT_MANAGER, PROJECT_TEAM, PLANNING, IN_PROGRESS, IN_REVIEW, COMPLETED, CANCELLED FROM PROJECT WHERE ID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.clientComboBox.getSelectionModel().select(clients.getClient(rs.getString("CLIENT_ID")));
                this.projectTitleTextField.setText(rs.getString("TITLE"));
                String[] projectType = (rs.getString("PROJECT_TYPE") != null) ? rs.getString("PROJECT_TYPE").split(", ") : null;
                if (projectType != null) {
                    for (String type : projectType) {
                        this.projectTypeCheckComboBox.getCheckModel().check(type);
                    }
                }
                String[] managers = (rs.getString("PROJECT_MANAGER") != null) ? rs.getString("PROJECT_MANAGER").split(", ") : null;
                if (managers != null) {
                    for (String id : managers) {
                        this.managersCheckComboBox.getCheckModel().check(users.getNameByID(id));
                    }
                }
                this.planningRadioButton.setSelected(rs.getBoolean("PLANNING"));
                this.inProgressRadioButton.setSelected(rs.getBoolean("IN_PROGRESS"));
                this.inReviewRadioButton.setSelected(rs.getBoolean("IN_REVIEW"));
                this.completeRadioButton.setSelected(rs.getBoolean("COMPLETED"));
                this.cancelledRadioButton.setSelected(rs.getBoolean("CANCELLED"));

                String[] members = (rs.getString("PROJECT_TEAM") != null) ? rs.getString("PROJECT_TEAM").split(", ") : null;
                if (members != null) {
                    for (String member : members) {
                        this.teamMembersListView.getSelectionModel().select(users.getNameByID(member));
                    }
                }
                this.projectNotesTextArea.setText(rs.getString("DESCRIPTION"));
                this.billingTableView.getItems().setAll(projects.getInvoices(ID));
            }
            ps.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error selecting the project");
            alert.setContentText("There was an error selecting project information from the database. Please try again or contact your systems administrator for assitnace.");
            alert.showAndWait();
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.menuBar.setUseSystemMenuBar(true);
        
        // Set the combo and checkbombo box items
        this.projectTypeCheckComboBox.getItems().setAll("Mobile Application - iOS", "Mobile Application - Android", "Other", "Social Media Marketing", "Software - New", "Software - Existing", "Website - New", "Website - Existing");

        this.managersCheckComboBox.getItems().setAll(users.getUsers());
        this.teamMembersListView.getItems().setAll(users.getUsers());
        try {
            this.clientComboBox.getItems().setAll(clients.clientList());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        //Set table view
        ProjectsBillingTableViewController tableView = new ProjectsBillingTableViewController(this.billingTableView);

        // Disable the save project button unless there is an active ID and it 
        // is not a new quote. 
        if (!this.isNew) {
            this.projectIDTextField.setOnKeyPressed((KeyEvent event) -> {
                if (!this.projectIDTextField.getText().trim().isEmpty() && (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER)) {
                    if (this.saveProjectButton.isDisabled()) {
                        this.saveProjectButton.setDisable(false);
                    }
                    setValues(this.projectIDTextField.getText());
                } else if (this.projectIDTextField.getText().trim().isEmpty()) {
                    this.saveProjectButton.setDisable(true);
                }
            });
        }

        // Set the click listener for the billing table. 
        this.billingTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2 && (!event.isMiddleButtonDown() && !event.isSecondaryButtonDown())) {
                ObservableList<BillingItems> selectedItems = this.billingTableView.getSelectionModel().getSelectedItems();
                selectedItems.forEach((item) -> {
                    try {
                        invoiceMain.isNew = false;
                        invoiceMain.invoiceID = String.valueOf(item.getId());
                        invoiceMain.clientID = clients.clientID(this.clientComboBox.getSelectionModel().getSelectedItem().toString());
                        invoiceMain.start(new Stage());
                    } catch (IOException | SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                });
            }
        });

    }
}
