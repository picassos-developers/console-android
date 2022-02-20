package app.mynta.console.android.models;

public class LoadingSpinners {
    private String title;
    private String style;

    public LoadingSpinners(String title, String style) {
        this.title = title;
        this.style = style;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
