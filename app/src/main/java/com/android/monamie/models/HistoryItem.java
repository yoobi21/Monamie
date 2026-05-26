package com.android.monamie.models;

public class HistoryItem {
    private String orderId;
    private String date;
    private String totalAmount;
    private String status;
    private int imageRes;

    public HistoryItem(String orderId, String date, String totalAmount, String status, int imageRes) {
        this.orderId = orderId;
        this.date = date;
        this.totalAmount = totalAmount;
        this.status = status;
        this.imageRes = imageRes;
    }

    public String getOrderId() { return orderId; }
    public String getDate() { return date; }
    public String getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public int getImageRes() { return imageRes; }
}