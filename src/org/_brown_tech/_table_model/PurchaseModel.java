package org._brown_tech._table_model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class PurchaseModel extends RecursiveTreeObject<PurchaseModel> {

    public StringProperty date_time;
    public StringProperty receipt_no;
    public StringProperty billAmount;
    public StringProperty amount;
    public StringProperty username;


    public PurchaseModel(String date_time, String receipt_no, String billAmount, String amount, String username) {
        this.date_time = new SimpleStringProperty(date_time);
        this.receipt_no = new SimpleStringProperty(receipt_no);
        this.billAmount = new SimpleStringProperty(billAmount);
        this.amount = new SimpleStringProperty(amount);
        this.username = new SimpleStringProperty(username);
    }

    public String getDate_time() {
        return date_time.get();
    }

    public StringProperty date_timeProperty() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time.set(date_time);
    }

    public String getReceipt_no() {
        return receipt_no.get();
    }

    public StringProperty receipt_noProperty() {
        return receipt_no;
    }

    public void setReceipt_no(String receipt_no) {
        this.receipt_no.set(receipt_no);
    }

    public String getBillAmount() {
        return billAmount.get();
    }

    public StringProperty billAmountProperty() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount.set(billAmount);
    }

    public String getAmount() {
        return amount.get();
    }

    public StringProperty amountProperty() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

}
