/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.projects;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 *
 * @author cmeehan
 */
public class ProjectsBillingTableViewController {

    public ProjectsBillingTableViewController(TableView tableView) {
        billingTable(tableView);
    }

    public ObservableList<BillingItems> data = FXCollections.observableArrayList(new BillingItems(null, null, null, null, null));

    private void billingTable(TableView tableView) {
        Callback<TableColumn<BillingItems, String>, TableCell<BillingItems, String>> cellFactory = (TableColumn<BillingItems, String> param) -> new EditingCell();
        Callback<TableColumn<BillingItems, Boolean>, TableCell<BillingItems, Boolean>> booleanCellFactory = (TableColumn<BillingItems, Boolean> param) -> new EditingBooleanCell();

        // Setting the column properties for the invoice table view
        TableColumn<BillingItems, String> invoiceIdColumn = new TableColumn("Invoice ID");
        TableColumn<BillingItems, String> invoiceTitleColumn = new TableColumn("Invoice Title");
        TableColumn<BillingItems, String> invoiceTypeColumn = new TableColumn("Type");
        TableColumn<BillingItems, String> totalCostColumn = new TableColumn("Total Cost");
        TableColumn<BillingItems, String> amountDueColumn = new TableColumn("Amount Due");

        invoiceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        invoiceIdColumn.setCellFactory(cellFactory);
        invoiceIdColumn.setPrefWidth(100);
        invoiceIdColumn.setOnEditCommit((TableColumn.CellEditEvent<BillingItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((BillingItems) t.getTableView().getItems().get(tableRow)).setID(t.getNewValue());
        });

        invoiceTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        invoiceTitleColumn.setCellFactory(cellFactory);
        invoiceTitleColumn.setPrefWidth(300);
        invoiceTitleColumn.setOnEditCommit((TableColumn.CellEditEvent<BillingItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((BillingItems) t.getTableView().getItems().get(tableRow)).setTitle(t.getNewValue());
        });

        invoiceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        invoiceTypeColumn.setCellFactory(cellFactory);
        invoiceTypeColumn.setPrefWidth(300);
        invoiceTypeColumn.setOnEditCommit((TableColumn.CellEditEvent<BillingItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((BillingItems) t.getTableView().getItems().get(tableRow)).setType(t.getNewValue());
        });

        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        totalCostColumn.setCellFactory(cellFactory);
        totalCostColumn.setPrefWidth(100);
        totalCostColumn.setOnEditCommit((TableColumn.CellEditEvent<BillingItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((BillingItems) t.getTableView().getItems().get(tableRow)).setCost(t.getNewValue());
        });

        amountDueColumn.setCellValueFactory(new PropertyValueFactory<>("due"));
        amountDueColumn.setCellFactory(cellFactory);
        amountDueColumn.setPrefWidth(100);
        amountDueColumn.setOnEditCommit((TableColumn.CellEditEvent<BillingItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((BillingItems) t.getTableView().getItems().get(tableRow)).setDue(t.getNewValue());
        });

        tableView.setItems(data);
        tableView.getColumns().addAll(invoiceIdColumn, invoiceTitleColumn, invoiceTypeColumn, totalCostColumn, amountDueColumn);
    }

    public static class BillingItems {

        public SimpleStringProperty invoiceID;
        public SimpleStringProperty invoiceTitle;
        public SimpleStringProperty invoiceType;
        public SimpleStringProperty totalCost;
        public SimpleStringProperty amountDue;

        public BillingItems() {
        }

        public BillingItems(String invoiceID, String invoiceTitle, String invoiceType, String totalCost, String amountDue) {
            this.invoiceID = new SimpleStringProperty(invoiceID);
            this.invoiceTitle = new SimpleStringProperty(invoiceTitle);
            this.invoiceType = new SimpleStringProperty(invoiceType);
            this.totalCost = new SimpleStringProperty(totalCost);
            this.amountDue = new SimpleStringProperty(amountDue);
        }

        public String getId() {
            return invoiceID.get();
        }

        public void setID(String id) {
            this.invoiceID.set(id);
        }

        public String getTitle() {
            return invoiceTitle.get();
        }

        public void setTitle(String title) {
            this.invoiceTitle.set(title);
        }

        public String getType() {
            return invoiceType.get();
        }

        public void setType(String type) {
            this.invoiceType.set(type);
        }

        public String getCost() {
            return totalCost.get();
        }

        public void setCost(String cost) {
            this.totalCost.set(cost);
        }

        public String getDue() {
            return amountDue.get();
        }

        public void setDue(String due) {
            this.amountDue.set(due);
        }
    }

    public class EditingBooleanCell extends TableCell<BillingItems, Boolean> {

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
        private TableColumn<BillingItems, ?> getNextColumn(boolean forward) {
            List<TableColumn<BillingItems, ?>> columns = new ArrayList<>();
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

        private List<TableColumn<BillingItems, ?>> getLeaves(
                TableColumn<BillingItems, ?> root) {
            List<TableColumn<BillingItems, ?>> columns = new ArrayList<>();
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

    public class EditingCell extends TableCell<BillingItems, String> {

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
        private TableColumn<BillingItems, ?> getNextColumn(boolean forward) {
            List<TableColumn<BillingItems, ?>> columns = new ArrayList<>();
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

        private List<TableColumn<BillingItems, ?>> getLeaves(
                TableColumn<BillingItems, ?> root) {
            List<TableColumn<BillingItems, ?>> columns = new ArrayList<>();
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
