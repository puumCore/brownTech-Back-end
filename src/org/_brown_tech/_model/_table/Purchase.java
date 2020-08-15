package org._brown_tech._model._table;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class Purchase extends RecursiveTreeObject<Purchase> {

    public StringProperty date_time;
    public StringProperty receipt_no;
    public StringProperty billAmount;
    public StringProperty amount;
    public StringProperty username;


    public Purchase(String date_time, String receipt_no, String billAmount, String amount, String username) {
        this.date_time = new SimpleStringProperty(date_time);
        this.receipt_no = new SimpleStringProperty(receipt_no);
        this.billAmount = new SimpleStringProperty(billAmount);
        this.amount = new SimpleStringProperty(amount);
        this.username = new SimpleStringProperty(username);
    }

}
