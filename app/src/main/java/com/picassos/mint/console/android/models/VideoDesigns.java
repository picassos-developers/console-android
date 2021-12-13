package com.picassos.mint.console.android.models;

public class VideoDesigns {
    private int id;
    private String auth;
    private String title;

    public VideoDesigns(int id, String auth, String title) {
        this.id = id;
        this.auth = auth;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
