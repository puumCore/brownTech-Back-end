package org._brown_tech._controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org._brown_tech._custom.Brain;
import org._brown_tech._model._object._actors.User;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class EmployeeUI extends Brain implements Initializable {

    protected static User globalUser;
    private User user;

    @FXML
    private Label nameLbl;

    @FXML
    void deactivate_account(ActionEvent event) {
        if (event == null) {
            return;
        }
        String question = "REACTIVATE this account";
        if (this.user.isActive()) {
            question = "DEACTIVATE this account";
        }
        if (i_am_sure_of_it(question)) {
            if (make_user_account_inactive_or_not(this.user)) {
                this.user.setActive(!this.user.isActive());
                if (this.user.isActive()) {
                    nameLbl.setText(this.user.getFullname());
                } else {
                    nameLbl.setText("*".concat(this.user.getFullname()));
                }
                if (this.user.isActive()) {
                    success_notification("User has been re-activated").show();
                } else {
                    success_notification("User has been deactivated").show();
                }
            } else {
                if (this.user.isActive()) {
                    error_message("Failed!", "The account was NOT deactivated").show();
                } else {
                    error_message("Failed!", "The account was NOT reactivated").show();
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.user = EmployeeUI.globalUser;
        if (this.user.isActive()) {
            nameLbl.setText(this.user.getFullname());
        } else {
            nameLbl.setText("*".concat(this.user.getFullname()));
        }
    }
}
