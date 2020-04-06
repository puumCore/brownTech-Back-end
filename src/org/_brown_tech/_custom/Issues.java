package org._brown_tech._custom;

import animatefx.animation.Shake;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import org._brown_tech.Main;
import org.controlsfx.control.Notifications;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
public class Issues extends Brain {

    private final String pathToErrorFolder = Main.RESOURCE_PATH.getAbsolutePath() + "\\_watchDog\\_error";
    private final String pathToInfoFolder = Main.RESOURCE_PATH.getAbsolutePath() + "\\_watchDog\\_info";

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
        textInputDialog.setHeaderText("To fetch more specific results for ".concat(selectedMonth).concat(" we require a little more info.") .concat("\nRead below"));
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
        alert.setTitle("I require your confirmation to continue...");
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
    @Contract(value = "_ -> new", pure = true)
    public final Task<Object> stack_trace_printing(StackTraceElement[] stackTraceElements) {
        return new Task<Object>() {
            @Override
            public Object call() {
                write_stack_trace(stackTraceElements);
                return false;
            }
        };
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
            File log = new File(pathToInfoFolder.concat("\\AccountObj for {" + new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime()).replaceAll("-", " ") + "}.txt"));
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
                new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + ex, 1)).start();
                new Thread(stack_trace_printing(ex.getStackTrace())).start();
                ex.printStackTrace();
                programmer_error(ex).show();
            }
        }
    }

    public final void record_error(String rec_text) {
        BufferedWriter bw = null;
        try {
            File log = new File(pathToErrorFolder.concat(new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime()).replaceAll("-", " ") + " Summarised Error log.txt"));
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
                new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + ex, 1)).start();
                new Thread(stack_trace_printing(ex.getStackTrace())).start();
                ex.printStackTrace();
                programmer_error(ex).show();
            }
        }
    }

    public final void write_stack_trace(StackTraceElement[] message) {
        BufferedWriter bw = null;
        try {
            File log = new File(pathToErrorFolder.concat(new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime()).replaceAll("-", " ") + " Detailed Stack Trace Log.txt"));
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
                for (StackTraceElement stackTraceElement : message) {
                    bw.write("\n " + time_stamp() + "\n " + stackTraceElement.toString() + "\n ");
                }
            }
        } catch (IOException ex) {
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + ex, 1)).start();
            new Thread(stack_trace_printing(ex.getStackTrace())).start();
            ex.printStackTrace();
            programmer_error(ex).show();

        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception ex) {
                new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + ex, 1)).start();
                new Thread(stack_trace_printing(ex.getStackTrace())).start();
                ex.printStackTrace();
                programmer_error(ex).show();
            }
        }
    }

    @NotNull
    public final String time_stamp() {
        return  date_today() + " at " + new SimpleDateFormat("HH:mm:ss:SSS").format(Calendar.getInstance().getTime());
    }

    @NotNull
    public final String date_today() {
        return new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
    }

    public final Notifications information_message(String message) {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.INFORMATION_OUTLINE);
        icon.setGlyphSize(30);
        icon.setGlyphStyle("-fx-fill : rgb(0, 80, 143);");
        return Notifications.create()
                .title("Info")
                .text(message)
                .graphic(icon)
                .hideAfter(Duration.seconds(8))
                .position(Pos.BASELINE_RIGHT);
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
        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(label, 0, 0);
        gridPane.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(gridPane);
        return alert;
    }

}
