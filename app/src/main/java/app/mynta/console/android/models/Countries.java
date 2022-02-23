package app.mynta.console.android.models;

public class Countries {
    private String title;
    private String code;
    private String flag;

    public Countries(String title, String code, String flag) {
        this.title = title;
        this.code = code;
        this.flag = flag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
