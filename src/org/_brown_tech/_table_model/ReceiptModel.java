package org._brown_tech._table_model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class ReceiptModel extends RecursiveTreeObject<ReceiptModel> {

    public StringProperty receipt_no;
    public StringProperty date_time;
    public StringProperty serial_number;
    public StringProperty quantity_sold;
    public StringProperty selling_price;
    public StringProperty buying_price;
    public StringProperty typeOfStock;

    public ReceiptModel(String receipt_no, String date_time, String serial_number, String quantity_sold, String selling_price, String buying_price, String typeOfStock) {
        this.receipt_no = new SimpleStringProperty(receipt_no);
        this.date_time = new SimpleStringProperty(date_time);
        this.serial_number = new SimpleStringProperty(serial_number);
        this.quantity_sold = new SimpleStringProperty(quantity_sold);
        this.selling_price = new SimpleStringProperty(selling_price);
        this.buying_price = new SimpleStringProperty(buying_price);
        this.typeOfStock = new SimpleStringProperty(typeOfStock);
    }

    public String getReceipt_no() {
        return receipt_no.get();
    }

    public StringProperty receipt_noProperty() {
        return receipt_no;
    }

    public String getDate_time() {
        return date_time.get();
    }

    public StringProperty date_timeProperty() {
        return date_time;
    }

    public String getSerial_number() {
        return serial_number.get();
    }

    public StringProperty serial_numberProperty() {
        return serial_number;
    }

    public String getQuantity_sold() {
        return quantity_sold.get();
    }

    public StringProperty quantity_soldProperty() {
        return quantity_sold;
    }

    public String getSelling_price() {
        return selling_price.get();
    }

    public StringProperty selling_priceProperty() {
        return selling_price;
    }

    public String getOriginal_price() {
        return buying_price.get();
    }

    public StringProperty original_priceProperty() {
        return buying_price;
    }

}
