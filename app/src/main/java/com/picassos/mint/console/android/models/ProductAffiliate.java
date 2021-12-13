package com.picassos.mint.console.android.models;

public class ProductAffiliate {
    private int id;
    private String title;
    private int sales;
    private String url;
    private String last_update;
    private String description;
    private int price;
    private float rating;
    private int rating_count;
    private String published_at;
    private String image_preview;

    public ProductAffiliate(int id, String title, int sales, String url, String last_update, String description, int price, float rating, int rating_count, String published_at, String image_preview) {
        this.id = id;
        this.title = title;
        this.sales = sales;
        this.url = url;
        this.last_update = last_update;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.rating_count = rating_count;
        this.published_at = published_at;
        this.image_preview = image_preview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRating_count() {
        return rating_count;
    }

    public void setRating_count(int rating_count) {
        this.rating_count = rating_count;
    }

    public String getPublished_at() {
        return published_at;
    }

    public void setPublished_at(String published_at) {
        this.published_at = published_at;
    }

    public String getImage_preview() {
        return image_preview;
    }

    public void setImage_preview(String image_preview) {
        this.image_preview = image_preview;
    }
}
