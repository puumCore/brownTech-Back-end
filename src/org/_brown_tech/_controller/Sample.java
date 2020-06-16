package org._brown_tech._controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org._brown_tech.Main;
import org._brown_tech._custom.Brain;
import org._brown_tech._object._actors.Account;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class Sample extends Brain {

    public static Thread logoutThread = null;

    @FXML
    private StackPane phaseTwo;

    @FXML
    private StackPane phaseOne;

    @FXML
    private StackPane loginPane;

    @FXML
    private JFXTextField usernameTF;

    @FXML
    private JFXTextField plainSiriTF;

    @FXML
    private JFXPasswordField siriPF;

    @FXML
    private JFXButton hideShowSiriBtn;

    @FXML
    private StackPane splashPane;

    @FXML
    private JFXProgressBar loadingBar;

    @FXML
    private Label threadUpdate;

    @FXML
    void allow_user_to_login(ActionEvent event) {
        if (event != null) {
            if (usernameTF.getText().trim().isEmpty() || usernameTF.getText() == null) {
                empty_and_null_pointer_message(usernameTF.getParent()).show();
                return;
            }
            String password;
            if (hideShowSiriBtn.getText().startsWith("Show")) {
                if (siriPF.getText().trim().isEmpty() || siriPF.getText() == null) {
                    empty_and_null_pointer_message(siriPF.getParent().getParent()).show();
                    return;
                }
                password = siriPF.getText().trim();
            } else {
                if (plainSiriTF.getText().trim().isEmpty() || plainSiriTF.getText() == null) {
                    empty_and_null_pointer_message(plainSiriTF.getParent().getParent()).show();
                    return;
                }
                password = plainSiriTF.getText().trim();
            }
            final Account account = get_user_account(usernameTF.getText().trim(), password);
            if (account == null) {
                error_message("Invalid credentials!", "Check your username or password").show();
            } else {
                Brain.myAccount = account;
                Main.stage.setTitle("Brown tech 2020");
                Sample.logoutThread = new Thread(logout_from_elseWhere());

                usernameTF.setText("");
                siriPF.setText("");
                plainSiriTF.setText("");

                new FadeOut(loginPane).play();
                splashPane.toFront();
                new FadeIn(splashPane).setDelay(Duration.seconds(0.5)).play();

                breath_of_life();
            }
        }
    }

    @FXML
    void reset_user_password(ActionEvent event) {
        if (event != null) {
            if (usernameTF.getText().trim().isEmpty() || usernameTF.getText() == null) {
                empty_and_null_pointer_message(usernameTF.getParent()).text("Ensure you type your valid username to continue!").show();
                return;
            }
            Account account = get_user_account(usernameTF.getText().trim(), null);
            if (account == null) {
                error_message("Sorry i don't know that username", "Try to remember your username to continue...").show();
            } else {
                final String GENERATED_PLAIN_PASSWORD = RandomStringUtils.randomAlphabetic(10);
                account.setPassword(GENERATED_PLAIN_PASSWORD);
                account = reset_password(account);
                if (account == null) {
                    error_message("Failed!", "Your password was not updated, please retry").show();
                } else {
                    success_notification("Your account password has been updated!").show();
                }
            }
        }
    }

    @FXML
    void show_or_hide_password(ActionEvent event) {
        if (event != null) {
            final JFXButton jfxButton = (JFXButton) event.getSource();
            if (jfxButton == hideShowSiriBtn) {
                show_or_hide_any_password(jfxButton, plainSiriTF, siriPF);
            }
        }
    }

    private void breath_of_life() {
        start_loading_spinner();
    }

    private void start_loading_spinner() {
        try {
            loadingBar.setProgress(0);
            loadingBar.progressProperty().unbind();
            final Task<Object> objectTask = loading_progress();
            loadingBar.progressProperty().bind(objectTask.progressProperty());
            objectTask.setOnSucceeded(event -> {
                loadingBar.progressProperty().unbind();
                new FadeOut(phaseOne).play();
                phaseTwo.toFront();
                new FadeIn(phaseTwo).play();
            });
            objectTask.setOnFailed(event -> {
                loadingBar.progressProperty().unbind();
                error_message("Awww!", "Something went wrong while loading application.").show();
            });
            new Thread(objectTask).start();

        } catch (Exception e) {
            e.printStackTrace();
            programmer_error(e);
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
        }
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    private Task<Object> loading_progress() {
        final Task<Object> task = get_then_display_workStation();
        task.exceptionProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Exception e = (Exception) newValue;
                e.printStackTrace();
                programmer_error(e).show();
                new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                new Thread(stack_trace_printing(e)).start();
            }
        }));

        final String[] appInfo = new String[]{"Please wait as i freshen up :)",
                "Loading...",
                "Loading..",
                "Loading.",
                "Finishing...",
                "Am ready"};
        return new Task<Object>() {
            @Override
            protected Object call() {
                int steps = 0;
                while (steps <= 100) {
                    try {
                        updateProgress(steps, 100);
                        switch (steps) {
                            case 0:
                                new Thread(task).start();
                                Platform.runLater(() -> threadUpdate.setText(appInfo[0]));
                                break;
                            case 20:
                                Platform.runLater(() -> threadUpdate.setText(appInfo[1]));
                                break;
                            case 40:
                                Platform.runLater(() -> threadUpdate.setText(appInfo[2]));
                                break;
                            case 60:
                                Platform.runLater(() -> threadUpdate.setText(appInfo[3]));
                                break;
                            case 80:
                                Platform.runLater(() -> threadUpdate.setText(appInfo[4]));
                                break;
                            case 90:
                                Platform.runLater(() -> threadUpdate.setText(appInfo[5]));
                        }
                        Thread.sleep(100);
                        steps++;
                    } catch (InterruptedException e) {
                        new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                        new Thread(stack_trace_printing(e)).start();
                        break;
                    }
                }
                return null;
            }
        };
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    private Task<Object> logout_from_elseWhere() {
        return new Task<Object>() {
            @Override
            protected Object call() {
                final ObservableList<Node> nodes = phaseTwo.getChildren();
                if (!nodes.isEmpty()) {
                    for (Node node : nodes) {
                        Platform.runLater(() -> {
                            StackPane.clearConstraints(node);
                            phaseTwo.getChildren().remove(node);
                        });
                    }
                }
                Platform.runLater(() -> {
                    Main.stage.setTitle("");

                    loadingBar.setProgress(0);

                    new FadeOut(phaseTwo).play();
                    phaseOne.toFront();
                    new FadeIn(phaseOne).setDelay(Duration.seconds(0.5)).play();

                    new FadeOut(splashPane).play();
                    loginPane.toFront();
                    new FadeIn(loginPane).setDelay(Duration.seconds(0.5)).play();
                });

                return null;
            }
        };
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    private Task<Object> get_then_display_workStation() {
        return new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                final ObservableList<Node> nodes = phaseTwo.getChildren();
                if (!nodes.isEmpty()) {
                    for (Node node : nodes) {
                        Platform.runLater(() -> {
                            StackPane.clearConstraints(node);
                            phaseTwo.getChildren().remove(node);
                        });
                    }
                }
                final Node node = FXMLLoader.load(getClass().getResource("/org/_brown_tech/_fxml/deskUI.fxml"));
                Platform.runLater(() -> phaseTwo.getChildren().add(node));
                return null;
            }
        };
    }

    private void show_or_hide_any_password(@NotNull final JFXButton jfxButton, final JFXTextField jfxTextField, final JFXPasswordField jfxPasswordField) {
        if (jfxButton.getText().startsWith("Show")) {
            if (!jfxPasswordField.getText().trim().isEmpty() || jfxPasswordField.getText() != null) {
                jfxTextField.setText(jfxPasswordField.getText().trim());
            }
            new FadeOut(jfxPasswordField).play();
            jfxTextField.toFront();
            new FadeIn(jfxTextField).setDelay(Duration.seconds(0.5)).play();
            jfxButton.setText("Hide password");
        } else {
            if (!jfxTextField.getText().trim().isEmpty() || jfxTextField.getText() != null) {
                jfxPasswordField.setText(jfxTextField.getText().trim());
            }
            new FadeOut(jfxTextField).play();
            jfxPasswordField.toFront();
            new FadeIn(jfxPasswordField).setDelay(Duration.seconds(0.5)).play();
            jfxButton.setText("Show password");
        }
    }

}
