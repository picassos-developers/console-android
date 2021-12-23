package com.picassos.mint.console.android.models;

public class Navigations {
    private int id;
    private int identifier;
    private int enabled;
    private String type;
    private String label;
    private String icon;

    public Navigations() {
    }

    public Navigations(int id, int identifier, int enabled, String type, String label, String icon) {
        this.id = id;
        this.identifier = identifier;
        this.enabled = enabled;
        this.type = type;
        this.label = label;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
