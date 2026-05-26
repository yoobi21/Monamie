package com.android.monamie.utils;

import com.android.monamie.models.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private final List<CartItem> items = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public void addItem(CartItem newItem) {
        for (CartItem item : items) {
            if (item.getProductId().equals(newItem.getProductId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        items.add(newItem);
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public void updateQty(String productId, int qty) {
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                if (qty <= 0) removeItem(productId);
                else item.setQuantity(qty);
                return;
            }
        }
    }

    public List<CartItem> getItems()  { return items; }
    public int            getCount()  { return items.size(); }
    public int            getTotalQuantity() {
        int total = 0;
        for (CartItem item : items) total += item.getQuantity();
        return total;
    }
    public void           clear()     { items.clear(); }

    public void removePromoItems() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            items.removeIf(CartItem::isPromo);
        } else {
            java.util.Iterator<CartItem> it = items.iterator();
            while (it.hasNext()) {
                if (it.next().isPromo()) it.remove();
            }
        }
    }

    public int getTotal() {
        int total = 0;
        for (CartItem item : items) total += item.getSubtotal();
        return total;
    }

    public int getOriginalTotal() {
        int total = 0;
        for (CartItem item : items) total += item.getOriginalSubtotal();
        return total;
    }
}