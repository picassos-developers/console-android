package com.picassos.mint.console.android.models;

public class Purchase {
    private int id;
    private int purchase_id;
    private String product_id;
    private String product_prefix;
    private int product_price;
    private String date;

    public Purchase() {
    }

    public Purchase(int id, int purchase_id, String product_id, String product_prefix, int product_price, String date) {
        this.id = id;
        this.purchase_id = purchase_id;
        this.product_id = product_id;
        this.product_prefix = product_prefix;
        this.product_price = product_price;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPurchase_id() {
        return purchase_id;
    }

    public void setPurchase_id(int purchase_id) {
        this.purchase_id = purchase_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_prefix() {
        return product_prefix;
    }

    public void setProduct_prefix(String product_prefix) {
        this.product_prefix = product_prefix;
    }

    public int getProduct_price() {
        return product_price;
    }

    public void setProduct_price(int product_price) {
        this.product_price = product_price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
