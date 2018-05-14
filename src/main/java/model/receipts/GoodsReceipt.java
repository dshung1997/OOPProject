package model.receipts;

import controller.App;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class GoodsReceipt extends Receipt {

//    private String receiptID;
//    private Enum<Category> category;
//    private double totalCost;
//    private LocalDate date;
//    private String remark;
//
//    private ArrayList<ItemOrder> listItems;


    private String supplierName;
    private String purchaserName;

    public GoodsReceipt()
    {
        setCategory(Category.SELL);
        setReceiptID("RB0000");
        setSupplierName("1");
        setListItems(new ArrayList<>());
        setTotalCost(0);
        setRemark("1");
        setDate(LocalDateTime.now().toLocalDate());
        setPurchaserName("1");
    }

    public GoodsReceipt(String _receiptID, Enum<Category> _category, String _supplierName, String _purchaserName, double _totalCost, LocalDate _date, String _remark, ArrayList<ItemOrder> _listItems)
    {
        setCategory(_category);
        setReceiptID(_receiptID);
        setSupplierName(_supplierName);

        setTotalCost(_totalCost);
        setRemark(_remark);
        setDate(_date);
        setPurchaserName(_purchaserName);

        setListItems(_listItems);
    }

    public void calculateTotalCost()
    {
        for(ItemOrder item: getListItems())
        {
            double cost = item.getProduct().getBuyingPrice() * item.getAmount();
            setTotalCost(getTotalCost() + cost);
        }
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPurchaserName() {
        return purchaserName;
    }

    public void setPurchaserName(String purchaserName) {
        this.purchaserName = purchaserName;
    }


    public String toString()
    {
        String text = "";
        text = text.concat(getReceiptID());
        text = text.concat("|");

        text = text.concat(getCategory().toString());
        text = text.concat("|");

        text = text.concat(getSupplierName());
        text = text.concat("|");

        text = text.concat(getPurchaserName());
        text = text.concat("|");

        text = text.concat(String.valueOf(getTotalCost()));
        text = text.concat("|");

        text = text.concat(getDate().toString());
        text = text.concat("|");

        text = text.concat(App.stringToAscii(getRemark()));
        text = text.concat(" |");

        text = text.concat(String.valueOf(getListItems().size()));
        text = text.concat("\n");

        for(ItemOrder i: listItems)
        {
            text = text.concat(String.valueOf(i.getAmount()));
            text = text.concat("|");

            text = text.concat(i.getProduct().toString());
            text = text.concat("\n");
        }

        return text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
