/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.customers;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

/**
 *
 * @author cmeehan
 */
public class CustomerInvoiceTable {

    private final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    private double sum = 0.0;
    private List<Double> totalCost = new ArrayList<>();
    private final ObservableList<String> categories = FXCollections.observableArrayList("New Development", "Design", "Updates", "Marketing", "Logo Development", "Consultation", "Set Up", "Other");
    private final ObservableList<String> unit = FXCollections.observableArrayList("Hour", "Project");

    public CustomerInvoiceTable(TableView tableView) {
        invoiceTable(tableView);
    }

    public ObservableList<InvoiceItems> data = FXCollections.observableArrayList(new InvoiceItems(null, null, null, null));

    private void invoiceTable(TableView tableView) {
        Callback<TableColumn<InvoiceItems, String>, TableCell<InvoiceItems, String>> cellFactory = (TableColumn<InvoiceItems, String> param) -> new EditingCell();
        Callback<TableColumn<InvoiceItems, Boolean>, TableCell<InvoiceItems, Boolean>> booleanCellFactory = (TableColumn<InvoiceItems, Boolean> param) -> new EditingBooleanCell();

        // Setting the column properties for the invoice table view
        TableColumn<InvoiceItems, String> projectColumn = new TableColumn("Project");
        TableColumn<InvoiceItems, String> categoryColumn = new TableColumn("Type");
        TableColumn<InvoiceItems, String> statusColumn = new TableColumn("Status");
        TableColumn<InvoiceItems, String> valueColumn = new TableColumn("Value");
        
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        projectColumn.setCellFactory(cellFactory);
        projectColumn.setPrefWidth(64);
        projectColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setProject(t.getNewValue());
        });

        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), categories));
        categoryColumn.setPrefWidth(125);
        categoryColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setCategory(t.getNewValue());
        });
        
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("costUnit"));
        statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), unit));
        statusColumn.setPrefWidth(85);
        statusColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setStatus(t.getNewValue());
        });

        valueColumn.setCellValueFactory(new PropertyValueFactory<>("costUnitTotal"));
        valueColumn.setCellFactory(cellFactory);
        valueColumn.setPrefWidth(64);
        valueColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setValue(t.getNewValue());
        });

        tableView.setItems(data);
        tableView.getColumns().addAll(projectColumn, categoryColumn, statusColumn, valueColumn);
    }

    public static class InvoiceItems {

        public SimpleStringProperty project;
        public SimpleStringProperty category;
        public SimpleStringProperty status;
        public SimpleStringProperty value;

        public InvoiceItems() {
        }

        public InvoiceItems(String project, String category, String status, String value) {
            this.project = new SimpleStringProperty(project);
            this.category = new SimpleStringProperty(category);
            this.status = new SimpleStringProperty(status);
            this.value = new SimpleStringProperty(value);
        }

        public String getProject() {
            return project.get();
        }

        public void setProject(String project) {
            this.project.set(project);
        }

        public String getCategory() {
            return category.get();
        }

        public void setCategory(String category) {
            this.category.set(category);
        }

        public String getStatus() {
            return status.get();
        }

        public void setStatus(String status) {
            this.status.set(status);
        }

        public String getValue() {
            return value.get();
        }

        public void setValue(String value) {
            this.value.set(value);
        }
    }

    public class EditingBooleanCell extends TableCell<InvoiceItems, Boolean> {

        private CheckBox checkBox;

        public EditingBooleanCell() {
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (!isEmpty()) {
                createCheckBox();
            }
            setText(null);
            setGraphic(checkBox);
            checkBox.requestFocus();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setGraphic(null);
        }

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (isEditing()) {
                if (checkBox != null) {
                    checkBox.setSelected(isSelected());
                }
            }
        }

        private void createCheckBox() {
            checkBox = new CheckBox();
            checkBox.focusedProperty().addListener((ObservableValue<? extends Boolean> arg, Boolean arg1, Boolean arg2) -> {
                if (!arg2) {
                    commitEdit(checkBox.isSelected());
                }
            });
        }

        // Thanks to https://gist.github.com/abhinayagarwal/9383881
        private TableColumn<InvoiceItems, ?> getNextColumn(boolean forward) {
            List<TableColumn<InvoiceItems, ?>> columns = new ArrayList<>();
            getTableView().getColumns().forEach((column) -> {
                columns.addAll(getLeaves(column));
            });
            // There is no other column that supports editing.
            if (columns.size() < 2) {
                return null;
            }
            int currentIndex = columns.indexOf(getTableColumn());
            int nextIndex = currentIndex;
            if (forward) {
                nextIndex++;
                if (nextIndex > columns.size() - 1) {
                    nextIndex = 0;
                }
            } else {
                nextIndex--;
                if (nextIndex < 0) {
                    nextIndex = columns.size() - 1;
                }
            }
            return columns.get(nextIndex);
        }

        private List<TableColumn<InvoiceItems, ?>> getLeaves(
                TableColumn<InvoiceItems, ?> root) {
            List<TableColumn<InvoiceItems, ?>> columns = new ArrayList<>();
            if (root.getColumns().isEmpty()) {
                // We only want the leaves that are editable.
                if (root.isEditable()) {
                    columns.add(root);
                }
                return columns;
            } else {
                root.getColumns().forEach((column) -> {
                    columns.addAll(getLeaves(column));
                });
                return columns;
            }
        }
    }

    public class EditingCell extends TableCell<InvoiceItems, String> {

        private TextField textField;

        public EditingCell() {
        }

        @Override
        public void startEdit() {
            super.startEdit();
            if (!isEmpty()) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.requestFocus();
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.focusedProperty().addListener((ObservableValue<? extends Boolean> arg, Boolean arg1, Boolean arg2) -> {
                if (!arg2) {
                    commitEdit(textField.getText());
                }
            });
            textField.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                } else if (event.getCode() == KeyCode.TAB) {
                    commitEdit(textField.getText());
                    TableColumn nextColumn = getNextColumn(!event.isShiftDown());
                    if (nextColumn != null) {
                        getTableView().edit(getTableRow().getIndex(), nextColumn);

                    }
                }
            });
        }

        // Thanks to https://gist.github.com/abhinayagarwal/9383881
        private TableColumn<InvoiceItems, ?> getNextColumn(boolean forward) {
            List<TableColumn<InvoiceItems, ?>> columns = new ArrayList<>();
            getTableView().getColumns().forEach((column) -> {
                columns.addAll(getLeaves(column));
            });
            // There is no other column that supports editing.
            if (columns.size() < 2) {
                return null;
            }
            int currentIndex = columns.indexOf(getTableColumn());
            int nextIndex = currentIndex;
            if (forward) {
                nextIndex++;
                if (nextIndex > columns.size() - 1) {
                    nextIndex = 0;
                }
            } else {
                nextIndex--;
                if (nextIndex < 0) {
                    nextIndex = columns.size() - 1;
                }
            }
            return columns.get(nextIndex);
        }

        private List<TableColumn<InvoiceItems, ?>> getLeaves(
                TableColumn<InvoiceItems, ?> root) {
            List<TableColumn<InvoiceItems, ?>> columns = new ArrayList<>();
            if (root.getColumns().isEmpty()) {
                // We only want the leaves that are editable.
                if (root.isEditable()) {
                    columns.add(root);
                }
                return columns;
            } else {
                root.getColumns().forEach((column) -> {
                    columns.addAll(getLeaves(column));
                });
                return columns;
            }
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

}
