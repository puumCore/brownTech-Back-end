package org._brown_tech._custom;

import animatefx.animation.Shake;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org._brown_tech.Main;
import org.controlsfx.control.Notifications;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Optional;

/**
 * @author Mandela
 */
public abstract class Watchdog {

    private final String pathToErrorFolder = Main.RESOURCE_PATH.getAbsolutePath() + "\\_watchDog\\_error\\";
    private final String pathToInfoFolder = Main.RESOURCE_PATH.getAbsolutePath() + "\\_watchDog\\_info\\";

    protected final @NotNull Alert server_error(final String description) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(Main.stage);
        alert.setTitle("SERVER ERROR");
        alert.setHeaderText("The server encountered an error");
        alert.setContentText("This dialog is a detailed explanation of the error that has occurred");
        Label label = new Label("The exception stacktrace was: ");
        TextArea textArea = new TextArea(description);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        VBox vBox = new VBox();
        vBox.getChildren().add(label);
        vBox.getChildren().add(textArea);
        alert.getDialogPane().setExpandableContent(vBox);
        return alert;
    }

    /**
     * <p>
     * <strong>
     * Results on choose_between_making_an_item_temporarily_or_permanently_unAvailable()
     * </strong>
     * </p>
     * <p>0 means Cancel</p>
     * <p>1 means Temporarily</p>
     * <p>2 means Permanently</p>
     *
     * @see Watchdog#choose_between_making_an_item_temporarily_or_permanently_unAvailable()
     */
    public final @NotNull Integer choose_between_making_an_item_temporarily_or_permanently_unAvailable() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(Main.stage);
        alert.setTitle("I require your confirmation to continue...");
        alert.setHeaderText("How would you would you like the item to be deleted?");
        alert.setContentText("Note: This can not be undone!");
        ButtonType optionAButtonType = new ButtonType("Temporarily");
        ButtonType optionBButtonType = new ButtonType("Permanently", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().setAll(optionAButtonType, optionBButtonType, cancelButtonType);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().equals(optionAButtonType)) {
            return 1;
        } else if (result.isPresent() && result.get().equals(optionBButtonType)) {
            return 2;
        } else {
            return 0;
        }
    }

    public String pretty_show_money(double amount) {
        String prettyAmountOfMoney;
        final double MIL = 1000000.0;
        final double BIL = 1000000000.0;
        if ((amount >= MIL) && (amount < BIL)) {
            prettyAmountOfMoney = String.format("%,.2f", (amount / MIL)).concat(" Mil");
            return prettyAmountOfMoney;
        }
        if ((amount >= BIL)/* && (BIL < amount)*/) {
            prettyAmountOfMoney = String.format("%,.2f", (amount / BIL)).concat(" Mil");
            return prettyAmountOfMoney;
        }
        prettyAmountOfMoney = String.format("%,.1f", amount);
        return prettyAmountOfMoney;
    }

    public String concat_year_to_the_selected_month(String selectedMonth) {
        final TextInputDialog textInputDialog = new TextInputDialog(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
        textInputDialog.initOwner(Main.stage);
        textInputDialog.setTitle("Text Input Dialog");
        textInputDialog.setHeaderText("To fetch more specific results for ".concat(selectedMonth).concat(" we require a little more info.").concat("\nRead below"));
        textInputDialog.setContentText("Please enter a specific year to proceed:");
        Optional<String> result = textInputDialog.showAndWait();
        return result.orElse(null);
    }

    public Integer get_previous_month(int monthParam) {
        switch (monthParam) {
            case 1:
                return 12;

            case 2:
                return 1;

            case 3:
                return 2;

            case 4:
                return 3;

            case 5:
                return 4;

            case 6:
                return 5;

            case 7:
                return 6;

            case 8:
                return 7;

            case 9:
                return 8;

            case 10:
                return 9;

            case 11:
                return 10;

            case 12:
                return 11;

            default:
                return monthParam;
        }
    }

    @NotNull
    public final String previous_date() {
        int day = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("dd")));
        int month = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("MM")));
        int year = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
        final YearMonth yearMonth = YearMonth.of(Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))), get_previous_month(month));
        int days_of_the_previous_month = yearMonth.lengthOfMonth();
        int the_previous_date = 0;
        if (day == 1) {
            switch (days_of_the_previous_month) {
                case 31:
                    the_previous_date = 31;
                    month = yearMonth.getMonthValue();
                    break;
                case 30:
                    the_previous_date = 30;
                    month = yearMonth.getMonthValue();
                    break;
                case 29:
                    the_previous_date = 29;
                    month = yearMonth.getMonthValue();
                    break;
                case 28:
                    the_previous_date = 28;
                    month = yearMonth.getMonthValue();
                    break;
            }
        } else {
            the_previous_date = --day;
        }
        return LocalDate.of(year, month, the_previous_date).format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    }

    @NotNull
    public final Boolean i_am_sure_of_it(String nameOfAction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(Main.stage);
        alert.setTitle("The app requires your confirmation to continue...");
        alert.setHeaderText("Are you sure you want to ".concat(nameOfAction).concat(" ?"));
        alert.setContentText("This can not be undone!");
        ButtonType yesButtonType = new ButtonType("YES");
        ButtonType noButtonType = new ButtonType("NO", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().setAll(yesButtonType, noButtonType);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().equals(yesButtonType);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public final Task<Object> write_log(String string, int q) {
        return new Task<Object>() {
            @Override
            public Object call() {
                if (q == 1) {
                    record_error(string);
                }
                if (q == 2) {
                    activityLog(string);
                }
                return false;
            }
        };
    }

    public final void activityLog(String message) {
        BufferedWriter bw = null;
        try {
            File log = new File(pathToInfoFolder.concat("\\AccountObj for {" + gate_date_for_file_name() + "}.txt"));
            if (!log.exists()) {
                if (log.createNewFile()) {
                    if (log.canWrite() & log.canRead()) {
                        FileWriter fw = new FileWriter(log, true);
                        bw = new BufferedWriter(fw);
                        bw.write("\nThis is a newly created file [ " + time_stamp() + " ].");
                    }
                }
            }
            if (log.canWrite() & log.canRead()) {
                FileWriter fw = new FileWriter(log, true);
                bw = new BufferedWriter(fw);
                bw.write(message);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            programmer_error(ex).show();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                programmer_error(ex).show();
            }
        }
    }

    public final void record_error(String rec_text) {
        BufferedWriter bw = null;
        try {
            File log = new File(pathToErrorFolder.concat(gate_date_for_file_name().concat(" Summarised Error log.txt")));
            if (!log.exists()) {
                if (log.createNewFile()) {
                    if (log.canWrite() & log.canRead()) {
                        FileWriter fw = new FileWriter(log, true);
                        bw = new BufferedWriter(fw);
                        bw.write("\nThis is a newly created file [ " + time_stamp() + " ].");
                    }
                }
            }
            if (log.canWrite() & log.canRead()) {
                FileWriter fw = new FileWriter(log, true);
                bw = new BufferedWriter(fw);
                bw.write("\n" + rec_text);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            programmer_error(ex).show();

        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                programmer_error(ex).show();
            }
        }
    }

    @NotNull
    public final String time_stamp() {
        return date_today()
                + " at " +
                new SimpleDateFormat("HH:mm:ss:SSS").format(Calendar.getInstance().getTime());
    }

    public String date_today() {
        return new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
    }

    @Contract(pure = true)
    public final @NotNull Runnable write_stack_trace(Exception exception) {
        return () -> {
            BufferedWriter bw = null;
            try {
                File log = new File(pathToErrorFolder.concat(gate_date_for_file_name().concat(" stackTrace_log.txt")));
                if (!log.exists()) {
                    FileWriter fw = new FileWriter(log);
                    fw.write("\nThis is a newly created file [ " + time_stamp() + " ].");
                }
                if (log.canWrite() & log.canRead()) {
                    FileWriter fw = new FileWriter(log, true);
                    bw = new BufferedWriter(fw);
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    exception.printStackTrace(printWriter);
                    String exceptionText = stringWriter.toString();
                    bw.write("\n ##################################################################################################"
                            + " \n " + time_stamp()
                            + "\n " + exceptionText
                            + "\n\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                programmer_error(ex).show();
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    programmer_error(ex).show();
                }
            }
        };
    }

    private @NotNull String gate_date_for_file_name() {
        return new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime()).replaceAll("-", " ");
    }

    @NotNull
    public final Alert programmer_error(@NotNull Object object) {
        Exception exception = (Exception) object;
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(Main.stage);
        alert.setTitle("WATCH DOG");
        alert.setHeaderText("ERROR TYPE : " + exception.getClass());
        alert.setContentText("This dialog is a detailed explanation of the error that has occurred");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        String exceptionText = stringWriter.toString();
        Label label = new Label("The exception stacktrace was: ");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        VBox vBox = new VBox();
        vBox.getChildren().add(label);
        vBox.getChildren().add(textArea);
        alert.getDialogPane().setExpandableContent(vBox);
        return alert;
    }

    public final void information_message(String message) {
        try {
            SystemTray systemTray = SystemTray.getSystemTray();
            java.awt.image.BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/org/_brown_tech/_images/_pictures/icon.png"));
            TrayIcon trayIcon = new TrayIcon(bufferedImage);
            trayIcon.setImageAutoSize(true);
            systemTray.add(trayIcon);
            trayIcon.displayMessage("Information", message, TrayIcon.MessageType.NONE);
        } catch (IOException | AWTException exception) {
            exception.printStackTrace();
            programmer_error(exception).show();
        }
    }

    protected final Notifications warning_message(String title, String text) {
        Image image = new Image("/org/_brown_tech/_images/_icons/icons8_Error_48px.png");
        return Notifications.create()
                .title(title)
                .text(text)
                .graphic(new ImageView(image))
                .hideAfter(Duration.seconds(8))
                .position(Pos.TOP_LEFT);
    }

    protected final Notifications empty_and_null_pointer_message(Node node) {
        Image image = new Image("/org/_brown_tech/_images/_icons/icons8_Error_48px.png");
        return Notifications.create()
                .title("Something is Missing")
                .text("Click Here to trace this Error.")
                .graphic(new ImageView(image))
                .hideAfter(Duration.seconds(8))
                .position(Pos.TOP_LEFT)
                .onAction(event -> {
                    new Shake(node).play();
                    node.requestFocus();
                });
    }

    @NotNull
    public final Alert error_message_alert(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(Main.stage);
        alert.setTitle("Brown tech encountered an Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        return alert;
    }

    public final Notifications success_notification(String about) {
        return Notifications.create()
                .title("Success")
                .text(about)
                .position(Pos.BASELINE_LEFT)
                .hideAfter(Duration.seconds(5))
                .graphic(new ImageView(new Image("/org/_brown_tech/_images/_icons/icons8_Ok_48px.png")));
    }

    public final Notifications error_message(String title, String text) {
        Image image = new Image("/org/_brown_tech/_images/_icons/icons8_Close_Window_48px.png");
        return Notifications.create()
                .title(title)
                .text(text)
                .graphic(new ImageView(image))
                .hideAfter(Duration.seconds(8))
                .position(Pos.TOP_RIGHT);
    }

}
