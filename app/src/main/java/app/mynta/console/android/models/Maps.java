package app.mynta.console.android.models;

public class Maps {
    private String title;
    private String style;
    private String design;

    public Maps(String title, String style, String design) {
        this.title = title;
        this.style = style;
        this.design = design;
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

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }
}
