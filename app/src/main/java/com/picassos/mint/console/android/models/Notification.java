package com.picassos.mint.console.android.models;

import java.io.Serializable;

public class Notification implements Serializable {

    public Long id;
    public String title;
    public String content;
    public Long obj_id;

    public Boolean read = false;
    public Long created_at;
}
