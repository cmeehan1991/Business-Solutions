/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.projects.search;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author cmeehan
 */
public final class ProjectsSearchResultTable {
    public ProjectsSearchResultTable(TableView tableView){
        setTableView(tableView);
    }
    
    public void setTableView(TableView tableView){
        ObservableList<String> data = FXCollections.observableArrayList();
        TableColumn<ProjectsTable, String> idColumn = new TableColumn("Project ID");
        TableColumn<ProjectsTable, String> companyNameColumn = new TableColumn("Client");
        TableColumn<ProjectsTable, String> titleColumn = new TableColumn("Project");
        TableColumn<ProjectsTable, String> typeColumn = new TableColumn("Project Type");
        TableColumn<ProjectsTable, String> createdColumn = new TableColumn("Date Created");
        TableColumn<ProjectsTable, String> updatedColumn = new TableColumn("Last Updated");
        
        // Set the cell value factory
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("created"));
        updatedColumn.setCellValueFactory(new PropertyValueFactory<>("updated"));
        
        // Set the column widths
        idColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        companyNameColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        titleColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        typeColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        createdColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        updatedColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        
        
        tableView.getItems().setAll(data);
        tableView.getColumns().addAll(idColumn, companyNameColumn, titleColumn, typeColumn, createdColumn, updatedColumn);
    }
   
    public static class ProjectsTable{
        SimpleStringProperty id, companyName, title, type, created, updated;
        
        public ProjectsTable(){}
        
        public ProjectsTable(String id, String companyName, String title, String type, String created, String updated ){
            this.id = new SimpleStringProperty(id);
            this.companyName = new SimpleStringProperty(companyName);
            this.title = new SimpleStringProperty(title);
            this.type = new SimpleStringProperty(type);
            this.created = new SimpleStringProperty(created);
            this.updated = new SimpleStringProperty(updated);
        }
        
        public String getId(){
            return id.get();
        }
        
        public void setId(String id){
            this.id = new SimpleStringProperty(id);
        }
        
        public String getCompanyName(){
            return companyName.get();
        }
        
        public void setCompanyName(String companyName){
            this.companyName = new SimpleStringProperty(companyName);
        }
        
        public String getTitle(){
            return title.get();
        }
        
        public void setTitle(String title){
            this.title = new SimpleStringProperty(title);
        }
        
        public String getType(){
            return type.get();
        }
        
        public void setType(String type){
            this.type = new SimpleStringProperty(type);
        }
        
        public String getCreated(){
            return created.get();
        }
        
        public void setCreated(String created){
            this.created = new SimpleStringProperty(created);
        }
        
        public String getUpdated(){
            return updated.get();
        }
        
        public void setUpdated(String updated){
            this.updated = new SimpleStringProperty(updated);
        }
    }
}
