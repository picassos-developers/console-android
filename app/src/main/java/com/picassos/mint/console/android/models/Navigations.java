package com.picassos.mint.console.android.models;

public class Navigations {
    private int id;
    private String title;
    private String link;
    private String icon;
    private String behavior;
    private String premium;

    public Navigations(int id, String title, String link, String icon, String behavior, String premium) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.icon = icon;
        this.behavior = behavior;
        this.premium = premium;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }
}
