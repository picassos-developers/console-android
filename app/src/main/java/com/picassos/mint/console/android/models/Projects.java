package com.picassos.mint.console.android.models;

public class Projects {
    private int id;
    private String sak;
    private String name;
    private String purchasecode;
    private String packagename;
    private String firebase_access_key;

    public Projects(int id, String sak, String name, String purchasecode, String packagename, String firebase_access_key) {
        this.id = id;
        this.sak = sak;
        this.name = name;
        this.purchasecode = purchasecode;
        this.packagename = packagename;
        this.firebase_access_key = firebase_access_key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSak() {
        return sak;
    }

    public void setSak(String sak) {
        this.sak = sak;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPurchasecode() {
        return purchasecode;
    }

    public void setPurchasecode(String purchasecode) {
        this.purchasecode = purchasecode;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getFirebase_access_key() {
        return firebase_access_key;
    }

    public void setFirebase_access_key(String firebase_access_key) {
        this.firebase_access_key = firebase_access_key;
    }
}
