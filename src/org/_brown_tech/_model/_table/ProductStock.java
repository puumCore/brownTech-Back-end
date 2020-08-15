package org._brown_tech._model._table;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class ProductStock extends RecursiveTreeObject<ProductStock> {

    public StringProperty serial;
    public StringProperty name;
    public StringProperty description;
    public SimpleObjectProperty<ImageView> imageView;
    public StringProperty quantity;
    public StringProperty buyingPrice;
    public StringProperty markedPrice;
    public StringProperty status;

    public void setSerial(String serial) {
        this.serial.set(serial);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setImageView(ImageView imageView) {
        this.imageView.set(imageView);
    }

    public void setQuantity(String quantity) {
        this.quantity.set(quantity);
    }

    public void setBuyingPrice(String buyingPrice) {
        this.buyingPrice.set(buyingPrice);
    }

    public void setMarkedPrice(String markedPrice) {
        this.markedPrice.set(markedPrice);
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

}
