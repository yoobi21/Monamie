package com.android.monamie.models;

public class Product {
    private int id;
    private String name;
    private String category;
    private int price;
    private int imageRes;
    private int stock;

    public Product() { }

    public Product(int id, String name, String category, int price, int imageRes) {
        this(id, name, category, price, imageRes, 99); // Default stock if not specified
    }

    public Product(int id, String name, String category, int price, int imageRes, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.imageRes = imageRes;
        this.stock = stock;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getPrice() { return price; }
    public int getImageRes() { return imageRes; }
    public int getStock() { return stock; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String cat) { this.category = cat; }
    public void setPrice(int price) { this.price = price; }
    public void setImageRes(int imageRes) { this.imageRes = imageRes; }
    public void setStock(int stock) { this.stock = stock; }
}