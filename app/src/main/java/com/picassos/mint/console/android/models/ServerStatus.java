package com.picassos.mint.console.android.models;

public class ServerStatus {
    private int id;
    private String status;
    private String date;

    public ServerStatus() {
    }

    public ServerStatus(int id, String status, String date) {
        this.id = id;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
