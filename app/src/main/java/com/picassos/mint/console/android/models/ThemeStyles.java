package com.picassos.mint.console.android.models;

public class ThemeStyles {
    private String title;
    private String gradient_start;
    private String gradient_end;

    public ThemeStyles(String title, String gradient_start, String gradient_end) {
        this.title = title;
        this.gradient_start = gradient_start;
        this.gradient_end = gradient_end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGradient_start() {
        return gradient_start;
    }

    public void setGradient_start(String gradient_start) {
        this.gradient_start = gradient_start;
    }

    public String getGradient_end() {
        return gradient_end;
    }

    public void setGradient_end(String gradient_end) {
        this.gradient_end = gradient_end;
    }
}
