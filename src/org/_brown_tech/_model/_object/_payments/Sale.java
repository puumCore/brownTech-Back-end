package org._brown_tech._model._object._payments;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class Sale implements Serializable {

    public static final long serialVersionUID = 7L;

    private Integer receiptId;
    private String dateTime;
    private Double billAmount;
    private int paymentMethod;
    private String username;
    private List<ReceiptObj> receiptObjs;

    public Sale() {
    }

    public Integer getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Integer receiptId) {
        this.receiptId = receiptId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Double billAmount) {
        this.billAmount = billAmount;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ReceiptObj> getReceiptObjs() {
        return receiptObjs;
    }

    public void setReceiptObjs(List<ReceiptObj> receiptObjs) {
        this.receiptObjs = receiptObjs;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Sale.class);
    }
}
