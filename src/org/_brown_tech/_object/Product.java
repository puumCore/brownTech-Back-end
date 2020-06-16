package org._brown_tech._object;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class Product implements Serializable {

    public static final long serialVersionUID = 5L;

    private String serial_number;
    private String name;
    private String description;
    private Integer rating, stock;
    private Double markedPrice;
    private Double buyingPrice;
    private String image;
    private Boolean isAvailable;

    public Product() {
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getMarkedPrice() {
        return markedPrice;
    }

    public void setMarkedPrice(Double markedPrice) {
        this.markedPrice = markedPrice;
    }

    public Double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(Double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public void clear() {
        setSerial_number(null);
        setName(null);
        setDescription(null);
        setRating(null);
        setStock(null);
        setMarkedPrice(null);
        setBuyingPrice(null);
        setImage(null);
        setAvailable(null);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Product.class);
    }
}
