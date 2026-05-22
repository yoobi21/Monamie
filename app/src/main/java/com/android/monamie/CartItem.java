package com.android.monamie;

public class CartItem {
    private String productId;
    private String name;
    private int    price;
    private int    quantity;
    private int    imageRes;

    public CartItem() {}

    public CartItem(String productId, String name, int price, int quantity, int imageRes) {
        this.productId = productId;
        this.name      = name;
        this.price     = price;
        this.quantity  = quantity;
        this.imageRes  = imageRes;
    }

    public int    getSubtotal()   { return price * quantity; }
    public String getProductId()  { return productId; }
    public String getName()       { return name; }
    public int    getPrice()      { return price; }
    public int    getQuantity()   { return quantity; }
    public int    getImageRes()   { return imageRes; }

    public void setProductId(String id)    { this.productId = id; }
    public void setName(String name)       { this.name = name; }
    public void setPrice(int price)        { this.price = price; }
    public void setQuantity(int qty)       { this.quantity = qty; }
    public void setImageRes(int imageRes)  { this.imageRes = imageRes; }
}