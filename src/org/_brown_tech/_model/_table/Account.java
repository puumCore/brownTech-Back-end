package org._brown_tech._model._table;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
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

}
