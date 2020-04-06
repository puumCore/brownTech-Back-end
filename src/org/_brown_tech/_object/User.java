package org._brown_tech._object;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author Mandela
 */
public class User implements Serializable {

    public static final long serialVersionUID = 72L;
    public String username;
    public String fullname;
    public boolean isAdmin;
    public boolean isActive;

    public User(String username, String fullname, boolean isAdmin, boolean isActive) {
        this.username = username;
        this.fullname = fullname;
        this.isAdmin = isAdmin;
        this.isActive = isActive;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public boolean isAdmin() {
        return isAdmin;
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
