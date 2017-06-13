/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invoices;

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
public class InvoiceItemizationTable {

    private final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    private double sum = 0.0;
    private List<Double> totalCost = new ArrayList<>();
    private final ObservableList<String> categories = FXCollections.observableArrayList("New Development", "Design", "Updates", "Marketing", "Logo Development", "Consultation", "Set Up", "Other");
    private final ObservableList<String> unit = FXCollections.observableArrayList("Hour", "Project");

    public InvoiceItemizationTable(TableView tableView) {
        invoiceItemizationTable(tableView);
    }

    public ObservableList<InvoiceItems> data = FXCollections.observableArrayList(new InvoiceItems(false, null, null, null, null, null));

    private void invoiceItemizationTable(TableView tableView) {
        Callback<TableColumn<InvoiceItems, String>, TableCell<InvoiceItems, String>> cellFactory = (TableColumn<InvoiceItems, String> param) -> new EditingCell();
        Callback<TableColumn<InvoiceItems, Boolean>, TableCell<InvoiceItems, Boolean>> booleanCellFactory = (TableColumn<InvoiceItems, Boolean> param) -> new EditingBooleanCell();

        // Setting the column properties for the invoice table view
        TableColumn<InvoiceItems, Boolean> paidColumn = new TableColumn("Paid");
        TableColumn<InvoiceItems, String> categoryColumn = new TableColumn("Category");
        TableColumn<InvoiceItems, String> costColumn = new TableColumn("Cost");
        TableColumn<InvoiceItems, String> costUnitColumn = new TableColumn("per");
        TableColumn<InvoiceItems, String> costUnitTotalColumn = new TableColumn("∑");
        TableColumn<InvoiceItems, String> shortDescriptionColumn = new TableColumn("Short Description");
        
        paidColumn.setCellValueFactory(new PropertyValueFactory<>("paid"));
        paidColumn.setCellFactory(booleanCellFactory);
        paidColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        paidColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, Boolean> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setPaid(t.getNewValue());
        });

        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), categories));
        categoryColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        categoryColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setCategory(t.getNewValue());
        });

        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        costColumn.setCellFactory(cellFactory);
        costColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        costColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setCost(t.getNewValue());
        });

        costUnitColumn.setCellValueFactory(new PropertyValueFactory<>("costUnit"));
        costUnitColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), unit));
        costUnitColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        costUnitColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setCostUnit(t.getNewValue());
        });

        costUnitTotalColumn.setCellValueFactory(new PropertyValueFactory<>("costUnitTotal"));
        costUnitTotalColumn.setCellFactory(cellFactory);
        costUnitTotalColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        costUnitTotalColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setCostUnitTotal(t.getNewValue());
        });

        shortDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("shortDescription"));
        shortDescriptionColumn.setCellFactory(cellFactory);
        shortDescriptionColumn.prefWidthProperty().bind(tableView.widthProperty().divide(6));
        shortDescriptionColumn.setOnEditCommit((TableColumn.CellEditEvent<InvoiceItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((InvoiceItems) t.getTableView().getItems().get(tableRow)).setShortDescription(t.getNewValue());
        });

        tableView.setItems(data);
        tableView.getColumns().addAll(categoryColumn, costColumn, costUnitColumn, costUnitTotalColumn, paidColumn, shortDescriptionColumn);
    }

    public static class InvoiceItems {

        public BooleanProperty paid;
        public SimpleStringProperty category;
        public SimpleStringProperty cost;
        public SimpleStringProperty costUnit;
        public SimpleStringProperty costUnitTotal;
        public SimpleStringProperty shortDescription;

        public InvoiceItems() {
        }

        public InvoiceItems(boolean paid, String category, String cost, String costUnit, String costUnitTotal, String shortDescription) {
            this.paid = new SimpleBooleanProperty(paid);
            this.category = new SimpleStringProperty(category);
            this.cost = new SimpleStringProperty(cost);
            this.costUnit = new SimpleStringProperty(costUnit);
            this.costUnitTotal = new SimpleStringProperty(costUnitTotal);
            this.shortDescription = new SimpleStringProperty(shortDescription);
        }

        public boolean isPaid() {
            return paid.get();
        }

        public void setPaid(boolean paid) {
            this.paid.set(paid);
        }

        public String getCategory() {
            return category.get();
        }

        public void setCategory(String category) {
            this.category.set(category);
        }

        public String getCost() {
            return cost.get();
        }

        public void setCost(String cost) {
            this.cost.set(cost);
        }

        public String getCostUnit() {
            return costUnit.get();
        }

        public void setCostUnit(String costUnit) {
            this.costUnit.set(costUnit);
        }

        public String getCostUnitTotal() {
            return costUnitTotal.get();
        }

        public void setCostUnitTotal(String costUnitTotal) {
            this.costUnitTotal.set(costUnitTotal);
        }

        public String getShortDescription() {
            return shortDescription.get();
        }

        public void setShortDescription(String shortDescription) {
            this.shortDescription.set(shortDescription);
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
