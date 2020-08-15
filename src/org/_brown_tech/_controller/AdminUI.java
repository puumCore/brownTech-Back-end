package org._brown_tech._controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org._brown_tech._model._object._actors.User;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class AdminUI implements Initializable {

    protected static User globalUser;

    @FXML
    private Label nameLbl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final User user = AdminUI.globalUser;
        nameLbl.setText(user.getFullname());
    }
}
