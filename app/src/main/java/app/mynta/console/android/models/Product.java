package app.mynta.console.android.models;

public class Product {
    private int id;
    private String productId;
    private String url;
    private String thumbnail;
    private String prefix;
    private String title;
    private String description;
    private int price;
    private int discount;

    public Product(int id, String productId, String url, String thumbnail, String prefix, String title, String description, int price, int discount) {
        this.id = id;
        this.productId = productId;
        this.url = url;
        this.thumbnail = thumbnail;
        this.prefix = prefix;
        this.title = title;
        this.description = description;
        this.price = price;
        this.discount = discount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
