package app.mynta.console.android.models;

public class Comments {
    private String token;
    private int id;
    private String username;
    private String email;
    private String description;
    private String date;
    private int article;
    private int votes;
    private int is_edited;

    public Comments(String secret_api_key, int id, String username, String email, String description, String date, int article, int votes, int is_edited) {
        this.token = secret_api_key;
        this.id = id;
        this.username = username;
        this.email = email;
        this.description = description;
        this.date = date;
        this.article = article;
        this.votes = votes;
        this.is_edited = is_edited;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getArticle() {
        return article;
    }

    public void setArticle(int article) {
        this.article = article;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getIs_edited() {
        return is_edited;
    }

    public void setIs_edited(int is_edited) {
        this.is_edited = is_edited;
    }
}
