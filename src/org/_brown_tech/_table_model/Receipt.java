package org._brown_tech._table_model;

import com.google.gson.Gson;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Mandela
 */
public class Receipt extends RecursiveTreeObject<Receipt> {

    public StringProperty receiptNumber;
    public StringProperty dateAndTime;
    public StringProperty productSerial;
    public StringProperty quantitySold;
    public StringProperty sellingPrice;
    public StringProperty buyingPrice;
    public StringProperty typeOfStock;

    public Receipt(String receiptNumber, String dateAndTime, String productSerial, String quantitySold, String sellingPrice, String buyingPrice, String typeOfStock) {
        this.receiptNumber = new SimpleStringProperty(receiptNumber);
        this.dateAndTime = new SimpleStringProperty(dateAndTime);
        this.productSerial = new SimpleStringProperty(productSerial);
        this.quantitySold = new SimpleStringProperty(quantitySold);
        this.sellingPrice = new SimpleStringProperty(sellingPrice);
        this.buyingPrice = new SimpleStringProperty(buyingPrice);
        this.typeOfStock = new SimpleStringProperty(typeOfStock);
    }

    public String getReceiptNumber() {
        return receiptNumber.get();
    }

    public StringProperty receiptNumberProperty() {
        return receiptNumber;
    }

    public String getDateAndTime() {
        return dateAndTime.get();
    }

    public StringProperty dateAndTimeProperty() {
        return dateAndTime;
    }

    public String getProductSerial() {
        return productSerial.get();
    }

    public StringProperty productSerialProperty() {
        return productSerial;
    }

    public String getQuantitySold() {
        return quantitySold.get();
    }

    public StringProperty quantitySoldProperty() {
        return quantitySold;
    }

    public String getSellingPrice() {
        return sellingPrice.get();
    }

    public StringProperty sellingPriceProperty() {
        return sellingPrice;
    }

    public String getBuyingPrice() {
        return buyingPrice.get();
    }

    public StringProperty buyingPriceProperty() {
        return buyingPrice;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Receipt.class);
    }
}
