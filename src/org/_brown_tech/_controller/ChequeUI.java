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
import org._brown_tech._custom.Issues;
import org._brown_tech._object.ChequeObj;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Mandela
 */
public class ChequeUI extends Issues implements Initializable {

    protected static ChequeObj globalChequeObj;
    private ChequeObj chequeObj;

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
    void approveCheque(ActionEvent event) {
        if (event == null) {
            return;
        }
        if (i_am_sure_of_it("approve this cheque")) {
            if (approve_cheque(chequeObj.getDateTime())) {
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
        this.chequeObj = ChequeUI.globalChequeObj;
        receiptLbl.setText(chequeObj.getReceiptNumber());
        chqNoLbl.setText(chequeObj.getChequeNo());
        dueDateLbl.setText(chequeObj.getMaturityDate());
        bankLbl.setText(chequeObj.getBankName());
        amtLbl.setText(chequeObj.getAmount());
    }
}
