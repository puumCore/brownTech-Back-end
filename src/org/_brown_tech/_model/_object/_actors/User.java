package org._brown_tech._model._object._actors;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class User implements Serializable {

    public static final long serialVersionUID = 3L;
    private String username;
    private String fullname;
    private boolean isAdmin;
    private boolean isActive;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, User.class);
    }
}
