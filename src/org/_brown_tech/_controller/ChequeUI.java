package org._brown_tech._controller;

import animatefx.animation.FadeOutLeft;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org._brown_tech._custom.Brain;
import org._brown_tech._object._payments.Cheque;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class ChequeUI extends Brain implements Initializable {

    protected static Cheque globalCheque;
    private Cheque cheque;

    @FXML
    private Label receiptLbl;

    @FXML
    private Label chqNoLbl;

    @FXML
    private Label dueDateLbl;

    @FXML
    private Label bankLbl;

    @FXML
    private Label amtLbl;

    @FXML
    void approveCheque(@NotNull ActionEvent event) {
        if (i_am_sure_of_it("approve this cheque")) {
            if (approve_cheque(cheque.getDateTime())) {
                final JFXButton jfxButton = (JFXButton) event.getSource();
                final StackPane stackPane = (StackPane) jfxButton.getParent().getParent();
                final VBox vBox = (VBox) stackPane.getParent();
                new FadeOutLeft(stackPane).play();
                Platform.runLater(() -> vBox.getChildren().remove(stackPane));
                success_notification("Reload to see changes").show();
            } else {
                error_message("Failed!", "The cheque has not been approved").show();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.cheque = ChequeUI.globalCheque;
        receiptLbl.setText(cheque.getReceiptNumber().toString());
        chqNoLbl.setText(cheque.getChequeNumber().toString());
        dueDateLbl.setText(cheque.getMaturityDate());
        bankLbl.setText(cheque.getBank());
        amtLbl.setText("Ksh ".concat(String.format("%,.1f", cheque.getAmount())));
    }
}
