package app.mynta.console.android.models;

public class TicketResponds {
    private int id;
    private String subject;
    private String message;
    private String token;
    private String date;

    public TicketResponds(int id, String subject, String message, String token, String date) {
        this.id = id;
        this.subject = subject;
        this.message = message;
        this.token = token;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
