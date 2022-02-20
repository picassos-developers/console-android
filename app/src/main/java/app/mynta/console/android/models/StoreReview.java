package app.mynta.console.android.models;

public class StoreReview {
    private int id;
    private String author;
    private String product_id;
    private String review;
    private int rating;
    private String date;

    public StoreReview() {
    }

    public StoreReview(int id, String author, String product_id, String review, int rating, String date) {
        this.id = id;
        this.author = author;
        this.product_id = product_id;
        this.review = review;
        this.rating = rating;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
