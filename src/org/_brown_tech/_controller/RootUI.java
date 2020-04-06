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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org._brown_tech.Main;
import org._brown_tech._custom.Email;
import org._brown_tech._custom.Issues;
import org._brown_tech._object.AccountObj;
import org._brown_tech._outsourced.BCrypt;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Mandela
 */

public class RootUI extends Issues implements Initializable {

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
            final String firstName = verify_user(usernameTF.getText().trim(), password, true);
            if (firstName == null) {
                error_message("Invalid credentials!", "Check your username or password").show();
            } else {
                RootUI.LOGGED_IN_USER = usernameTF.getText();
                final AccountObj accountObj = get_details_of_a_user(LOGGED_IN_USER, true);
                if (accountObj == null) {
                    warning_message("Feature failed", "I could not fetch your details to enable you to update your accountObj").show();
                } else {
                    if (phaseTwo.getOpacity() < 1) {
                        final Task<Object> task = get_then_display_workStation();
                        task.setOnSucceeded(event1 -> {
                            usernameTF.setText("");
                            siriPF.setText("");
                            plainSiriTF.setText("");
                            new FadeOut(phaseOne).play();
                            phaseTwo.toFront();
                            new FadeIn(phaseTwo).play();
                            Main.stage.setTitle("Hello ".concat(firstName));
                            RootUI.logoutThread = new Thread(logout_from_elseWhere());
                        });
                        task.exceptionProperty().addListener(((observable, oldValue, newValue) -> {
                            if (newValue != null) {
                                Exception e = (Exception) newValue;
                                e.printStackTrace();
                                programmer_error(e).show();
                                new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                                new Thread(stack_trace_printing(e.getStackTrace())).start();
                            }
                        }));
                        new Thread(task).start();
                        return;
                    }
                    usernameTF.setText("");
                    siriPF.setText("");
                    plainSiriTF.setText("");
                    new FadeOut(phaseOne).play();
                    phaseTwo.toFront();
                    new FadeIn(phaseTwo).play();
                    Main.stage.setTitle("Hello ".concat(firstName));
                    RootUI.logoutThread = new Thread(logout_from_elseWhere());
                }
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
            final String fullName = check_if_the_username_is_legit((usernameTF.getText().trim()));
            if (fullName != null) {
                final String USERNAME = usernameTF.getText().trim();
                final String GENERATED_PLAIN_PASSWORD = RandomStringUtils.randomAlphabetic(10);
                final String NEW_PASSWORD = BCrypt.hashpw(GENERATED_PLAIN_PASSWORD, BCrypt.gensalt(14));
                if (update_password(USERNAME, NEW_PASSWORD)) {
                    final AccountObj accountObj1 = get_details_of_a_user(USERNAME, false);
                    new Email(GENERATED_PLAIN_PASSWORD, accountObj1.getEmail()).send();
                    information_message(GENERATED_PLAIN_PASSWORD).show();
                    System.out.println(GENERATED_PLAIN_PASSWORD);
                    error_message("Failed!", "Your new password was not sent to your email").show();
                } else {
                    error_message("Failed!", "Your new password was not reset by the app").show();
                }

            } else {
                error_message("Woow! That's a bad username", "Try to remember your username to continue...").show();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        breath_of_life();
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
                new FadeOut(splashPane).play();
                loginPane.toFront();
                new FadeIn(loginPane).setDelay(Duration.seconds(0.7)).play();
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
            new Thread(stack_trace_printing(e.getStackTrace())).start();
        }
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    private Task<Object> loading_progress() {
        final String[] appInfo = new String[]{"Please wait as i freshen up :)",
                "Loading work station...",
                "#",
                "#",
                "#",
                "#"};
        return new Task<Object>() {
            @Override
            protected Object call() {
                int steps = 0;
                while (steps <= 100) {
                    try {
                        updateProgress(steps, 100);
                        switch (steps) {
                            case 0:
                                Platform.runLater(() -> threadUpdate.setText(appInfo[0]));
                                break;
                            case 20:
                                final Task<Object> task = get_then_display_workStation();
                                task.exceptionProperty().addListener(((observable, oldValue, newValue) -> {
                                    if (newValue != null) {
                                        Exception e = (Exception) newValue;
                                        e.printStackTrace();
                                        programmer_error(e).show();
                                        new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                                        new Thread(stack_trace_printing(e.getStackTrace())).start();
                                    }
                                }));
                                new Thread(task).start();
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
                        new Thread(stack_trace_printing(e.getStackTrace())).start();
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
                Platform.runLater(() -> Main.stage.setTitle(""));
                Platform.runLater(() -> {
                    new FadeOut(phaseTwo).play();
                    phaseOne.toFront();
                    new FadeIn(phaseOne).play();
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
