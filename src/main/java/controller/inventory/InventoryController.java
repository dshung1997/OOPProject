package controller.inventory;

import controller.App;
import controller.FinalPaths;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.receipts.GoodsReceipt;
import model.receipts.ItemOrder;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class InventoryController implements Initializable {

    @FXML
    private TableView<GoodsReceipt> goodsReceiptsTable;
    @FXML
    private TableColumn<GoodsReceipt, String> receiptIDColumn;
    @FXML
    private TableColumn<GoodsReceipt, String> dateColumn;
    @FXML
    private TableColumn<GoodsReceipt, String> supplierColumn;
    @FXML
    private TableColumn<GoodsReceipt, String> purchaserColumn;
    @FXML
    private TableColumn<GoodsReceipt, String> totalColumn;

    @FXML
    private TableView<ItemOrder> detailReceiptTable;
    @FXML
    private TableColumn<ItemOrder, String> itemIDColumn;
    @FXML
    private TableColumn<ItemOrder, String> itemNameColumn;
    @FXML
    private TableColumn<ItemOrder, String> amountColumn;
    @FXML
    private TableColumn<ItemOrder, String> buyingPriceColumn;
    @FXML
    private TableColumn<ItemOrder, String> totalPriceColumn;

    @FXML
    private JFXButton resetButton;
    @FXML
    private JFXButton addButton;
    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXTextField searchText;
    @FXML
    private JFXDatePicker fromDate;
    @FXML
    private JFXDatePicker toDate;

    @FXML
    private Label totalCostLabel;
    @FXML
    private TextArea remarkTextArea;

    @FXML private VBox detailPane;

    private FilteredList<GoodsReceipt> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureReceiptsTable();
        bindReceiptsTable();

        configureDetailReceiptTable();

        addButton.setOnAction(e -> {
            displayAddReceiptBox();
        });

        handleTextArea();
        handleSaveButton();
        handleResetButton();


    }

    public void bindReceiptsTable() {
        receiptIDColumn.setCellValueFactory((TableColumn.CellDataFeatures<GoodsReceipt, String> cdf) -> {
            GoodsReceipt r = cdf.getValue();
            return new SimpleStringProperty(r.getReceiptID());
        });

        dateColumn.setCellValueFactory((TableColumn.CellDataFeatures<GoodsReceipt, String> cdf) -> {
            GoodsReceipt r = cdf.getValue();
            return new SimpleStringProperty(r.getDate().toString());
        });

        supplierColumn.setCellValueFactory((TableColumn.CellDataFeatures<GoodsReceipt, String> cdf) -> {
            GoodsReceipt r = cdf.getValue();
            return new SimpleStringProperty(r.getSupplierName());
        });

        purchaserColumn.setCellValueFactory((TableColumn.CellDataFeatures<GoodsReceipt, String> cdf) -> {
            GoodsReceipt r = cdf.getValue();
            return new SimpleStringProperty(r.getPurchaserName());
        });

        totalColumn.setCellValueFactory((TableColumn.CellDataFeatures<GoodsReceipt, String> cdf) -> {
            GoodsReceipt r = cdf.getValue();
            double totalCost = r.getTotalCost();

            return new SimpleStringProperty(String.format("%.0f", totalCost));
        });

        ObservableList<GoodsReceipt> listGoodsReceipts = App.dataManager.getInventoryManager().getListGoodsReceipts();
        filteredData = new FilteredList<>(listGoodsReceipts, p -> true);

        ObjectProperty<Predicate<GoodsReceipt>> nameFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<GoodsReceipt>> fromDateFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<GoodsReceipt>> toDateFilter = new SimpleObjectProperty<>();

        nameFilter.bind(Bindings.createObjectBinding(() ->
                goodsReceipt -> {
                    String text = searchText.getText().toLowerCase();
                    if (text.equals("")) return true;
                    return (goodsReceipt.getReceiptID().toLowerCase().contains(text) || goodsReceipt.getPurchaserName().toLowerCase().contains(text) || goodsReceipt.getSupplierName().toLowerCase().contains(text));
                }, searchText.textProperty()));


        fromDateFilter.bind(Bindings.createObjectBinding(() ->
                goodsReceipt -> {
                    LocalDate date = fromDate.getValue();
                    if (date == null) return true;

                    return (goodsReceipt.getDate().isAfter(date) || goodsReceipt.getDate().isEqual(date));

                }, fromDate.valueProperty()));

        toDateFilter.bind(Bindings.createObjectBinding(() ->
                goodsReceipt -> {
                    LocalDate date = toDate.getValue();

                    if (date == null) return true;

                    return (goodsReceipt.getDate().isBefore(date) || goodsReceipt.getDate().isEqual(date));

                }, toDate.valueProperty()));

        filteredData.predicateProperty().bind(Bindings.createObjectBinding(
                () -> nameFilter.get().and(fromDateFilter.get()).and(toDateFilter.get()),
                nameFilter, fromDateFilter, toDateFilter));


        SortedList<GoodsReceipt> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(goodsReceiptsTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        goodsReceiptsTable.setItems(sortedData);

    }

    private void configureReceiptsTable() {
        goodsReceiptsTable.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );

        goodsReceiptsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                bindDetailReceiptTable(newSelection);
                setTotalCostLabel(newSelection.getTotalCost());
                setRemarkTextArea(newSelection.getRemark());

                remarkTextArea.setDisable(false);
                detailPane.setVisible(true);
            }
            else {
                detailPane.setVisible(false);
                detailReceiptTable.setItems(null);
                setTotalCostLabel(0.0);
                setRemarkTextArea("");
                saveButton.setDisable(true);
                remarkTextArea.setDisable(true);
            }
        });
    }

    public void configureDetailReceiptTable() {
        itemIDColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(i.getProduct().getProductID());
        });

        itemNameColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(i.getProduct().getName());
        });

        amountColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(String.valueOf(i.getAmount()));
        });

        buyingPriceColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(String.format("%.0f", i.getProduct().getBuyingPrice()));
        });

        totalPriceColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            double cost = i.getProduct().getSellingPrice() * i.getAmount() * (1 - (i.getProduct().getDiscount() / 100));
            return new SimpleStringProperty(String.format("%.0f", cost));
        });

    }

    private void bindDetailReceiptTable(GoodsReceipt _sellReceipt) {
        ObservableList<ItemOrder> obsListItems = FXCollections.observableArrayList(_sellReceipt.getListItems());
        detailReceiptTable.setItems(obsListItems);
    }


    private void displayAddReceiptBox() {
        Stage window = new Stage(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add New GoodsReceipt");
        window.setMinWidth(1210);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FinalPaths.INVENTORY_APP_BUY_RECEIPT));
        AnchorPane addLayout = null;
        try {
            addLayout = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(addLayout);

        AddGoodsReceiptController addGoodsReceiptController = (AddGoodsReceiptController) fxmlLoader.getController();
        addGoodsReceiptController.setParentStage(window);

        window.setScene(scene);
        window.showAndWait();
    }

    private void setTotalCostLabel(Double _cost) {
        totalCostLabel.setText(String.valueOf(_cost));
    }

    private void setRemarkTextArea(String _text) {
        remarkTextArea.setText(_text);
    }

    private void handleTextArea() {
        remarkTextArea.textProperty().addListener((observable, oldValue, newValue) -> {

            GoodsReceipt currentSelected = goodsReceiptsTable.getSelectionModel().getSelectedItem();

            if(newValue != null && oldValue != null)
            {
                if (!newValue.equals(oldValue) && !newValue.equals(currentSelected.getRemark())) {
                    saveButton.setDisable(false);
                } else saveButton.setDisable(true);
            }
        });
    }

    private void handleSaveButton() {
        saveButton.setOnAction(e -> {
            GoodsReceipt currentSelected = goodsReceiptsTable.getSelectionModel().getSelectedItem();
            currentSelected.setRemark(remarkTextArea.getText());
            saveButton.setDisable(true);
        });
    }

    private void handleResetButton() {
        resetButton.setOnAction(e -> {
            searchText.setText("");
            fromDate.setValue(null);
            toDate.setValue(null);
        });
    }

    public void setDates(LocalDate from, LocalDate to) {
        fromDate.setValue(from);
        toDate.setValue(to);
    }
}
