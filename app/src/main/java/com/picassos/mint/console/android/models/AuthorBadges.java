package com.picassos.mint.console.android.models;

public class AuthorBadges {
    private String name;
    private String label;
    private String image;

    public AuthorBadges() {
    }

    public AuthorBadges(String name, String label, String image) {
        this.name = name;
        this.label = label;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
