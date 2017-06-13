/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quotes;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
public class QuoteItemizationTable {

    private final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    private final NumberFormat TIME_FORMAT = NumberFormat.getNumberInstance();
    private double sum = 0.0;
    private List<Double> totalCost = new ArrayList<>();
    private final ObservableList<String> categories = FXCollections.observableArrayList("New Development", "Design", "Updates", "Marketing", "Logo Development", "Consultation", "Set Up", "Other");
    private final ObservableList<String> unit = FXCollections.observableArrayList("Hour", "Project");

    public QuoteItemizationTable(TableView tableView) {
        invoiceItemizationTable(tableView);
    }

    public ObservableList<QuoteItems> data = FXCollections.observableArrayList(new QuoteItems(null, "0.00", null, "0.00", null));

    private void invoiceItemizationTable(TableView tableView) {
        Callback<TableColumn<QuoteItems, String>, TableCell<QuoteItems, String>> cellFactory = (TableColumn<QuoteItems, String> param) -> new EditingCell();
        Callback<TableColumn<QuoteItems, Boolean>, TableCell<QuoteItems, Boolean>> booleanCellFactory = (TableColumn<QuoteItems, Boolean> param) -> new EditingBooleanCell();
        Callback<TableColumn<QuoteItems, String>, TableCell<QuoteItems, String>> currencyCellFactory = (TableColumn<QuoteItems, String> param) -> new EditingCurrencyCell();
        Callback<TableColumn<QuoteItems, String>, TableCell<QuoteItems, String>> timeCellFactory = (TableColumn<QuoteItems, String> param) -> new EditingTimeCell();

        // Setting the column properties for the invoice table view
        TableColumn<QuoteItems, String> categoryColumn = new TableColumn("Category");
        TableColumn<QuoteItems, String> costColumn = new TableColumn("Price");
        TableColumn<QuoteItems, String> costUnitColumn = new TableColumn("Price Unit");
        TableColumn<QuoteItems, String> costUnitTotalColumn = new TableColumn("Unit ∑");
        TableColumn<QuoteItems, String> shortDescriptionColumn = new TableColumn("Description");
        

        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), categories));
        categoryColumn.setPrefWidth(150);
        categoryColumn.setOnEditCommit((TableColumn.CellEditEvent<QuoteItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((QuoteItems) t.getTableView().getItems().get(tableRow)).setCategory(t.getNewValue());
        });

        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        costColumn.setCellFactory(currencyCellFactory);
        costColumn.setPrefWidth(64);
        costColumn.setOnEditCommit((TableColumn.CellEditEvent<QuoteItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((QuoteItems) t.getTableView().getItems().get(tableRow)).setCost((t.getNewValue()));
        });

        costUnitColumn.setCellValueFactory(new PropertyValueFactory<>("costUnit"));
        costUnitColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new DefaultStringConverter(), unit));
        costUnitColumn.setPrefWidth(85);
        costUnitColumn.setOnEditCommit((TableColumn.CellEditEvent<QuoteItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((QuoteItems) t.getTableView().getItems().get(tableRow)).setCostUnit(t.getNewValue());
        });

        costUnitTotalColumn.setCellValueFactory(new PropertyValueFactory<>("costUnitTotal"));
        costUnitTotalColumn.setCellFactory(timeCellFactory);
        costUnitTotalColumn.setPrefWidth(64);
        costUnitTotalColumn.setOnEditCommit((TableColumn.CellEditEvent<QuoteItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((QuoteItems) t.getTableView().getItems().get(tableRow)).setCostUnitTotal(t.getNewValue());
        });

        shortDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("shortDescription"));
        shortDescriptionColumn.setCellFactory(cellFactory);
        shortDescriptionColumn.setPrefWidth(300);
        shortDescriptionColumn.setOnEditCommit((TableColumn.CellEditEvent<QuoteItems, String> t) -> {
            int tableRow = t.getTablePosition().getRow();
            ((QuoteItems) t.getTableView().getItems().get(tableRow)).setShortDescription(t.getNewValue());
        });

        tableView.setItems(data);
        tableView.getColumns().addAll(categoryColumn, costColumn, costUnitColumn, costUnitTotalColumn, shortDescriptionColumn);
    }

    public static class QuoteItems {

        public SimpleStringProperty category;
        public SimpleStringProperty cost;
        public SimpleStringProperty costUnit;
        public SimpleStringProperty costUnitTotal;
        public SimpleStringProperty shortDescription;

        public QuoteItems() {
        }

        public QuoteItems(String category, String cost, String costUnit, String costUnitTotal, String shortDescription) {
            this.category = new SimpleStringProperty(category);
            this.cost = new SimpleStringProperty(cost);
            this.costUnit = new SimpleStringProperty(costUnit);
            this.costUnitTotal = new SimpleStringProperty(costUnitTotal);
            this.shortDescription = new SimpleStringProperty(shortDescription);
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

    public class EditingBooleanCell extends TableCell<QuoteItems, Boolean> {

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
        private TableColumn<QuoteItems, ?> getNextColumn(boolean forward) {
            List<TableColumn<QuoteItems, ?>> columns = new ArrayList<>();
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

        private List<TableColumn<QuoteItems, ?>> getLeaves(
                TableColumn<QuoteItems, ?> root) {
            List<TableColumn<QuoteItems, ?>> columns = new ArrayList<>();
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

    public class EditingCell extends TableCell<QuoteItems, String> {

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
        private TableColumn<QuoteItems, ?> getNextColumn(boolean forward) {
            List<TableColumn<QuoteItems, ?>> columns = new ArrayList<>();
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

        private List<TableColumn<QuoteItems, ?>> getLeaves(
                TableColumn<QuoteItems, ?> root) {
            List<TableColumn<QuoteItems, ?>> columns = new ArrayList<>();
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
    
    public class EditingCurrencyCell extends TableCell<QuoteItems, String> {

        private TextField textField;

        public EditingCurrencyCell() {
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
                        textField.setText(CURRENCY_FORMAT.format(Double.parseDouble(getString())));
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(CURRENCY_FORMAT.format(Double.parseDouble(getString())));
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
        private TableColumn<QuoteItems, ?> getNextColumn(boolean forward) {
            List<TableColumn<QuoteItems, ?>> columns = new ArrayList<>();
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

        private List<TableColumn<QuoteItems, ?>> getLeaves(
                TableColumn<QuoteItems, ?> root) {
            List<TableColumn<QuoteItems, ?>> columns = new ArrayList<>();
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
    
     public class EditingTimeCell extends TableCell<QuoteItems, String> {

        private TextField textField;

        public EditingTimeCell() {
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
                        textField.setText(TIME_FORMAT.format(Double.parseDouble(getString())));
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(TIME_FORMAT.format(Double.parseDouble(getString())));
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
        private TableColumn<QuoteItems, ?> getNextColumn(boolean forward) {
            List<TableColumn<QuoteItems, ?>> columns = new ArrayList<>();
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

        private List<TableColumn<QuoteItems, ?>> getLeaves(
                TableColumn<QuoteItems, ?> root) {
            List<TableColumn<QuoteItems, ?>> columns = new ArrayList<>();
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
