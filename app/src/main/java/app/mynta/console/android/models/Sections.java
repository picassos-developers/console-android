package app.mynta.console.android.models;

public class Sections {
    private int section_id;
    private String section_title;
    private String section_description;

    public Sections(int section_id, String section_title, String section_description) {
        this.section_id = section_id;
        this.section_title = section_title;
        this.section_description = section_description;
    }

    public int getSection_id() {
        return section_id;
    }

    public void setSection_id(int section_id) {
        this.section_id = section_id;
    }

    public String getSection_title() {
        return section_title;
    }

    public void setSection_title(String section_title) {
        this.section_title = section_title;
    }

    public String getSection_description() {
        return section_description;
    }

    public void setSection_description(String section_description) {
        this.section_description = section_description;
    }
}
