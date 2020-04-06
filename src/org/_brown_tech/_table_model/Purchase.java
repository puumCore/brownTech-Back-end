package org._brown_tech._table_model;

import com.google.gson.Gson;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.*;

/**
 * @author Mandela
 */
public class Purchase extends RecursiveTreeObject<Purchase> {

    public StringProperty dateTime;
    public StringProperty receiptNumber;
    public StringProperty billAmount;
    public StringProperty paymentMethod;
    public StringProperty nameOfStaffWhoMadeTheSale;


    public Purchase(String dateTime, String  receiptNumber, String billAmount, String paymentMethod, String nameOfStaffWhoMadeTheSale) {
        this.dateTime = new SimpleStringProperty(dateTime);
        this.receiptNumber = new SimpleStringProperty(receiptNumber);
        this.billAmount = new SimpleStringProperty(billAmount);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.nameOfStaffWhoMadeTheSale = new SimpleStringProperty(nameOfStaffWhoMadeTheSale);
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime.set(dateTime);
    }

    public String getReceiptNumber() {
        return receiptNumber.get();
    }

    public StringProperty receiptNumberProperty() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber.set(receiptNumber);
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

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public StringProperty paymentMethodProperty() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public String getNameOfStaffWhoMadeTheSale() {
        return nameOfStaffWhoMadeTheSale.get();
    }

    public StringProperty nameOfStaffWhoMadeTheSaleProperty() {
        return nameOfStaffWhoMadeTheSale;
    }

    public void setNameOfStaffWhoMadeTheSale(String nameOfStaffWhoMadeTheSale) {
        this.nameOfStaffWhoMadeTheSale.set(nameOfStaffWhoMadeTheSale);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Purchase.class);
    }
}
