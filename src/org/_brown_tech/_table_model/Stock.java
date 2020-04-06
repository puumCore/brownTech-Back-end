package org._brown_tech._table_model;

import com.google.gson.Gson;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

/**
 * @author Mandela
 */
public class Stock extends RecursiveTreeObject<Stock> {

    public static final long serialVersionUID = 42L;

    public StringProperty serial, name, description;
    public SimpleObjectProperty<ImageView> imageView;
    public StringProperty quantity;
    public StringProperty buyingPrice, markedPrice;
    public StringProperty status;

    public Stock() {
    }

    public Stock(String serial, String name, String description, ImageView imageView, String quantity, String buyingPrice, String markedPrice, String status) {
        this.serial = new SimpleStringProperty(serial);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.imageView = new SimpleObjectProperty<>(imageView);
        this.quantity = new SimpleStringProperty(quantity);
        this.buyingPrice = new SimpleStringProperty(buyingPrice);
        this.markedPrice = new SimpleStringProperty(markedPrice);
        this.status = new SimpleStringProperty(status);
    }

    public String getSerial() {
        return serial.get();
    }

    public void setSerial(String serial) {
        this.serial = new SimpleStringProperty(serial);
    }

    public StringProperty serialProperty() {
        return serial;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description = new SimpleStringProperty(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ImageView getImageView() {
        return imageView.get();
    }

    public void setImageView(ImageView imageView) {
        this.imageView = new SimpleObjectProperty<>(imageView);
    }

    public SimpleObjectProperty<ImageView> imageViewProperty() {
        return imageView;
    }

    public String getQuantity() {
        return quantity.get();
    }

    public void setQuantity(String quantity) {
        this.quantity = new SimpleStringProperty(quantity);
    }

    public StringProperty quantityProperty() {
        return quantity;
    }

    public String getBuyingPrice() {
        return buyingPrice.get();
    }

    public void setBuyingPrice(String buyingPrice) {
        this.buyingPrice = new SimpleStringProperty(buyingPrice);
    }

    public StringProperty buyingPriceProperty() {
        return buyingPrice;
    }

    public String getMarkedPrice() {
        return markedPrice.get();
    }

    public void setMarkedPrice(String markedPrice) {
        this.markedPrice = new SimpleStringProperty(markedPrice);
    }

    public StringProperty markedPriceProperty() {
        return markedPrice;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Stock.class);
    }
}
