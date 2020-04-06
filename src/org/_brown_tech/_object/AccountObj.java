package org._brown_tech._object;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author Mandela
 */
public class AccountObj implements Serializable {

    public static final long serialVersionUID = 82L;

    public String username, firstname, surname, siri, email;
    public Boolean isAdmin;

    public AccountObj() {
    }

    public AccountObj(String username, String firstname, String surname, String siri, String email, Boolean isAdmin) {
        this.username = username;
        this.firstname = firstname;
        this.surname = surname;
        this.siri = siri;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSiri() {
        return siri;
    }

    public void setSiri(String siri) {
        this.siri = siri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, AccountObj.class);
    }
}
