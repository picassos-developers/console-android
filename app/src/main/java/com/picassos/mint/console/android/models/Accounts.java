package com.picassos.mint.console.android.models;

import java.io.Serializable;

public class Accounts implements Serializable {
    public Long id;
    public String token;
    public String username;
    public String email;
    public Boolean active = false;
}
