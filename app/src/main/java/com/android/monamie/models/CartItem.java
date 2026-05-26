package com.android.monamie.models;

public class CartItem {
    private String productId;
    private String name;
    private int    price;
    private int    originalPrice;
    private int    quantity;
    private int    imageRes;
    private boolean isPromo; // Added to distinguish promo items

    public CartItem() {}

    public CartItem(String productId, String name, int price, int quantity, int imageRes) {
        this(productId, name, price, price, quantity, imageRes, false);
    }

    public CartItem(String productId, String name, int price, int originalPrice, int quantity, int imageRes, boolean isPromo) {
        this.productId = productId;
        this.name      = name;
        this.price     = price;
        this.originalPrice = originalPrice;
        this.quantity  = quantity;
        this.imageRes  = imageRes;
        this.isPromo   = isPromo;
    }

    public int    getSubtotal()   { return price * quantity; }
    public int    getOriginalSubtotal() { return originalPrice * quantity; }
    public String getProductId()  { return productId; }
    public String getName()       { return name; }
    public int    getPrice()      { return price; }
    public int    getOriginalPrice() { return originalPrice; }
    public int    getQuantity()   { return quantity; }
    public int    getImageRes()   { return imageRes; }
    public boolean isPromo()       { return isPromo; }

    public void setProductId(String id)    { this.productId = id; }
    public void setName(String name)       { this.name = name; }
    public void setPrice(int price)        { this.price = price; }
    public void setOriginalPrice(int op)   { this.originalPrice = op; }
    public void setQuantity(int qty)       { this.quantity = qty; }
    public void setImageRes(int imageRes)  { this.imageRes = imageRes; }
    public void setPromo(boolean promo)    { isPromo = promo; }
}