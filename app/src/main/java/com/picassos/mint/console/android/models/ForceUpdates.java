package com.picassos.mint.console.android.models;

public class ForceUpdates {
    private int id;
    private String title;

    public ForceUpdates() {
    }

    public ForceUpdates(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
