package com.android.monamie.utils;

import com.android.monamie.R;
import com.android.monamie.models.Product;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductManager {
    private static ProductManager instance;
    private final List<Product> products = new ArrayList<>();

    private ProductManager() {
        // Initialize with default products and stock values
        products.addAll(Arrays.asList(
            new Product(1, "Red Velvet Cookie", "Kukis", 8000, R.drawable.img_cookie_velvet, 50),
            new Product(2, "Matcha Cookie",      "Kukis", 9000, R.drawable.img_cookie_matcha, 30),
            new Product(3, "Choco Chip Cookie",  "Kukis", 7000, R.drawable.img_cookie_choco, 100),
            new Product(4, "Butter Cookie",      "Kukis", 6000, R.drawable.img_cookie_butter, 80),
            new Product(5, "Americano",          "Kopi",    15000, R.drawable.coffee_americano, 40),
            new Product(6, "Caffè Latte",        "Kopi",    12000, R.drawable.coffee_latte, 45),
            new Product(7, "Matcha",             "Kopi",    16000, R.drawable.coffee_matcha, 25)
        ));
    }

    public static synchronized ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public void decreaseStock(String name, int quantity) {
        for (Product p : products) {
            // Check by name (case-insensitive) as it's the most reliable link from CartItem
            if (p.getName().equalsIgnoreCase(name)) {
                int currentStock = p.getStock();
                p.setStock(Math.max(0, currentStock - quantity));
                return;
            }
        }
    }

    public int getStock(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p.getStock();
            }
        }
        return 0;
    }

    public int getImageByName(String name) {
        for (Product p : products) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p.getImageRes();
            }
        }
        return R.drawable.img_cookie_velvet; // Default fallback
    }
}
