package app.mynta.console.android.models;

public class Articles {
    private int article_id;
    private String title;

    public Articles(int article_id, String title) {
        this.article_id = article_id;
        this.title = title;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
