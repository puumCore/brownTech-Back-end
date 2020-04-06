package org._brown_tech._object;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author Mandela
 */
public class Sale implements Serializable {

    public static final long serialVersionUID = 52L;

    public String dateAndTime;
    public Integer receiptNumber;
    public String fullname;
    public Double amount;
    public String paymentMethod;

    public Sale(String dateAndTime, Integer receiptNumber, String fullname, Double amount, String paymentMethod) {
        this.dateAndTime = dateAndTime;
        this.receiptNumber = receiptNumber;
        this.fullname = fullname;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Integer getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(Integer receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Sale.class);
    }
}
