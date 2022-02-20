package app.mynta.console.android.models;

public class NotificationTarget {
    private String title;
    private String target;

    public NotificationTarget() {
    }

    public NotificationTarget(String title, String target) {
        this.title = title;
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
