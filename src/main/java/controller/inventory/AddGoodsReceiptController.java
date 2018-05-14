package controller.inventory;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import controller.App;
import controller.FinalPaths;
import controller.products.UpdateProductController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.product.Book;
import model.product.Mode;
import model.product.Product;
import model.receipts.GoodsReceipt;
import model.receipts.Category;
import model.receipts.ItemOrder;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class AddGoodsReceiptController implements Initializable {

    private Stage parentStage;

    @FXML private JFXTextField searchText;

    @FXML private JFXButton addButton;
    @FXML private JFXButton deleteButton;
    @FXML private JFXButton newProductButton;
    @FXML private JFXButton saveButton;
    @FXML private JFXButton cancelButton;

    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, String> statusColumn;
    @FXML private TableColumn<Product, String> quantityColumn;
    @FXML private TableColumn<Product, String> buyingPriceColumn;
    @FXML private TableColumn<Product, String> sellingPriceColumn;
    @FXML private TableColumn<Product, String> discountColumn;

    @FXML private TableView<ItemOrder> itemsTable;
    @FXML private TableColumn<ItemOrder, String> idItemColumn;
    @FXML private TableColumn<ItemOrder, String> nameItemColumn;
    @FXML private TableColumn<ItemOrder, String> categoryItemColumn;
    @FXML private TableColumn<ItemOrder, String> statusItemColumn;
    @FXML private TableColumn<ItemOrder, String> amountItemColumn;
    @FXML private TableColumn<ItemOrder, String> buyingPriceItemColumn;
    @FXML private TableColumn<ItemOrder, String> costColumn;

    @FXML private Label receiptIDLabel;
    @FXML private TextField supplierTextField;
    @FXML private JFXDatePicker datePicker;
    @FXML private Label purchaserLabel;
    @FXML private TextArea remarkTextArea;
    @FXML private Label totalCostLabel;

    private ObservableList<ItemOrder> listItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listItems = FXCollections.observableArrayList();

        bindItemsTable();
        bindProductsTable();
        handleProductsTableSelection();
        handleItemsTableSelection();
        handleAddButton();
        handleSaveButton();
        handleCancelButton();
        handleDeleteButton();

        setSellDate(LocalDate.now());

        setReceiptIDLabel();
        setPurchaserLabel();

        newProductButton.setOnAction(e -> {
            displayAddProductBox();

        });
    }

    private void bindItemsTable()
    {
        idItemColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(i.getProduct().getProductID());
        });

        nameItemColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(i.getProduct().getName());
        });

        categoryItemColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(i.getProduct().getCategory().toString());
        });

        statusItemColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(i.getProduct().getStatus().toString());
        });

        amountItemColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(String.valueOf(i.getAmount()));
        });

        buyingPriceItemColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            return new SimpleStringProperty(String.format("%.0f", i.getProduct().getBuyingPrice()));
        });

        costColumn.setCellValueFactory((TableColumn.CellDataFeatures<ItemOrder, String> cdf) -> {
            ItemOrder i = cdf.getValue();
            double cost = i.getAmount() * i.getProduct().getBuyingPrice();
            return new SimpleStringProperty(String.format("%.0f", cost));
        });

        itemsTable.setItems(listItems);
    }

    private void bindProductsTable()
    {
        idColumn.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> cdf) -> {
            Product p = cdf.getValue();
            return new SimpleStringProperty(p.getProductID());
        });

        nameColumn.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> cdf) -> {
            Product p = cdf.getValue();
            return new SimpleStringProperty(p.getName());
        });

        categoryColumn.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> cdf) -> {
            Product p = cdf.getValue();
            return new SimpleStringProperty(p.getCategory().toString());
        });

        statusColumn.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> cdf) -> {
            Product p = cdf.getValue();
            String status = p.getStatus().toString();
            return new SimpleStringProperty(status);
        });

        quantityColumn.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> cdf) -> {
            Product p = cdf.getValue();
            int quantity = p.getQuantity();

            return new SimpleStringProperty(String.valueOf(quantity));
        });

        buyingPriceColumn.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> cdf) -> {
            Product p = cdf.getValue();
            double buyingPrice = p.getBuyingPrice();

            return new SimpleStringProperty(String.format("%.0f", buyingPrice));
        });

        sellingPriceColumn.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> cdf) -> {
            Product p = cdf.getValue();
            double sellingPrice = p.getSellingPrice();

            return new SimpleStringProperty(String.format("%.0f", sellingPrice));
        });

        discountColumn.setCellValueFactory((TableColumn.CellDataFeatures<Product, String> cdf) -> {
            Product p = cdf.getValue();
            int discount = p.getDiscount();

            return new SimpleStringProperty(String.valueOf(discount+" %"));
        });

        ObservableList<Product> listProducts = App.dataManager.getProductsManager().getProducts();
        FilteredList<Product> filteredData = new FilteredList<>(listProducts, p -> true);

        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(product -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (product.getProductID().contains(lowerCaseFilter)) {
                    return true;
                } else if (product.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Product> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(productsTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        productsTable.setItems(sortedData);
    }

    private void handleProductsTableSelection()
    {
        productsTable.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );

        productsTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends Product> c) -> {
            int numberSelections = c.getList().toArray().length;

            if(numberSelections == 0)
                addButton.setDisable(true);
            else
                addButton.setDisable(false);
        });

        productsTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    addItem(row.getItem());
                }
            });
            return row;
        });
    }

    private void handleItemsTableSelection()
    {
        itemsTable.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );

        itemsTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends ItemOrder> c) -> {
            int numberSelections = c.getList().toArray().length;

            if(numberSelections == 0)
                deleteButton.setDisable(true);
            else
                deleteButton.setDisable(false);
        });
    }

    private int displayAmountBox()
    {
        int maxNumber = 1000;
        AtomicInteger selectedNumber = new AtomicInteger(0);

        Stage window = new Stage(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Amount");
        window.setMinWidth(300);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FinalPaths.INVENTORY_AMOUNT_BOX));
        AnchorPane amountLayout = null;
        try {
            amountLayout = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(amountLayout);

        JFXButton okButton =(JFXButton) scene.lookup("#okButton");
        JFXButton cancelButton =(JFXButton) scene.lookup("#cancelButton");
        JFXTextField amountTextField = (JFXTextField) scene.lookup("#amountTextField");

        okButton.setOnAction(e -> {
            try {

                selectedNumber.set(Integer.valueOf(amountTextField.getText()));
                window.close();
            } catch (Exception ex)
            {
                App.displayAlertingBox("Wrong number !");
            }
        });

        cancelButton.setOnAction(e -> {
            selectedNumber.set(0);
            window.close();
        });

        window.setScene(scene);
        window.showAndWait();

        return selectedNumber.get();
    }

    private void handleAddButton()
    {
        addButton.setOnAction(e -> {
            Product p = productsTable.getSelectionModel().getSelectedItem();
            addItem(p);
        });
    }

    private void addItem(Product p)
    {
        int amountSelected = 0;

        amountSelected = displayAmountBox();

        if(amountSelected > 0)
        {
            boolean isExisted = false;

            for(ItemOrder i: listItems)
            {
                if(i.getProduct() == p)
                {
                    i.setAmount(i.getAmount() + amountSelected);
                    p.setQuantity(p.getQuantity() + amountSelected);
                    isExisted = true;
                    break;
                }
            }

            if(!isExisted)
            {
                listItems.add(new ItemOrder(p, amountSelected));
                p.setQuantity(p.getQuantity() + amountSelected);
            }

            refresh();
        }
    }

    private void handleClosing()
    {
        int nSelected = listItems.size();
        if(nSelected == 0) parentStage.close();
        else {
            int select = App.displayConfirmBox("Are you sure to cancel ?");
            if (select == 1) {
                listItems.forEach(i -> {
                    i.getProduct().setQuantity(i.getProduct().getQuantity() - i.getAmount());
                });

                parentStage.close();
            }
        }
    }
    private void handleCancelButton()
    {
        cancelButton.setOnAction(e -> {
            handleClosing();
        });
    }

    public void setParentStage(Stage _stage)
    {
        parentStage = _stage;
        _stage.setOnCloseRequest(e -> {
            e.consume();
            handleClosing();
        });
    }

    private void handleSaveButton()
    {
        saveButton.setOnAction(e -> {

            int nItems = listItems.size();
            if(nItems == 0) App.displayAlertingBox("Nothing to save !");
            else {
                int saving = App.displayConfirmBox("Are you sure to save ?");
                if (saving == 1) {
                    handleSaving();
                    parentStage.close();
                }
            }
        });
    }

    private void handleSaving()
    {
        GoodsReceipt r;
        String _receiptID = receiptIDLabel.getText();
        String _supplier = supplierTextField.getText();
        LocalDate _date = datePicker.getValue();
        String _purchaser = purchaserLabel.getText();
        String _remark = remarkTextArea.getText();
        Double _totalCost = Double.valueOf(totalCostLabel.getText());

        ArrayList<ItemOrder> _listItems = new ArrayList<>();

        for(ItemOrder i: listItems)
        {
            ItemOrder _i = new ItemOrder(new Product(i.getProduct()), i.getAmount());
            _listItems.add(_i);
        }
        r = new GoodsReceipt(_receiptID, Category.SELL, _supplier, _purchaser, _totalCost, _date, _remark, _listItems);

        App.dataManager.getInventoryManager().addGoodsReceipt(r);
    }

    private void setReceiptIDLabel()
    {
        receiptIDLabel.setText(App.dataManager.getInventoryManager().getNextGoodsReceiptID());
    }

    private void setPurchaserLabel()
    {
        purchaserLabel.setText(App.getUser().getName());
    }

    private void setTotalCostLabel(double totalCost)
    {
        totalCostLabel.setText(String.format("%.0f", totalCost));
    }

    private void setSellDate(LocalDate _date)
    {
        datePicker.setValue(_date);
    }

    private void refresh()
    {
        productsTable.refresh();
        itemsTable.refresh();

        double totalCost = 0.0;

        for(ItemOrder i : listItems)
        {
            double cost = i.getProduct().getBuyingPrice() * i.getAmount();
            totalCost += cost;
        }
        setTotalCostLabel(totalCost);
    }

    private void handleDeleteButton()
    {
        deleteButton.setOnAction(e -> {
            ItemOrder i = itemsTable.getSelectionModel().getSelectedItem();
            i.getProduct().setQuantity(i.getProduct().getQuantity() - i.getAmount());
            listItems.remove(i);
            refresh();
        });
    }

    private void displayAddProductBox()
    {
        Stage window = new Stage(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add New product");
        window.setMinWidth(400);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FinalPaths.PRODUCTS_UPDATE));
        AnchorPane addLayout = null;
        try {
            addLayout = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(addLayout);

        UpdateProductController updateProductController = (UpdateProductController) fxmlLoader.getController();
        updateProductController.init(Mode.ADD, new Book());
        updateProductController.setParentStage(window);
        updateProductController.setParentScene(scene);

        window.setScene(scene);
        window.showAndWait();

        refresh();
    }
}

