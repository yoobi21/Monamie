package com.android.monamie.models;

public class Product {
    private int id;
    private String name;
    private String category;
    private int price;
    private int imageRes;

    public Product() { }

    public Product(int id, String name, String category, int price, int imageRes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.imageRes = imageRes;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getPrice() { return price; }
    public int getImageRes() { return imageRes; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String cat) { this.category = cat; }
    public void setPrice(int price) { this.price = price; }
    public void setImageRes(int imageRes) { this.imageRes = imageRes; }
}