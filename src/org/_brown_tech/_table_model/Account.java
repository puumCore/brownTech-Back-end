package org._brown_tech._table_model;

import com.google.gson.Gson;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Mandela
 */
public class Account extends RecursiveTreeObject<Account> {

    public StringProperty username;
    public StringProperty firstname;
    public StringProperty surname;
    public StringProperty email;

    public Account(String username, String firstname, String surname, String email) {
        this.username = new SimpleStringProperty(username);
        this.firstname = new SimpleStringProperty(firstname);
        this.surname = new SimpleStringProperty(surname);
        this.email = new SimpleStringProperty(email);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getFirstname() {
        return firstname.get();
    }

    public StringProperty firstnameProperty() {
        return firstname;
    }

    public String getSurname() {
        return surname.get();
    }

    public StringProperty surnameProperty() {
        return surname;
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this, Account.class);
    }
}
