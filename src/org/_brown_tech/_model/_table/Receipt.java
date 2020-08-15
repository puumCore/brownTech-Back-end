package org._brown_tech._model._table;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class Receipt extends RecursiveTreeObject<Receipt> {

    public StringProperty receipt_no;
    public StringProperty date_time;
    public StringProperty serial_number;
    public StringProperty quantity_sold;
    public StringProperty selling_price;
    public StringProperty buying_price;
    public StringProperty typeOfStock;

    public Receipt(String receipt_no, String date_time, String serial_number, String quantity_sold, String selling_price, String buying_price, String typeOfStock) {
        this.receipt_no = new SimpleStringProperty(receipt_no);
        this.date_time = new SimpleStringProperty(date_time);
        this.serial_number = new SimpleStringProperty(serial_number);
        this.quantity_sold = new SimpleStringProperty(quantity_sold);
        this.selling_price = new SimpleStringProperty(selling_price);
        this.buying_price = new SimpleStringProperty(buying_price);
        this.typeOfStock = new SimpleStringProperty(typeOfStock);
    }

}
