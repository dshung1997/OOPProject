package controller.sales;

        import controller.App;
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
        import javafx.stage.Modality;
        import javafx.stage.Stage;
        import model.receipts.ItemOrder;
        import model.receipts.SellReceipt;
        import com.jfoenix.controls.*;
        import javafx.fxml.FXML;
        import javafx.fxml.Initializable;

        import java.io.IOException;
        import java.net.URL;
        import java.time.LocalDate;
        import java.util.ResourceBundle;
        import java.util.function.Predicate;

public class SalesController implements Initializable {

    @FXML private TableView<SellReceipt> sellReceiptsTable;
    @FXML private TableColumn<SellReceipt, String> receiptIDColumn;
    @FXML private TableColumn<SellReceipt, String> saleDateColumn;
    @FXML private TableColumn<SellReceipt, String> customerColumn;
    @FXML private TableColumn<SellReceipt, String> cashierColumn;
    @FXML private TableColumn<SellReceipt, String> totalColumn;

    @FXML private TableView<ItemOrder> detailReceiptTable;
    @FXML private TableColumn<ItemOrder, String> itemIDColumn;
    @FXML private TableColumn<ItemOrder, String> itemNameColumn;
    @FXML private TableColumn<ItemOrder, String> amountColumn;
    @FXML private TableColumn<ItemOrder, String> sellingPriceColumn;
    @FXML private TableColumn<ItemOrder, String> totalPriceColumn;

    @FXML private JFXButton resetButton;
    @FXML private JFXButton addButton;
    @FXML private JFXButton saveButton;
    @FXML private JFXButton writeButton;

    @FXML private JFXTextField searchText;
    @FXML private JFXComboBox<String> searchType;
    @FXML private JFXDatePicker fromDate;
    @FXML private JFXDatePicker toDate;

    @FXML private Label totalCostLabel;
    @FXML private TextArea remarkTextArea;

    FilteredList<SellReceipt> filteredData;


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


        //writeButton.setOnAction(e -> App.dataManager.writeSellReceiptsFile());
    }

    public void bindReceiptsTable()
    {
        receiptIDColumn.setCellValueFactory((TableColumn.CellDataFeatures<SellReceipt, String> cdf) -> {
            SellReceipt r = cdf.getValue();
            return new SimpleStringProperty(r.getReceiptID());
        });

        saleDateColumn.setCellValueFactory((TableColumn.CellDataFeatures<SellReceipt, String> cdf) -> {
            SellReceipt r = cdf.getValue();
            return new SimpleStringProperty(r.getDate().toString());
        });

        customerColumn.setCellValueFactory((TableColumn.CellDataFeatures<SellReceipt, String> cdf) -> {
            SellReceipt r = cdf.getValue();
            return new SimpleStringProperty(r.getCustomerName());
        });

        cashierColumn.setCellValueFactory((TableColumn.CellDataFeatures<SellReceipt, String> cdf) -> {
            SellReceipt r = cdf.getValue();
            return new SimpleStringProperty(r.getCashierName());
        });

        totalColumn.setCellValueFactory((TableColumn.CellDataFeatures<SellReceipt, String> cdf) -> {
            SellReceipt r = cdf.getValue();
            double totalCost = r.getTotalCost();

            return new SimpleStringProperty(String.format("%.0f", totalCost));
        });

        ObservableList<SellReceipt> listSellReceipts = App.dataManager.getSalesManager().getListSellReceipts();
        filteredData = new FilteredList<>(listSellReceipts, p -> true);

        ObjectProperty<Predicate<SellReceipt>> nameFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<SellReceipt>> fromDateFilter = new SimpleObjectProperty<>();
        ObjectProperty<Predicate<SellReceipt>> toDateFilter = new SimpleObjectProperty<>();

        nameFilter.bind(Bindings.createObjectBinding(() ->
                sellReceipt -> {
                    String text = searchText.getText().toLowerCase();
                    if(text.equals("")) return true;
                    return (sellReceipt.getReceiptID().toLowerCase().contains(text) || sellReceipt.getCashierName().toLowerCase().contains(text) || sellReceipt.getCustomerName().toLowerCase().contains(text));
                }, searchText.textProperty()));


        fromDateFilter.bind(Bindings.createObjectBinding(() ->
                sellReceipt -> {
                    LocalDate date = fromDate.getValue();
                    if(date == null) return true;

                    return (sellReceipt.getDate().isAfter(date) || sellReceipt.getDate().isEqual(date));

                }, fromDate.valueProperty()));

        toDateFilter.bind(Bindings.createObjectBinding(() ->
                sellReceipt -> {
                    LocalDate date = toDate.getValue();

                    if(date == null) return true;

                    return (sellReceipt.getDate().isBefore(date) || sellReceipt.getDate().isEqual(date));

                }, toDate.valueProperty()));

        filteredData.predicateProperty().bind(Bindings.createObjectBinding(
                () -> nameFilter.get().and(fromDateFilter.get()).and(toDateFilter.get()),
                nameFilter, fromDateFilter, toDateFilter));


        SortedList<SellReceipt> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(sellReceiptsTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        sellReceiptsTable.setItems(sortedData);

    }

    private void configureReceiptsTable()
    {
        sellReceiptsTable.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );

        sellReceiptsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                bindDetailReceiptTable(newSelection);
                setTotalCostLabel(newSelection.getTotalCost());
                setRemarkTextArea(newSelection.getRemark());

                remarkTextArea.setDisable(false);

                System.out.println(newSelection.toString());
            }
            else
            {
                detailReceiptTable.setItems(null);
                setTotalCostLabel(0.0);
                setRemarkTextArea("");
                saveButton.setDisable(true);
                remarkTextArea.setDisable(true);
            }
        });



    }

    public void configureDetailReceiptTable()
    {
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

        sellingPriceColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(String.format(".0f", i.getProduct().getSellingPrice()));
        });

        totalPriceColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            double cost = i.getProduct().getSellingPrice() * i.getAmount() * (1 - (i.getProduct().getDiscount() / 100));
            return new SimpleStringProperty(String.format(".0f", cost));
        });

    }

    private void bindDetailReceiptTable(SellReceipt _sellReceipt)
    {
        ObservableList<ItemOrder> obsListItems = FXCollections.observableArrayList(_sellReceipt.getListItems());
        detailReceiptTable.setItems(obsListItems);
    }


    private void displayAddReceiptBox()
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add New SellReceipt");
        window.setMinWidth(1210);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/sales/AddSellReceipt.fxml"));
        AnchorPane addLayout = null;
        try {
            addLayout = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(addLayout);

        AddSellReceiptController addSellReceiptController = (AddSellReceiptController) fxmlLoader.getController();
        addSellReceiptController.setParentStage(window);

        window.setScene(scene);
        window.showAndWait();
    }

    private void setTotalCostLabel(Double _cost)
    {
        totalCostLabel.setText(String.format("%.0f",_cost));
    }

    private void setRemarkTextArea(String _text)
    {
        remarkTextArea.setText(_text);
    }

    private void handleTextArea()
    {
        remarkTextArea.textProperty().addListener((observable, oldValue, newValue) -> {

            SellReceipt currentSelected = sellReceiptsTable.getSelectionModel().getSelectedItem();

            if(!newValue.equals(oldValue) && !newValue.equals(currentSelected.getRemark()))
            {
                saveButton.setDisable(false);
            }
            else saveButton.setDisable(true);
        });
    }

    private void handleSaveButton()
    {
        saveButton.setOnAction(e -> {
            SellReceipt currentSelected = sellReceiptsTable.getSelectionModel().getSelectedItem();
            currentSelected.setRemark(remarkTextArea.getText());
            saveButton.setDisable(true);
        });
    }

    private void handleResetButton()
    {
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