package controller.inventory;
import controller.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.receipts.GoodsReceipt;
import model.receipts.ItemOrder;

public class InventoryManager {
    
    private String currentGoodsReceiptID;
    private ObservableList<GoodsReceipt> listGoodsReceipts;

    public InventoryManager()
    {
        currentGoodsReceiptID = "RB0000";
        setListGoodsReceipts(FXCollections.observableArrayList());
    }

    public void addGoodsReceipt(GoodsReceipt _goodsReceipt)
    {
        getListGoodsReceipts().add(_goodsReceipt);
        updateCurrentReceiptID(_goodsReceipt.getReceiptID());

        int no0fProducts = 0;
        for(ItemOrder i: _goodsReceipt.getListItems())
        {
            no0fProducts += i.getAmount();
        }

        App.dataManager.getCashflowManager().addInventory(_goodsReceipt.getDate(), _goodsReceipt.getTotalCost(), no0fProducts);
    }
    private void updateCurrentReceiptID(String _receiptID)
    {
        int currentNumber = Integer.valueOf(currentGoodsReceiptID.substring(2));
        int next = Integer.valueOf(_receiptID.substring(2));

        if(currentNumber < next)
        {
            currentGoodsReceiptID = _receiptID;
        }
    }

    public String getNextGoodsReceiptID()
    {
        int currentNumber = Integer.valueOf(currentGoodsReceiptID.substring(2));

        String nextNumber = String.valueOf(currentNumber + 1);

        if(nextNumber.length() < 4)
        {
            int currentLength = nextNumber.length();
            for(int i = 0; i < 4 - currentLength; i++)
            {
                nextNumber = "0".concat(nextNumber);
            }
        }

        String nextReceiptID = "RB".concat(nextNumber);
        return nextReceiptID;
    }

    public ObservableList<GoodsReceipt> getListGoodsReceipts() {
        return listGoodsReceipts;
    }

    public void setListGoodsReceipts(ObservableList<GoodsReceipt> listGoodsReceipts) {
        this.listGoodsReceipts = listGoodsReceipts;
    }


}
