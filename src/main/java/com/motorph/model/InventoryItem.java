package com.motorph.model;

import java.time.LocalDate;

public class InventoryItem {
    private String productId;
    private String productName;
    private int quantity;
    private double price;
    private String category;
    private LocalDate entryDate;

    public InventoryItem(String productId, String productName, int quantity, double price, String category) {
        this.productId = productId;
        this.productName = productName;
        setQuantity(quantity);
        setPrice(price);
        this.category = category;
        this.entryDate = LocalDate.now();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void updateStock(int amount) {
        if (this.quantity + amount < 0) {
            throw new IllegalArgumentException("Insufficient stock to remove " + Math.abs(amount) + " items.");
        }
        this.quantity += amount;
    }

    public boolean isLowStock(int threshold) {
        return this.quantity <= threshold;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "id='" + productId + '\'' +
                ", name='" + productName + '\'' +
                ", qty=" + quantity +
                ", price=" + price +
                ", category='" + category + '\'' +
                '}';
    }
}
