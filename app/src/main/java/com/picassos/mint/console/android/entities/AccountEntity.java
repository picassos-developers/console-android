package com.picassos.mint.console.android.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.picassos.mint.console.android.models.Accounts;

import java.io.Serializable;

@Entity(tableName = "account")
public class AccountEntity implements Serializable {

    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "token")
    public String token;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "active")
    public Boolean active = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public static AccountEntity entity(Accounts accounts) {
        AccountEntity entity = new AccountEntity();
        entity.setId(accounts.id);
        entity.setToken(accounts.token);
        entity.setUsername(accounts.username);
        entity.setEmail(accounts.email);
        entity.setActive(accounts.active);
        return entity;
    }
}
