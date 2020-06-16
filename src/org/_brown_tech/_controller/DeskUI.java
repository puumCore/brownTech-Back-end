package org._brown_tech._controller;

import animatefx.animation.*;
import com.google.gson.JsonObject;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org._brown_tech.Main;
import org._brown_tech._custom.Brain;
import org._brown_tech._custom.Watchdog;
import org._brown_tech._object.Product;
import org._brown_tech._object._actors.Account;
import org._brown_tech._object._actors.User;
import org._brown_tech._object._payments.Cheque;
import org._brown_tech._outsourced.BCrypt;
import org._brown_tech._outsourced.PasswordDialog;
import org._brown_tech._table_model.AccountModel;
import org._brown_tech._table_model.PurchaseModel;
import org._brown_tech._table_model.ReceiptModel;
import org._brown_tech._table_model.StockModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public class DeskUI extends Brain implements Initializable {

    private final HashMap<Integer, StackPane> desk_stackPanes = new HashMap<>();
    private final HashMap<Integer, AnchorPane> sub_menu_anchorPanes = new HashMap<>();

    private final HashMap<Integer, StackPane> stock_leaflets = new HashMap<>();
    private final HashMap<Integer, StackPane> purchases_leaflets = new HashMap<>();
    private final HashMap<Integer, StackPane> payments_leaflets = new HashMap<>();
    private final HashMap<Integer, StackPane> accounts_leaflets = new HashMap<>();

    private final HashMap<Integer, AnchorPane> stock_underline = new HashMap<>();
    private final HashMap<Integer, AnchorPane> purchases_underline = new HashMap<>();
    private final HashMap<Integer, AnchorPane> payments_underline = new HashMap<>();
    private final HashMap<Integer, AnchorPane> accounts_underline = new HashMap<>();

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final JFXAutoCompletePopup<String> stockSuggestionsPopup = new JFXAutoCompletePopup<>();
    private final JFXAutoCompletePopup<String> badStockSerialSuggestionsPopup = new JFXAutoCompletePopup<>();
    private final JFXAutoCompletePopup<String> searchStockSerialForUpdateSuggestionsPopup = new JFXAutoCompletePopup<>();
    private final JFXAutoCompletePopup<String> searchStockSerialForUpdateSuggestionsPopup1 = new JFXAutoCompletePopup<>();
    private final JFXAutoCompletePopup<String> searchStockSerialForDeleteSuggestionsPopup = new JFXAutoCompletePopup<>();
    private final JFXAutoCompletePopup<String> receiptNumberSuggestionsPopup = new JFXAutoCompletePopup<>();
    private final JFXAutoCompletePopup<String> staffUsernameSuggestionsPopup = new JFXAutoCompletePopup<>();
    private final JFXAutoCompletePopup<String> staffUsernameSuggestionsPopup1 = new JFXAutoCompletePopup<>();
    private final ObservableList<String> stockSearchSuggestions = FXCollections.observableArrayList();
    private final ObservableList<String> stockSerials = FXCollections.observableArrayList();
    private final ObservableList<String> listOfActiveStaffUsername = FXCollections.observableArrayList();
    private final ObservableList<String> listOfIn_ActiveStaffUsername = FXCollections.observableArrayList();
    private final Product productToAdd = new Product();
    private final Product productToUpdate = new Product();
    private Product myProduct = null;
    private String PATH_TO_IMAGE_OF_NEW_PRODUCT = INPUT_STREAM_TO_NO_IMAGE;
    private String PATH_TO_IMAGE_OF_UPDATED_PRODUCT = INPUT_STREAM_TO_NO_IMAGE;
    private final UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("-?([1-9][0-9]*)?")) {
            return change;
        } else if ("-".equals(change.getText())) {
            if (change.getControlText().startsWith("-")) {
                change.setText("");
                change.setRange(0, 1);
                change.setCaretPosition(change.getCaretPosition() - 2);
                change.setAnchor(change.getAnchor() - 2);
            } else {
                change.setRange(0, 0);
            }
            return change;
        }
        return null;
    };
    private final StringConverter<Integer> converter = new IntegerStringConverter() {
        @Nullable
        @Override
        public Integer fromString(@NotNull String string) {
            if (string.isEmpty()) {
                return null;
            }
            return super.fromString(string);
        }
    };
    private Product productToDelete = null;

    @FXML
    private StackPane topBarPane;

    @FXML
    private AnchorPane accountsSubMenuItemsPane;

    @FXML
    private AnchorPane accountsViewLine;

    @FXML
    private AnchorPane addUserLine;

    @FXML
    private AnchorPane editUserLine;

    @FXML
    private AnchorPane removeUserLine;

    @FXML
    private AnchorPane paymentsSubMenuItemsPane;

    @FXML
    private AnchorPane paySummaryLine;

    @FXML
    private AnchorPane chequeLine;

    @FXML
    private AnchorPane purchasesSubMenuItemsPane;

    @FXML
    private AnchorPane summaryLine;

    @FXML
    private AnchorPane receiptLine;

    @FXML
    private AnchorPane stockSubMenuItemsPane;

    @FXML
    private AnchorPane viewLine;

    @FXML
    private AnchorPane addLine;

    @FXML
    private AnchorPane updateLine;

    @FXML
    private AnchorPane deleteLine;

    @FXML
    private StackPane leftSideMenuPane;

    @FXML
    private StackPane deskPane;

    @FXML
    private StackPane accountsPane;

    @FXML
    private StackPane accountsRemovePane;

    @FXML
    private VBox accountsBox;

    @FXML
    private JFXTextField oldNewFirstnameTF;

    @FXML
    private JFXTextField oldNewSurnameTF;

    @FXML
    private JFXTextField oldNewEmailTF;

    @FXML
    private JFXTextField oldNewUsernameTF;

    @FXML
    private JFXTextField actualPwd;

    @FXML
    private JFXPasswordField passwordTF;

    @FXML
    private MaterialDesignIconView newPswdEye;

    @FXML
    private JFXTextField actualConfrimPwd;

    @FXML
    private JFXPasswordField confrimPasswordTF;

    @FXML
    private MaterialDesignIconView newConfirmPswdEye;

    @FXML
    private StackPane accountsEditPane;

    @FXML
    private JFXTextField newFirstNameTF;

    @FXML
    private JFXTextField newSurnameTF;

    @FXML
    private JFXTextField newEmailTF;

    @FXML
    private JFXTextField newUsernameTF;

    @FXML
    private StackPane accountsViewPane;

    @FXML
    private JFXTextField accountParamTF;

    @FXML
    private StackPane accountsAddPane;

    @FXML
    private JFXTreeTableView<AccountModel> accountsTable;

    @FXML
    private TreeTableColumn<AccountModel, String> accUsernameCol;

    @FXML
    private TreeTableColumn<AccountModel, String> accFirstnameCol;

    @FXML
    private TreeTableColumn<AccountModel, String> accSurnameCol;

    @FXML
    private TreeTableColumn<AccountModel, String> accEmailCol;

    @FXML
    private StackPane statisticsPane;

    @FXML
    private AreaChart<String, Double> areaChart;

    @FXML
    private Label janProfitLbl;

    @FXML
    private Label aprProfitLbl;

    @FXML
    private Label julProfitLbl;

    @FXML
    private Label octProfitLbl;

    @FXML
    private Label febProfitLbl;

    @FXML
    private Label mayProfitLbl;

    @FXML
    private Label augProfitLbl;

    @FXML
    private Label novProfitLbl;

    @FXML
    private Label marProfitLbl;

    @FXML
    private Label junProfitLbl;

    @FXML
    private Label sepProfitLbl;

    @FXML
    private Label decProfitLbl;

    @FXML
    private Label yearProfitLbl;

    @FXML
    private StackPane paymentsPane;

    @FXML
    private StackPane paymentsChequePane;

    @FXML
    private PieChart chequePieChart;

    @FXML
    private Label chequeCountLbl;

    @FXML
    private Label aprvdChqLbl;

    @FXML
    private Label pndChqLbl;

    @FXML
    private VBox chequeBox;

    @FXML
    private StackPane paymentsSummaryPane;

    @FXML
    private PieChart paymentsPieChart;

    @FXML
    private Label sum_of_returnsLbl;

    @FXML
    private Label cashReturnsLbl;

    @FXML
    private Label mpesaReturnsLbl;

    @FXML
    private Label chequeReturnsLbl;

    @FXML
    private StackPane purchasesPane;

    @FXML
    private StackPane purchaseReceiptsPane;

    @FXML
    private JFXTextField rcptNoParamTF;

    @FXML
    private JFXTreeTableView<ReceiptModel> receiptsTable;

    @FXML
    private TreeTableColumn<ReceiptModel, String> receiptCol;

    @FXML
    private TreeTableColumn<ReceiptModel, String> rcptDateCol;

    @FXML
    private TreeTableColumn<ReceiptModel, String> codeCol;

    @FXML
    private TreeTableColumn<ReceiptModel, String> qtyCol;

    @FXML
    private TreeTableColumn<ReceiptModel, String> sellingPriceCol;

    @FXML
    private TreeTableColumn<ReceiptModel, String> buyPricePriceCol;

    @FXML
    private TreeTableColumn<ReceiptModel, String> typeOfStockCol;

    @FXML
    private StackPane purchaseSummaryPane;

    @FXML
    private JFXTreeTableView<PurchaseModel> salesTable;

    @FXML
    private TreeTableColumn<PurchaseModel, String> dateCol;

    @FXML
    private TreeTableColumn<PurchaseModel, String> saleRcptCol;

    @FXML
    private TreeTableColumn<PurchaseModel, String> saleAmtCol;

    @FXML
    private TreeTableColumn<PurchaseModel, String> paidAsCol;

    @FXML
    private TreeTableColumn<PurchaseModel, String> soldByCol;

    @FXML
    private JFXRadioButton allRb;

    @FXML
    private ToggleGroup sale_period;

    @FXML
    private JFXRadioButton todayRb;

    @FXML
    private JFXRadioButton yesterdayRb;

    @FXML
    private JFXDatePicker dateDp;

    @FXML
    private JFXComboBox<String> monthCbx;

    @FXML
    private StackPane stockPane;

    @FXML
    private StackPane stockDeletePane;

    @FXML
    private VBox delResultsPane;

    @FXML
    private ImageView delProdImageView;

    @FXML
    private Label delProdNameLbl;

    @FXML
    private ImageView delRatingsImageView;

    @FXML
    private Label delDescriptionLbl;

    @FXML
    private Label delStockQuantityLbl;

    @FXML
    private Label delBuyingPriceLbl;

    @FXML
    private Label delMarkedPriceLbl;

    @FXML
    private Label delSearchResultLbl;

    @FXML
    private JFXTextField prodCodeDelParTF;

    @FXML
    private StackPane stockUpdatePane;

    @FXML
    private JFXTextField prodCodeUpParTF;

    @FXML
    private AnchorPane updateDetailsPane;

    @FXML
    private JFXComboBox<Integer> newProdStarsCbx;

    @FXML
    private JFXTextField newProdQtyTF;

    @FXML
    private ImageView prodImageView1;

    @FXML
    private JFXTextField newBuyingPriceTF;

    @FXML
    private JFXTextField newMarkedPriceTF;

    @FXML
    private JFXTextArea newDescriptionTA;

    @FXML
    private JFXTextField newProdSerialTF;

    @FXML
    private JFXTextField newProdNameTF;

    @FXML
    private StackPane oldBioPane;

    @FXML
    private VBox updateResultsPane;

    @FXML
    private AnchorPane resultPane;

    @FXML
    private ImageView oldProdImageView;

    @FXML
    private Label oldProdNameLbl;

    @FXML
    private Label oldDescriptionLbl;

    @FXML
    private ImageView oldRatingsImageView;

    @FXML
    private Label oldStockQuantityLbl;

    @FXML
    private Label oldBuyingPriceLbl;

    @FXML
    private Label oldMarkedPriceLbl;

    @FXML
    private Label updateSearchResultLbl;

    @FXML
    private StackPane stockaddPane;

    @FXML
    private JFXTextField prodSerialTF;

    @FXML
    private JFXTextField prodQtyTF;

    @FXML
    private JFXTextField prodNameTF;

    @FXML
    private JFXTextField prodDescriptionTF;

    @FXML
    private JFXTextField prodMarkedPriceTF;

    @FXML
    private JFXTextField prodBuyingPriceTF;

    @FXML
    private JFXComboBox<Integer> prodRatingsCbx;

    @FXML
    private ImageView prodView;

    @FXML
    private StackPane stockViewPane;

    @FXML
    private JFXTextField stockParameterTF;

    @FXML
    private JFXTreeTableView<StockModel> stockTable;

    @FXML
    private TreeTableColumn<StockModel, String> prodCodeCol;

    @FXML
    private TreeTableColumn<StockModel, String> prodNameCol;

    @FXML
    private TreeTableColumn<StockModel, String> prodDescriptionCol;

    @FXML
    private TreeTableColumn<StockModel, String> prodCountCol;

    @FXML
    private TreeTableColumn<StockModel, ImageView> prodRatingsCol;

    @FXML
    private TreeTableColumn<StockModel, String> prodBuyPriceCol;

    @FXML
    private TreeTableColumn<StockModel, String> prodMarkedPriceCol;

    @FXML
    private TreeTableColumn<StockModel, String> prodStatusCol;

    @FXML
    private StackPane dashBoardPane;

    @FXML
    private PieChart productVersusServicesPie;

    @FXML
    private Label returnsSumLbl;

    @FXML
    private JFXComboBox<String> customYearCbx;

    @FXML
    private Label userFullnameTF;

    @FXML
    void add_user(ActionEvent event) {
        if (event != null) {
            if (newFirstNameTF.getText().trim().isEmpty() || newFirstNameTF.getText().isEmpty() || newFirstNameTF.getText() == null) {
                empty_and_null_pointer_message(newFirstNameTF).show();
                return;
            }
            if (newSurnameTF.getText().trim().isEmpty() || newSurnameTF.getText().isEmpty() || newSurnameTF.getText() == null) {
                newSurnameTF.setText("");
            }
            if (newEmailTF.getText().trim().isEmpty() || newEmailTF.getText().isEmpty() || newEmailTF.getText() == null) {
                empty_and_null_pointer_message(newEmailTF).show();
                return;
            }
            if (!email_is_in_correct_format(newEmailTF.getText().trim())) {
                error_message("Bad Email address", "The email seems not to be in its correct format").show();
                return;
            }
            if (newUsernameTF.getText().trim().isEmpty() || newUsernameTF.getText().isEmpty() || newUsernameTF.getText() == null) {
                empty_and_null_pointer_message(newUsernameTF).show();
                return;
            }
            final ObservableList<String> listOfAllUsernames = FXCollections.observableArrayList();
            listOfAllUsernames.addAll(listOfActiveStaffUsername);
            listOfAllUsernames.addAll(listOfIn_ActiveStaffUsername);
            if (listOfAllUsernames.contains(newUsernameTF.getText().trim()) || myAccount.getUsername().equals(newUsernameTF.getText().trim())) {
                warning_message("Taken!", "The username you have selected is already taken, enter a new one").show();
                return;
            }
            final String passwordForNewAccount = BCrypt.hashpw("1234", BCrypt.gensalt(14));
            final Account newUserAccount = new Account();
            newUserAccount.setFname(newFirstNameTF.getText().trim());
            newUserAccount.setSurname(newSurnameTF.getText().trim());
            newUserAccount.setEmail(newEmailTF.getText().trim());
            newUserAccount.setUsername(newUsernameTF.getText().trim());
            newUserAccount.setAdmin(false);
            newUserAccount.setPassword(passwordForNewAccount);
            if (create_new_account_for_staff(newUserAccount)) {
                set_up_accounts();
                newFirstNameTF.setText("");
                newSurnameTF.setText("");
                newEmailTF.setText("");
                newUsernameTF.setText("");
                success_notification("AccountModel has been created").show();
            } else {
                error_message("Failed!", "AccountModel account was not created").show();
            }
        }
    }

    @FXML
    void check_if_the_serial_exists(@NotNull ActionEvent event) {
        final JFXTextField jfxTextField = (JFXTextField) event.getSource();
        if (jfxTextField.getText().trim().isEmpty() || jfxTextField.getText() == null) {
            empty_and_null_pointer_message(jfxTextField).show();
            return;
        }
        if (stockSerials.contains(jfxTextField.getText().trim())) {
            error_message("Serial number is known!", "The serial already exists, enter a new one").show();
        }
    }

    @FXML
    void choose_image_from_local_drive(ActionEvent event) {
        if (event != null) {
            PATH_TO_IMAGE_OF_NEW_PRODUCT = look_for_image(prodView);
        }
    }

    @FXML
    void delete_stock_item(ActionEvent event) {
        if (event != null) {
            if (productToDelete == null) {
                warning_message("Failed!", "Please search an item above to continue").show();
                prodCodeDelParTF.requestFocus();
                return;
            }
            final int selectedChoice = choose_between_making_an_item_temporarily_or_permanently_unAvailable();
            if (selectedChoice != 0) {
                String promptInfo, successInfo, errorInfo;
                boolean makePermanent;
                if (selectedChoice == 1) {
                    promptInfo = "temporarily delete this item";
                    successInfo = "Item has been temporarily made unavailable";
                    errorInfo = "I was not able to temporarily delete this item";
                    makePermanent = false;
                } else {
                    promptInfo = "permanently delete this item";
                    successInfo = "Item has been permanently made unavailable";
                    errorInfo = "I was not able to permanently delete this item";
                    makePermanent = true;
                }
                if (i_am_sure_of_it(promptInfo)) {
                    if (delete_product(productToDelete, makePermanent)) {
                        prodCodeDelParTF.setText("");
                        setup_stock_pane();
                        display_current_details_of_an_item(null);
                        success_notification(successInfo);
                    } else {
                        error_message("Failed!", errorInfo);
                    }
                }
            }
        }
    }

    @FXML
    void edit_administrator_s_account(ActionEvent event) {
        if (event == null) {
            return;
        }
        final Account updatedAdminAccount = new Account();
        if (!oldNewFirstnameTF.getText().trim().isEmpty() || !oldNewFirstnameTF.getText().isEmpty() || oldNewFirstnameTF.getText() != null) {
            if (!Brain.myAccount.getFname().equals(oldNewFirstnameTF.getText().trim())) {
                updatedAdminAccount.setFname(oldNewFirstnameTF.getText().trim());
            }
        }
        if (!oldNewSurnameTF.getText().trim().isEmpty() || !oldNewSurnameTF.getText().isEmpty() || oldNewSurnameTF.getText() != null) {
            if (!Brain.myAccount.getSurname().equals(oldNewSurnameTF.getText().trim())) {
                updatedAdminAccount.setSurname(oldNewSurnameTF.getText().trim());
            }
        }
        if (!oldNewEmailTF.getText().trim().isEmpty() || !oldNewEmailTF.getText().isEmpty() || oldNewEmailTF.getText() != null) {
            if (!Brain.myAccount.getEmail().equals(oldNewEmailTF.getText().trim())) {
                if (!email_is_in_correct_format(newEmailTF.getText().trim())) {
                    error_message("Bad Email address", "The email seems not to be in its correct format").show();
                    return;
                }
                updatedAdminAccount.setEmail(oldNewEmailTF.getText().trim());
            }
        }
        if (!oldNewUsernameTF.getText().trim().isEmpty() || !oldNewUsernameTF.getText().isEmpty() || oldNewUsernameTF.getText() != null) {
            if (!Brain.myAccount.getUsername().equals(oldNewUsernameTF.getText().trim())) {
                updatedAdminAccount.setUsername(oldNewUsernameTF.getText().trim());
            }
        }
        String newPassword_typeA = "", newPassword_typeB = "";
        final MaterialDesignIconView passwordPrivacy = newPswdEye;
        if (passwordPrivacy.getGlyphName().equalsIgnoreCase("EYE")) {
            if (!(passwordTF.getText().trim().isEmpty() || passwordTF.getText().isEmpty() || passwordTF.getText() == null)) {
                newPassword_typeA = passwordTF.getText().trim();
            }
        } else if (passwordPrivacy.getGlyphName().equalsIgnoreCase("EYE_OFF")) {
            if (!(actualPwd.getText().trim().isEmpty() || actualPwd.getText().isEmpty() || actualPwd.getText() == null)) {
                newPassword_typeA = actualPwd.getText().trim();
            }
        }
        final MaterialDesignIconView passwordPrivacy2 = newConfirmPswdEye;
        if (passwordPrivacy2.getGlyphName().equalsIgnoreCase("EYE")) {
            if (!(confrimPasswordTF.getText().trim().isEmpty() || confrimPasswordTF.getText().isEmpty() || confrimPasswordTF.getText() == null)) {
                newPassword_typeB = confrimPasswordTF.getText().trim();
            }
        } else if (passwordPrivacy2.getGlyphName().equalsIgnoreCase("EYE_OFF")) {
            if (!(actualConfrimPwd.getText().trim().isEmpty() || actualConfrimPwd.getText().isEmpty() || actualConfrimPwd.getText() == null)) {
                newPassword_typeB = actualConfrimPwd.getText().trim();
            }
        }
        if (newPassword_typeA.isEmpty() && !newPassword_typeB.isEmpty()) {
            empty_and_null_pointer_message(passwordTF.getParent()).show();
            return;
        } else {
            if (newPassword_typeB.isEmpty() && !newPassword_typeA.isEmpty()) {
                empty_and_null_pointer_message(confrimPasswordTF.getParent()).show();
                return;
            }
        }
        if (!newPassword_typeA.equals(newPassword_typeB)) {
            warning_message("Incomplete!", "The first and second password are NOT the same").show();
            return;
        }
        String newSiri = newPassword_typeA;
        if (updatedAdminAccount.getUsername() == null && updatedAdminAccount.getFname() == null && updatedAdminAccount.getSurname() == null && updatedAdminAccount.getEmail() == null && newSiri.isEmpty()) {
            warning_message("Wait!", "First edit any detail to continue...").show();
            return;
        }
        if (i_am_sure_of_it(" edit your account details ")) {
            final PasswordDialog passwordDialog = new PasswordDialog();
            final Optional<String> password_copy = passwordDialog.showAndWait();
            password_copy.ifPresent(password -> {
                if (password_copy.get().isEmpty()) {
                    error_message("No password entered", "Please type a password to confirm the actions you request to be done.").show();
                } else {
                    if (BCrypt.checkpw(password_copy.get(), Brain.myAccount.getPassword())) {
                        boolean passwordOrUsernameHasBeenUpdated = false;
                        if (!updatedAdminAccount.getUsername().isEmpty()) {
                            myAccount.setUsername(updatedAdminAccount.getUsername());
                            Account account = update_account(myAccount);
                            if (account == null) {
                                error_message("Failed!", "I was not able to save your new username").show();
                            } else {
                                oldNewUsernameTF.setText("");
                                success_notification("Your new username has been saved").show();
                                passwordOrUsernameHasBeenUpdated = true;
                            }
                        }
                        if (!updatedAdminAccount.getPassword().isEmpty()) {
                            final String newHashedSiri = BCrypt.hashpw(updatedAdminAccount.getPassword(), BCrypt.gensalt(14));
                            myAccount.setPassword(newHashedSiri);
                            Account account = update_account(myAccount);
                            if (account == null) {
                                error_message("Failed!", "I was not able to save your new password").show();
                            } else {
                                passwordTF.setText("");
                                actualPwd.setText("");
                                confrimPasswordTF.setText("");
                                actualConfrimPwd.setText("");
                                passwordOrUsernameHasBeenUpdated = true;
                                success_notification("Your new password has been saved").show();
                            }
                        }
                        if (!updatedAdminAccount.getFname().isEmpty()) {
                            myAccount.setFname(updatedAdminAccount.getFname());
                            Account account = update_account(myAccount);
                            if (account == null) {
                                error_message("Failed!", "I was not able to save your new firstname").show();
                            } else {
                                oldNewFirstnameTF.setText("");
                                success_notification("Your new firstname has been saved").show();
                            }
                        }
                        if (!updatedAdminAccount.getSurname().isEmpty()) {
                            myAccount.setSurname(updatedAdminAccount.getSurname());
                            Account account = update_account(myAccount);
                            if (account == null) {
                                error_message("Failed!", "I was not able to save your new surname").show();
                            } else {
                                oldNewSurnameTF.setText("");
                                success_notification("Your new surname has been saved").show();
                            }
                        }
                        if (!updatedAdminAccount.getEmail().isEmpty()) {
                            myAccount.setEmail(updatedAdminAccount.getEmail());
                            Account account = update_account(myAccount);
                            if (account == null) {
                                error_message("Failed!", "I was not able to save your new email").show();
                            } else {
                                oldNewEmailTF.setText("");
                                success_notification("Your new email has been saved").show();
                            }
                        }
                        show_admin_info();
                        String successMessage;
                        if (passwordOrUsernameHasBeenUpdated) {
                            log_out_from_my_account(new ActionEvent());
                            successMessage = "You are now required to Login again with your new details";
                        } else {
                            successMessage = "Your new account has been loaded";
                        }
                        success_notification(successMessage).title("Saved!").show();
                    } else {
                        error_message("Unknown password!", "Ensure you remember quite well your current password").show();
                    }
                }
            });
        }
    }

    @FXML
    void find_account(@NotNull ActionEvent event) {
        final JFXTextField jfxTextField = (JFXTextField) event.getSource();
        if (jfxTextField.equals(accountParamTF)) {
            if (jfxTextField.getText().trim().isEmpty() || jfxTextField.getText().isEmpty() || jfxTextField.getText() == null) {
                empty_and_null_pointer_message(jfxTextField.getParent().getParent()).show();
                return;
            }
            display_users_based_on_observableList_param(get_staff_users_based_on_param(jfxTextField.getText().trim()));
        }
    }

    @FXML
    void find_stock_item(@NotNull ActionEvent event) {
        final JFXTextField jfxTextField = (JFXTextField) event.getSource();
        if (jfxTextField.getText().trim().isEmpty() || jfxTextField.getText() == null) {
            empty_and_null_pointer_message(jfxTextField).show();
            return;
        }
        if (jfxTextField.equals(stockParameterTF)) {
            Platform.runLater(() -> display_stock_items_based_on_observableList_param(get_stock_items_based_on_param(jfxTextField.getText().trim())));
        } else if (jfxTextField.equals(prodCodeUpParTF)) {
            if (jfxTextField.getText().trim().isEmpty() || jfxTextField.getText() == null) {
                empty_and_null_pointer_message(jfxTextField).show();
                display_old_details_of_an_item(null);
                return;
            }
            display_old_details_of_an_item(get_specific_product(jfxTextField.getText().trim()));
        } else {
            if (jfxTextField.getText().trim().isEmpty() || jfxTextField.getText() == null) {
                empty_and_null_pointer_message(jfxTextField).show();
                display_current_details_of_an_item(null);
                return;
            }
            display_current_details_of_an_item(get_specific_product(jfxTextField.getText().trim()));
        }
    }

    @FXML
    void load_new_image_of_product_we_want_to_update(ActionEvent event) {
        if (event != null) {
            PATH_TO_IMAGE_OF_UPDATED_PRODUCT = look_for_image(prodImageView1);
        }
    }

    @FXML
    void load_all_sales(@NotNull ActionEvent event) {
        monthCbx.getSelectionModel().clearSelection();
        dateDp.getEditor().setText("");
        final JFXRadioButton jfxRadioButton = (JFXRadioButton) event.getSource();
        if (jfxRadioButton.equals(allRb)) {
            if (jfxRadioButton.isSelected()) {
                display_purchases_based_on_observableList_param(get_purchases_based_on_param(""));
            }
        }
    }

    @FXML
    void load_sales_of_provided_days(@NotNull ActionEvent event) {
        monthCbx.getSelectionModel().clearSelection();
        dateDp.getEditor().setText("");
        final JFXRadioButton jfxRadioButton = (JFXRadioButton) event.getSource();
        if (jfxRadioButton.isSelected()) {
            if (event.getSource().equals(todayRb)) {
                display_purchases_based_on_observableList_param(get_purchases_based_on_param(date_today()));
            } else {
                if (event.getSource().equals(yesterdayRb)) {
                    display_purchases_based_on_observableList_param(get_purchases_based_on_param(previous_date()));
                }
            }
        }
    }

    @FXML
    void load_specific_receipt_data(@NotNull ActionEvent event) {
        final JFXTextField jfxTextField = (JFXTextField) event.getSource();
        if (jfxTextField.equals(rcptNoParamTF)) {
            if (jfxTextField.getText().trim().isEmpty() || jfxTextField.getText().isEmpty() || jfxTextField.getText() == null) {
                empty_and_null_pointer_message(jfxTextField.getParent().getParent()).show();
                return;
            }
            display_receipts_based_on_observableList_param(get_receipts_based_on_param(jfxTextField.getText().trim()));
        }
    }

    @FXML
    void log_out_from_my_account(ActionEvent event) {
        if (event == null) {
            return;
        }
        if (animate_show_dashboard()) {
            new SlideOutLeft(topBarPane).play();
            new SlideOutUp(leftSideMenuPane).play();
            new FadeOut(deskPane).play();
            Sample.logoutThread.start();
        }
    }

    @FXML
    void plot_area_chart_with_parameters(@NotNull ActionEvent event) {
        final JFXComboBox<?> jfxComboBox = (JFXComboBox<?>) event.getSource();
        if (jfxComboBox.getValue().toString().trim().isEmpty() || jfxComboBox.getValue().toString().isEmpty() || jfxComboBox.getValue() == null) {
            empty_and_null_pointer_message(jfxComboBox).show();
            return;
        }
        final String parameter = jfxComboBox.getValue().toString().trim();
        if (parameter.length() == 4) {
            plot_line_graph_based_on_profit_per_month(load_profit_and_loss_trend_across_months_of_a_requested_year(parameter), parameter);
            if (!customYearCbx.getItems().contains(parameter)) {
                customYearCbx.getItems().add(parameter);
            }
        }
    }

    @FXML
    void reload_accounts(@NotNull MouseEvent event) {
        accountParamTF.setText("");
        set_up_accounts();
        final Node node = (Node) event.getSource();
        new RotateOut(node).setResetOnFinished(true).play();
    }

    @FXML
    void reload_payments(@NotNull MouseEvent event) {
        set_up_payments_pane();
        final Node node = (Node) event.getSource();
        new RotateOut(node).setResetOnFinished(true).play();
    }

    @FXML
    void reload_receipts(@NotNull MouseEvent event) {
        rcptNoParamTF.setText("");
        display_receipts_based_on_observableList_param(get_receipts_based_on_param(""));
        final Node node = (Node) event.getSource();
        new RotateOut(node).setResetOnFinished(true).play();
    }

    @FXML
    void reload_stock(@NotNull MouseEvent event) {
        prodCodeUpParTF.setText("");
        stockParameterTF.setText("");
        prodCodeDelParTF.setText("");
        new Thread(reset_add_stock_item_pane_for_the_next_one()).start();
        final Task<Object> objectTask = reset_update_stock_item_pane_for_the_next_one();
        objectTask.setOnSucceeded(event1 -> {
            display_old_details_of_an_item(null);
            display_current_details_of_an_item(null);
        });
        new Thread(objectTask).start();
        setup_stock_pane();
        final Node node = (Node) event.getSource();
        new RotateOut(node).setResetOnFinished(true).play();
    }

    @FXML
    void search_for_sale(@NotNull ActionEvent event) {
        if (event.getSource().equals(dateDp)) {
            final JFXDatePicker jfxDatePicker = dateDp;
            if (jfxDatePicker.getEditor().getText().trim().isEmpty() || jfxDatePicker.getEditor().getText().isEmpty() || jfxDatePicker.getEditor().getText() == null) {
                empty_and_null_pointer_message(jfxDatePicker.getParent().getParent()).show();
                return;
            }
            display_purchases_based_on_observableList_param(get_purchases_based_on_param(jfxDatePicker.getEditor().getText()));
        } else {
            try {
                if (monthCbx.getSelectionModel().getSelectedItem().isEmpty() || monthCbx.getSelectionModel().getSelectedItem() == null || monthCbx.getValue() == null) {
                    empty_and_null_pointer_message(monthCbx.getParent().getParent()).show();
                    return;
                }
            } catch (NullPointerException e) {
                empty_and_null_pointer_message(monthCbx.getParent().getParent()).show();
                e.printStackTrace();
                return;
            }
            String parameters;
            final String requested_year = concat_year_to_the_selected_month(monthCbx.getSelectionModel().getSelectedItem());
            if (requested_year == null) {
                parameters = shortForm_of_the_month(monthCbx.getSelectionModel().getSelectedItem());
            } else {
                parameters = shortForm_of_the_month(monthCbx.getSelectionModel().getSelectedItem()) + "-" + requested_year;
            }
            display_purchases_based_on_observableList_param(get_purchases_based_on_param(parameters));
        }
        if (sale_period.getSelectedToggle() != null) {
            sale_period.getSelectedToggle().setSelected(false);
        }
    }

    @FXML
    void show_accounts(@NotNull ActionEvent event) {
        final JFXButton jfxButton = (JFXButton) event.getSource();
        if (animate_show_accounts(jfxButton)) {
            show_admin_info();
        }
    }

    @FXML
    void show_dashboard(@NotNull ActionEvent event) {
        final JFXButton jfxButton = (JFXButton) event.getSource();
        animate_show_dashboard(jfxButton);
    }

    @FXML
    void show_payments(@NotNull ActionEvent event) {
        final JFXButton jfxButton = (JFXButton) event.getSource();
        animate_show_payments(jfxButton);
    }

    @FXML
    void show_purchases(@NotNull ActionEvent event) {
        final JFXButton jfxButton = (JFXButton) event.getSource();
        animate_show_purchases(jfxButton);
    }

    @FXML
    void show_statistics(@NotNull ActionEvent event) {
        final JFXButton jfxButton = (JFXButton) event.getSource();
        animate_show_statistics(jfxButton);
    }

    @FXML
    void show_stock(@NotNull ActionEvent event) {
        final JFXButton jfxButton = (JFXButton) event.getSource();
        animate_show_stock(jfxButton);
    }

    @FXML
    void show_typed_password(@NotNull MouseEvent event) {
        if (event.getSource().equals(newPswdEye)) {
            show_hide_password(newPswdEye, actualPwd, passwordTF);
        } else {
            show_hide_password(newConfirmPswdEye, actualConfrimPwd, confrimPasswordTF);
        }
    }

    @FXML
    void update_product_details(ActionEvent event) {
        if (event != null) {
            try {
                if (myProduct == null) {
                    error_message("Failed!", "Please search an item first to continue").show();
                    return;
                }
                if (!newProdSerialTF.getText().trim().isEmpty() || newProdSerialTF.getText() != null) {
                    if (stockSerials.contains(newProdSerialTF.getText().trim())) {
                        error_message("Serial number is known!", "The serial already exists, enter a new one").show();
                        return;
                    }
                    if (!newProdSerialTF.getText().isEmpty()) {
                        if (!myProduct.getSerial_number().equals(newProdSerialTF.getText().trim())) {
                            productToUpdate.setSerial_number(newProdSerialTF.getText().trim());
                        }
                    }
                }
                if (newProdQtyTF.getText() != null) {
                    if (!newProdQtyTF.getText().isEmpty()) {
                        if (Integer.parseInt(newProdQtyTF.getText()) < 0) {
                            newProdQtyTF.setText("0");
                        }
                        if (!myProduct.getStock().equals(Integer.parseInt(newProdQtyTF.getText().trim()))) {
                            productToUpdate.setStock(Integer.parseInt(newProdQtyTF.getText().trim()));
                        }
                    }
                }
                if (newProdStarsCbx.getSelectionModel().getSelectedIndex() != 0 || newProdStarsCbx.getSelectionModel().getSelectedItem() != null) {
                    if (!myProduct.getRating().equals(newProdStarsCbx.getSelectionModel().getSelectedItem())) {
                        productToUpdate.setRating(newProdStarsCbx.getSelectionModel().getSelectedItem());
                    }
                }
                if (!newProdNameTF.getText().trim().isEmpty() || newProdNameTF.getText() != null) {
                    if (!newProdNameTF.getText().trim().equals("")) {
                        if (!myProduct.getName().equals(newProdNameTF.getText().trim())) {
                            productToUpdate.setName(newProdNameTF.getText().trim());
                        }
                    }
                }
                if (!newDescriptionTA.getText().trim().isEmpty() || newDescriptionTA.getText() != null) {
                    if (!newDescriptionTA.getText().trim().equals("")) {
                        if (!(myProduct.getDescription().equals(newDescriptionTA.getText().trim()))) {
                            productToUpdate.setDescription(newDescriptionTA.getText().trim());
                        }
                    }
                }
                if (newMarkedPriceTF.getText() != null) {
                    if (!newMarkedPriceTF.getText().isEmpty()) {
                        if (Double.parseDouble(newMarkedPriceTF.getText().trim()) < 0) {
                            newMarkedPriceTF.setText("0");
                        }
                        if (!myProduct.getMarkedPrice().equals(Double.parseDouble(newMarkedPriceTF.getText().trim()))) {
                            productToUpdate.setMarkedPrice(Double.parseDouble(newMarkedPriceTF.getText().trim()));
                        }
                    }
                }
                if (newBuyingPriceTF.getText() != null) {
                    if (!newBuyingPriceTF.getText().isEmpty()) {
                        if (Double.parseDouble(newBuyingPriceTF.getText().trim()) < 0) {
                            newBuyingPriceTF.setText("0");
                        }
                        if (!myProduct.getBuyingPrice().equals(Double.parseDouble(newBuyingPriceTF.getText().trim()))) {
                            productToUpdate.setBuyingPrice(Double.parseDouble(newBuyingPriceTF.getText().trim()));
                        }
                    }
                }
                if (PATH_TO_IMAGE_OF_UPDATED_PRODUCT == null || PATH_TO_IMAGE_OF_UPDATED_PRODUCT.equals(INPUT_STREAM_TO_NO_IMAGE)) {
                    productToAdd.setImage(null);
                } else {
                    try {
                        byte[] imageAsBytes = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE_OF_UPDATED_PRODUCT));
                        String encodedImage = Base64.getEncoder().encodeToString(imageAsBytes);
                        productToAdd.setImage(encodedImage);
                    } catch (IOException e) {
                        productToAdd.setImage(null);
                        e.printStackTrace();
                        programmer_error(e).show();
                    }
                }
                if (i_am_sure_of_it("update this item")) {
                    if (update_a_stock_item(myProduct.getSerial_number(), productToUpdate)) {
                        setup_stock_pane();
                        productToUpdate.clear();
                        PATH_TO_IMAGE_OF_UPDATED_PRODUCT = null;
                        new Thread(reset_update_stock_item_pane_for_the_next_one()).start();
                        display_old_details_of_an_item(null);
                        success_notification("Retry searching the item to view changes").show();
                    } else {
                        error_message("Failed!", "Item has not been updated as required").show();
                    }
                }
            } catch (Exception e) {
                new Thread(new Watchdog().write_log("\n\n" + new Watchdog().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                new Thread(new Watchdog().stack_trace_printing(e)).start();
                e.printStackTrace();
                new Watchdog().programmer_error(e).show();
            }
        }
    }

    @FXML
    void upload_product(ActionEvent event) {
        if (event != null) {
            if (prodSerialTF.getText().trim().isEmpty() || prodSerialTF.getText() == null) {
                empty_and_null_pointer_message(prodSerialTF).show();
                return;
            }
            if (stockSerials.contains(prodSerialTF.getText().trim())) {
                error_message("Serial number is known!", "The serial already exists, enter a new one").show();
                return;
            }
            if (prodQtyTF.getText().trim().isEmpty() || prodQtyTF.getText() == null) {
                empty_and_null_pointer_message(prodSerialTF).show();
                return;
            }
            if (Integer.parseInt(prodQtyTF.getText().trim()) < 0) {
                prodQtyTF.setText("0");
            }
            if (prodRatingsCbx.getSelectionModel().getSelectedItem() == 0 || prodRatingsCbx.getSelectionModel().getSelectedItem() == null) {
                empty_and_null_pointer_message(prodRatingsCbx).show();
            }
            if (prodNameTF.getText().trim().isEmpty() || prodNameTF.getText() == null) {
                empty_and_null_pointer_message(prodNameTF).show();
                return;
            }
            if (prodDescriptionTF.getText().trim().isEmpty() || prodDescriptionTF.getText() == null) {
                empty_and_null_pointer_message(prodDescriptionTF).show();
                return;
            }
            if (prodMarkedPriceTF.getText().trim().isEmpty() || prodMarkedPriceTF.getText() == null) {
                empty_and_null_pointer_message(prodMarkedPriceTF).show();
                return;
            }
            if (Integer.parseInt(prodMarkedPriceTF.getText().trim()) < 0) {
                prodMarkedPriceTF.setText("0");
            }
            if (prodBuyingPriceTF.getText().trim().isEmpty() || prodBuyingPriceTF.getText() == null) {
                empty_and_null_pointer_message(prodBuyingPriceTF).show();
                return;
            }
            if (Integer.parseInt(prodBuyingPriceTF.getText().trim()) < 0) {
                prodBuyingPriceTF.setText("0");
            }
            productToAdd.clear();
            productToAdd.setSerial_number(prodSerialTF.getText().trim());
            productToAdd.setName(prodNameTF.getText().trim());
            productToAdd.setDescription(prodDescriptionTF.getText().trim());
            productToAdd.setStock(Integer.parseInt(prodQtyTF.getText().trim()));
            productToAdd.setRating(prodRatingsCbx.getSelectionModel().getSelectedItem());
            productToAdd.setMarkedPrice(Double.parseDouble(prodMarkedPriceTF.getText().trim()));
            productToAdd.setBuyingPrice(Double.parseDouble(prodBuyingPriceTF.getText().trim()));
            String nameOfImage;
            if (PATH_TO_IMAGE_OF_NEW_PRODUCT == null || PATH_TO_IMAGE_OF_NEW_PRODUCT.equals(INPUT_STREAM_TO_NO_IMAGE)) {
                nameOfImage = "";
                productToAdd.setImage(null);
            } else {
                nameOfImage = new File(PATH_TO_IMAGE_OF_NEW_PRODUCT).getName();
                try {
                    byte[] imageAsBytes = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE_OF_NEW_PRODUCT));
                    String encodedImage = Base64.getEncoder().encodeToString(imageAsBytes);
                    productToAdd.setImage(encodedImage);
                } catch (IOException e) {
                    productToAdd.setImage(null);
                    e.printStackTrace();
                    programmer_error(e).show();
                    return;
                }
            }
            if (nameOfImage.isEmpty()) {
                error_message("Image not found!", "The path to the image seems broken or is bad, but don't worry the default one has been set").show();
            }
            if (add_product_to_stock(productToAdd)) {
                new Thread(reset_add_stock_item_pane_for_the_next_one()).start();
                productToAdd.clear();
                setup_stock_pane();
                PATH_TO_IMAGE_OF_NEW_PRODUCT = null;
                success_notification("Product has been added").show();
            } else {
                error_message("Failed!", "The product was not added to stock").show();
            }
        }
    }

    @FXML
    void view_accounts_pane(ActionEvent event) {
        if (accountsViewPane.getOpacity() < 1) {
            for (int index = 1; index <= 4; ++index) {
                if (index != 1) {
                    if (accounts_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(accounts_leaflets.get(index)).play();
                    }
                }
            }
            accounts_leaflets.get(1).toFront();
            new FadeIn(accounts_leaflets.get(1)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(1, ((JFXButton) event.getSource()), accounts_underline);
        }
    }

    @FXML
    void view_add_stock_pane(ActionEvent event) {
        if (stockaddPane.getOpacity() < 1) {
            for (int index = 1; index <= 4; ++index) {
                if (index != 2) {
                    if (stock_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(stock_leaflets.get(index)).play();
                    }
                }
            }
            stock_leaflets.get(2).toFront();
            new FadeIn(stock_leaflets.get(2)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(2, ((JFXButton) event.getSource()), stock_underline);
        }
    }

    @FXML
    void view_add_user_pane(ActionEvent event) {
        if (accountsAddPane.getOpacity() < 1) {
            for (int index = 1; index <= 4; ++index) {
                if (index != 2) {
                    if (accounts_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(accounts_leaflets.get(index)).play();
                    }
                }
            }
            accounts_leaflets.get(2).toFront();
            new FadeIn(accounts_leaflets.get(2)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(2, ((JFXButton) event.getSource()), accounts_underline);
        }
    }

    @FXML
    void view_cheques_pane(ActionEvent event) {
        if (paymentsChequePane.getOpacity() < 1) {
            for (int index = 1; index <= 2; ++index) {
                if (index != 2) {
                    if (payments_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(payments_leaflets.get(index)).play();
                    }
                }
            }
            payments_leaflets.get(2).toFront();
            new FadeIn(payments_leaflets.get(2)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(2, ((JFXButton) event.getSource()), payments_underline);
        }
    }

    @FXML
    void view_delete_stock_pane(ActionEvent event) {
        if (stockDeletePane.getOpacity() < 1) {
            for (int index = 1; index <= 4; ++index) {
                if (index != 4) {
                    if (stock_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(stock_leaflets.get(index)).play();
                    }
                }
            }
            stock_leaflets.get(4).toFront();
            new FadeIn(stock_leaflets.get(4)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(4, ((JFXButton) event.getSource()), stock_underline);
        }
    }

    @FXML
    void view_edit_user_pane(ActionEvent event) {
        if (accountsEditPane.getOpacity() < 1) {
            for (int index = 1; index <= 4; ++index) {
                if (index != 3) {
                    if (accounts_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(accounts_leaflets.get(index)).play();
                    }
                }
            }
            accounts_leaflets.get(3).toFront();
            new FadeIn(accounts_leaflets.get(3)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(3, ((JFXButton) event.getSource()), accounts_underline);
        }
    }

    @FXML
    void view_payments_summary_pane(ActionEvent event) {
        if (paymentsSummaryPane.getOpacity() < 1) {
            for (int index = 1; index <= 2; ++index) {
                if (index != 1) {
                    if (payments_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(payments_leaflets.get(index)).play();
                    }
                }
            }
            payments_leaflets.get(1).toFront();
            new FadeIn(payments_leaflets.get(1)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(1, ((JFXButton) event.getSource()), payments_underline);
        }
    }

    @FXML
    void view_receipts_pane(ActionEvent event) {
        if (purchaseReceiptsPane.getOpacity() < 1) {
            for (int index = 1; index <= 2; ++index) {
                if (index != 2) {
                    if (purchases_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(purchases_leaflets.get(index)).play();
                    }
                }
            }
            purchases_leaflets.get(2).toFront();
            new FadeIn(purchases_leaflets.get(2)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(2, ((JFXButton) event.getSource()), purchases_underline);
        }
    }

    @FXML
    void view_remove_user_pane(ActionEvent event) {
        if (accountsRemovePane.getOpacity() < 1) {
            for (int index = 1; index <= 4; ++index) {
                if (index != 4) {
                    if (accounts_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(accounts_leaflets.get(index)).play();
                    }
                }
            }
            accounts_leaflets.get(4).toFront();
            new FadeIn(accounts_leaflets.get(4)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(4, ((JFXButton) event.getSource()), accounts_underline);
        }
    }

    @FXML
    void view_sales_summary_pane(ActionEvent event) {
        if (purchaseSummaryPane.getOpacity() < 1) {
            for (int index = 1; index <= 2; ++index) {
                if (index != 1) {
                    if (purchases_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(purchases_leaflets.get(index)).play();
                    }
                }
            }
            purchases_leaflets.get(1).toFront();
            new FadeIn(purchases_leaflets.get(1)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(1, ((JFXButton) event.getSource()), purchases_underline);
        }
    }

    @FXML
    void view_stock_pane(ActionEvent event) {
        if (stockViewPane.getOpacity() < 1) {
            for (int index = 1; index <= 4; ++index) {
                if (index != 1) {
                    if (stock_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(stock_leaflets.get(index)).play();
                    }
                }
            }
            stock_leaflets.get(1).toFront();
            new FadeIn(stock_leaflets.get(1)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(1, ((JFXButton) event.getSource()), stock_underline);
        }
    }

    @FXML
    void view_update_stock_pane(ActionEvent event) {
        if (stockUpdatePane.getOpacity() < 1) {
            for (int index = 1; index <= 4; ++index) {
                if (index != 3) {
                    if (stock_leaflets.get(index).getOpacity() > 0) {
                        new FadeOut(stock_leaflets.get(index)).play();
                    }
                }
            }
            stock_leaflets.get(3).toFront();
            new FadeIn(stock_leaflets.get(3)).setDelay(Duration.seconds(0.5)).play();
            style_clicked_sub_menu_items(3, ((JFXButton) event.getSource()), stock_underline);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        set_up_UX();
        Platform.runLater(this::breath_of_life);
    }

    private void breath_of_life() {
        setup_Dashboard();
        setup_stock_pane();
        permanent_stuff();
        set_up_purchase_pane();
        set_up_payments_pane();
        set_up_statistics();
        set_up_accounts();
    }

    private void permanent_stuff() {
        arrange_nodes_in_their_appropriate_hashmap();
        assign_text_fields_suggestions_popups(stockParameterTF, stockSuggestionsPopup);
        assign_text_fields_suggestions_popups(prodSerialTF, badStockSerialSuggestionsPopup);
        assign_text_fields_suggestions_popups(prodCodeUpParTF, searchStockSerialForUpdateSuggestionsPopup);
        assign_text_fields_suggestions_popups(newProdSerialTF, searchStockSerialForUpdateSuggestionsPopup1);
        assign_text_fields_suggestions_popups(prodCodeDelParTF, searchStockSerialForDeleteSuggestionsPopup);
        make_a_textField_numeric(new JFXTextField[]{prodQtyTF, prodMarkedPriceTF, prodBuyingPriceTF, newProdQtyTF, newBuyingPriceTF, newMarkedPriceTF, rcptNoParamTF});
        assign_text_fields_suggestions_popups(rcptNoParamTF, receiptNumberSuggestionsPopup);
        assign_text_fields_suggestions_popups(accountParamTF, staffUsernameSuggestionsPopup);
        assign_text_fields_suggestions_popups(newUsernameTF, staffUsernameSuggestionsPopup1);
    }

    private void set_up_UX() {
        new SlideInLeft(topBarPane).play();
        new SlideInUp(leftSideMenuPane).play();
        new FadeIn(deskPane).play();
        format_datePickers_to_show_my_preferred_date_style(dateDp);
        prodRatingsCbx.getItems().clear();
        prodRatingsCbx.getItems().addAll(1, 2, 3, 4, 5);
        newProdStarsCbx.getItems().addAll(1, 2, 3, 4, 5);
        monthCbx.getItems().clear();
        monthCbx.setItems(get_data_from_properties_file());
    }

    private void set_up_accounts() {
        display_users_based_on_observableList_param(get_staff_users_based_on_param(""));
        if (!listOfActiveStaffUsername.isEmpty()) {
            listOfActiveStaffUsername.clear();
        }
        listOfActiveStaffUsername.addAll(get_username_staff_based_on_employment_status(true));
        if (!listOfIn_ActiveStaffUsername.isEmpty()) {
            listOfIn_ActiveStaffUsername.clear();
        }
        listOfIn_ActiveStaffUsername.addAll(get_username_staff_based_on_employment_status(false));
        update_autoComplete_suggestions(staffUsernameSuggestionsPopup, listOfActiveStaffUsername);
        final ObservableList<String> listOfAllUsernames = FXCollections.observableArrayList();
        listOfAllUsernames.addAll(listOfActiveStaffUsername);
        listOfAllUsernames.addAll(listOfIn_ActiveStaffUsername);
        update_autoComplete_suggestions(staffUsernameSuggestionsPopup1, listOfAllUsernames);
        display_all_users(get_all_users());
        userFullnameTF.setText(Brain.myAccount.getFname().concat(" ").concat(Brain.myAccount.getSurname()));
    }

    private void display_all_users(ObservableList<User> users) {
        final ObservableList<Node> nodes = accountsBox.getChildren();
        if (!nodes.isEmpty()) {
            for (Node node : nodes) {
                Platform.runLater(() -> {
                    StackPane.clearConstraints(node);
                    accountsBox.getChildren().remove(node);
                });
            }
        }
        if (users.isEmpty()) {
            return;
        }
        for (User user : users) {
            try {
                Node node;
                if (user.isAdmin()) {
                    AdminUI.globalUser = user;
                    node = FXMLLoader.load(getClass().getResource("/org/_brown_tech/_fxml/adminUI.fxml"));
                } else {
                    EmployeeUI.globalUser = user;
                    node = FXMLLoader.load(getClass().getResource("/org/_brown_tech/_fxml/employeeUI.fxml"));
                }
                Platform.runLater(() -> accountsBox.getChildren().add(node));
            } catch (IOException e) {
                new Thread(new Watchdog().write_log("\n\n" + new Watchdog().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                new Thread(new Watchdog().stack_trace_printing(e)).start();
                e.printStackTrace();
                new Watchdog().programmer_error(e).show();
            }
        }

    }

    private void show_admin_info() {
        Brain.myAccount = get_user_account(myAccount.getUsername(), null);
        if (Brain.myAccount == null) {
            return;
        }
        oldNewFirstnameTF.setText(Brain.myAccount.getFname());
        oldNewSurnameTF.setText(Brain.myAccount.getSurname());
        oldNewEmailTF.setText(Brain.myAccount.getEmail());
        oldNewUsernameTF.setText(Brain.myAccount.getUsername());
    }

    private void display_users_based_on_observableList_param(ObservableList<AccountModel> accountModelObservableList) {
        final JFXTreeTableView<AccountModel> jfxTreeTableView = accountsTable;
        accUsernameCol.setCellValueFactory(param -> param.getValue().getValue().username);
        accFirstnameCol.setCellValueFactory(param -> param.getValue().getValue().firstname);
        accSurnameCol.setCellValueFactory(param -> param.getValue().getValue().surname);
        accEmailCol.setCellValueFactory(param -> param.getValue().getValue().email);
        final TreeItem<AccountModel> root = new RecursiveTreeItem<>(accountModelObservableList, RecursiveTreeObject::getChildren);
        jfxTreeTableView.setRoot(root);
        jfxTreeTableView.setShowRoot(false);
        if (!accountModelObservableList.isEmpty()) {
            jfxTreeTableView.refresh();
        }
    }

    private void set_up_statistics() {
        final String DATE_TODAY = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        plot_line_graph_based_on_profit_per_month(load_profit_and_loss_trend_across_months_of_a_requested_year(DATE_TODAY), DATE_TODAY);
    }

    private void plot_line_graph_based_on_profit_per_month(@NotNull HashMap<String, Double> data, String requestedYear) {
        load_profit_of_every_month(data);
        final XYChart.Series<String, Double> dataSeries = new XYChart.Series<>();
        if (areaChart.getData().size() > 0) {
            areaChart.getData().clear();
        }
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : months) {
            dataSeries.getData().add(new XYChart.Data<>(month.toUpperCase(), data.get(month)));
        }
        dataSeries.setName(requestedYear);
        areaChart.getData().add(dataSeries);
    }

    private void load_profit_of_every_month(HashMap<String, Double> monthlyProfitData) {
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        double totalSum = 0.0;
        for (String string : months) {
            totalSum += monthlyProfitData.get(string);
        }
        janProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[0]))));
        febProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[1]))));
        marProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[2]))));
        aprProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[3]))));
        mayProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[4]))));
        junProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[5]))));
        julProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[6]))));
        augProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[7]))));
        sepProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[8]))));
        octProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[9]))));
        novProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[10]))));
        decProfitLbl.setText("Ksh ".concat(String.format("%,.1f", monthlyProfitData.get(months[11]))));
        //yearProfitLbl.setText("Ksh ".concat(String.format("%,.1f", totalSum)));
        yearProfitLbl.setText("Ksh ".concat(pretty_show_money(totalSum)));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Task<Object> display_cheques(List<Cheque> chequeObservableList) {
        return new Task<Object>() {
            @Override
            protected Object call() {
                final ObservableList<Node> nodeObservableList = chequeBox.getChildren();
                for (Node node : nodeObservableList) {
                    Platform.runLater(() -> {
                        VBox.clearConstraints(node);
                        chequeBox.getChildren().remove(node);
                    });
                }
                for (Cheque cheque : chequeObservableList) {
                    try {
                        ChequeUI.globalCheque = cheque;
                        final Node node = FXMLLoader.load(getClass().getResource("/org/_brown_tech/_fxml/chequeUI.fxml"));
                        Platform.runLater(() -> {
                            chequeBox.getChildren().add(node);
                            new SlideInRight(node).play();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        programmer_error(e);
                        new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                        new Thread(stack_trace_printing(e)).start();
                        Platform.runLater(() -> error_message_alert("IOException: ", e.getLocalizedMessage()).show());
                    }
                }
                return null;
            }
        };
    }

    private void set_up_cheque() {
        final JsonObject jsonObject = get_cheque_data();

        final JsonObject approved = jsonObject.get("approved").getAsJsonObject();
        final int approvedOnes = approved.get("count").getAsInt();
        final double amountApproved = approved.get("amount").getAsDouble();

        final JsonObject pending = jsonObject.get("pending").getAsJsonObject();
        final int pendingOnes = pending.get("count").getAsInt();
        final double amountPending = pending.get("amount").getAsDouble();

        final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        pieChartData.addAll(new PieChart.Data("Pending", pendingOnes), new PieChart.Data("Approved", approvedOnes));
        chequePieChart.setData(pieChartData);

        final int noOfCheques = (pendingOnes + approvedOnes);
        chequeCountLbl.setText(String.format("%d", noOfCheques));

        aprvdChqLbl.setText("Ksh ".concat(String.format("%,.1f", amountApproved)));
        pndChqLbl.setText("Ksh ".concat(String.format("%,.1f", amountPending)));

        new Thread(display_cheques(get_pending_cheques())).start();
    }

    private void set_up_payments_pane() {
        JsonObject jsonObject = get_sum_of_payment_methods();
        if (jsonObject != null) {
            final double cashAmount = jsonObject.get("cash").getAsDouble();
            final double chequeAmount = jsonObject.get("cheque").getAsDouble();
            final double mpesaAmount = jsonObject.get("mpesa").getAsDouble();
            cashReturnsLbl.setText("Ksh ".concat(String.format("%,.1f", cashAmount)));
            chequeReturnsLbl.setText("Ksh ".concat(String.format("%,.1f", chequeAmount)));
            mpesaReturnsLbl.setText("Ksh ".concat(String.format("%,.1f", mpesaAmount)));

            final double totalReturns = (cashAmount + chequeAmount + mpesaAmount);
            sum_of_returnsLbl.setText("Ksh ".concat(String.format("%,.1f", totalReturns)));

            final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            pieChartData.addAll(new PieChart.Data("Cash", cashAmount), new PieChart.Data("Cheque", chequeAmount), new PieChart.Data("M-Pesa", mpesaAmount));
            paymentsPieChart.setData(pieChartData);

            set_up_cheque();
        }
    }

    @NotNull
    @Contract(pure = true)
    private String shortForm_of_the_month(@NotNull String string) {
        switch (string) {
            case "January":
            default:
                return "Jan";
            case "February":
                return "Feb";
            case "March":
                return "Mar";
            case "May":
                return "May";
            case "June":
                return "Jun";
            case "July":
                return "Jul";
            case "August":
                return "Aug";
            case "September":
                return "Sep";
            case "October":
                return "Oct";
            case "November":
                return "Nov";
            case "December":
                return "Dec";
        }
    }

    private void set_up_purchase_pane() {
        display_purchases_based_on_observableList_param(get_purchases_based_on_param(""));
        display_receipts_based_on_observableList_param(get_receipts_based_on_param(""));
        final ObservableList<String> receipts = get_a_list_of_receipt_numbers();
        update_autoComplete_suggestions(receiptNumberSuggestionsPopup, receipts);
    }

    private void display_receipts_based_on_observableList_param(ObservableList<ReceiptModel> purchaseObservableList) {
        final JFXTreeTableView<ReceiptModel> jfxTreeTableView = receiptsTable;
        receiptCol.setCellValueFactory(param -> param.getValue().getValue().receipt_no);
        rcptDateCol.setCellValueFactory(param -> param.getValue().getValue().date_time);
        codeCol.setCellValueFactory(param -> param.getValue().getValue().serial_number);
        qtyCol.setCellValueFactory(param -> param.getValue().getValue().serial_number);
        sellingPriceCol.setCellValueFactory(param -> param.getValue().getValue().selling_price);
        buyPricePriceCol.setCellValueFactory(param -> param.getValue().getValue().buying_price);
        typeOfStockCol.setCellValueFactory(param -> param.getValue().getValue().typeOfStock);
        final TreeItem<ReceiptModel> root = new RecursiveTreeItem<>(purchaseObservableList, RecursiveTreeObject::getChildren);
        jfxTreeTableView.setRoot(root);
        jfxTreeTableView.setShowRoot(false);
        if (!purchaseObservableList.isEmpty()) {
            jfxTreeTableView.refresh();
        }
    }

    private void display_purchases_based_on_observableList_param(ObservableList<PurchaseModel> purchaseModelObservableList) {
        final JFXTreeTableView<PurchaseModel> jfxTreeTableView = salesTable;
        dateCol.setCellValueFactory(param -> param.getValue().getValue().date_time);
        saleRcptCol.setCellValueFactory(param -> param.getValue().getValue().receipt_no);
        saleAmtCol.setCellValueFactory(param -> param.getValue().getValue().billAmount);
        paidAsCol.setCellValueFactory(param -> param.getValue().getValue().amount);
        soldByCol.setCellValueFactory(param -> param.getValue().getValue().username);
        final TreeItem<PurchaseModel> root = new RecursiveTreeItem<>(purchaseModelObservableList, RecursiveTreeObject::getChildren);
        jfxTreeTableView.setRoot(root);
        jfxTreeTableView.setShowRoot(false);
        if (!purchaseModelObservableList.isEmpty()) {
            jfxTreeTableView.refresh();
        }
    }

    private void display_current_details_of_an_item(Product product) {
        if (product == null) {
            delProdImageView.setEffect(null);
            no_image_for_product(delProdImageView);
            delProdNameLbl.setText("");
            delDescriptionLbl.setText("");
            delRatingsImageView.setImage(null);
            delStockQuantityLbl.setText("");
            delBuyingPriceLbl.setText("");
            delMarkedPriceLbl.setText("");
            if (delResultsPane.getOpacity() > 0) {
                new FadeOut(delResultsPane).play();
                delSearchResultLbl.toFront();
                new FadeIn(delSearchResultLbl).play();
            }
            new RubberBand(delSearchResultLbl).play();
            if (delResultsPane.getOpacity() > 0) {
                new FadeOut(delResultsPane).play();
                delResultsPane.setDisable(true);
            }
        } else {
            Image image;
            if (product.getImage() == null) {
                image = new Image(getClass().getResourceAsStream(INPUT_STREAM_TO_NO_IMAGE));
            } else {
                try {
                    byte[] decodedImage = Base64.getDecoder().decode(product.getImage());
                    File productImageFile = new File(FileUtils.getTempDirectoryPath().concat("\\_brownTech\\_gallery\\").concat(RandomStringUtils.randomAlphabetic(10)).concat(".png"));
                    FileUtils.writeByteArrayToFile(productImageFile, decodedImage);
                    image = new Image(new FileInputStream(productImageFile));
                    productImageFile.deleteOnExit();
                } catch (Exception e) {
                    image = new Image(getClass().getResourceAsStream(INPUT_STREAM_TO_NO_IMAGE));
                }
            }
            view_image_with_dropShadow_effect(image, delProdImageView);
            delProdNameLbl.setText(product.getName());
            delDescriptionLbl.setText(product.getDescription());
            delRatingsImageView.setImage(get_image_of_ratings(product.getRating()));
            delStockQuantityLbl.setText(String.format("%d", product.getStock()));
            delBuyingPriceLbl.setText("Ksh ".concat(String.format("%,.1f", product.getBuyingPrice())));
            delMarkedPriceLbl.setText("Ksh ".concat(String.format("%,.1f", product.getMarkedPrice())));
            if (delSearchResultLbl.getOpacity() > 0) {
                new FadeOut(delSearchResultLbl).play();
                delResultsPane.toFront();
                new FadeIn(delResultsPane).play();
            }
            if (delResultsPane.getOpacity() < 1) {
                new FadeIn(delResultsPane).play();
                delResultsPane.setDisable(false);
            }
        }
        this.productToDelete = product;
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    private Task<Object> reset_update_stock_item_pane_for_the_next_one() {
        return new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                Platform.runLater(() -> no_image_for_product(prodImageView1));
                Thread.sleep(500);

                Platform.runLater(() -> newProdSerialTF.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> newProdQtyTF.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> newProdStarsCbx.getSelectionModel().clearSelection());
                Thread.sleep(500);

                Platform.runLater(() -> newProdNameTF.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> newDescriptionTA.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> newBuyingPriceTF.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> newMarkedPriceTF.setText(""));
                return null;
            }
        };
    }

    private String look_for_image(final ImageView imageView) {
        String nameOfNewImage = INPUT_STREAM_TO_NO_IMAGE;
        try {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG only", "*.png"));
            final File SOURCE_FILE = fileChooser.showOpenDialog(Main.stage);
            if (SOURCE_FILE == null) {
                error_message("No image selected", "Try again..").show();
            } else {
                final File destination_folder = new File(Main.RESOURCE_PATH.getAbsolutePath().concat("\\_gallery\\"));
                try {
                    FileUtils.copyFileToDirectory(SOURCE_FILE, destination_folder);
                    nameOfNewImage = destination_folder.getAbsolutePath().concat("\\".concat(SOURCE_FILE.getName()));
                    imageView.setImage(new Image(new FileInputStream(destination_folder.getAbsolutePath().concat("\\".concat(SOURCE_FILE.getName())))));
                } catch (IOException e) {
                    if (e.getLocalizedMessage().contains("are the same")) {
                        warning_message("Duplicate image id found", "The image you have selected already exists in the my gallery.").show();
                        imageView.setImage(new Image(new FileInputStream(destination_folder.getAbsolutePath().concat("\\".concat(SOURCE_FILE.getName())))));
                    } else {
                        imageView.setImage(new Image(getClass().getResourceAsStream(INPUT_STREAM_TO_NO_IMAGE)));
                        e.printStackTrace();
                        programmer_error(e).show();
                        new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                        new Thread(stack_trace_printing(e)).start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            programmer_error(e).show();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
        }
        return nameOfNewImage;
    }

    private void display_old_details_of_an_item(Product product) {
        if (product == null) {
            oldProdImageView.setEffect(null);
            no_image_for_product(oldProdImageView);
            oldProdNameLbl.setText("");
            oldDescriptionLbl.setText("");
            oldRatingsImageView.setImage(null);
            oldStockQuantityLbl.setText("");
            oldBuyingPriceLbl.setText("");
            oldMarkedPriceLbl.setText("");
            if (updateResultsPane.getOpacity() > 0) {
                new FadeOut(updateResultsPane).play();
                updateSearchResultLbl.toFront();
                new FadeIn(updateSearchResultLbl).play();
            }
            new RubberBand(updateSearchResultLbl).play();
            if (updateDetailsPane.getOpacity() > 0) {
                new FadeOutRight(updateDetailsPane).play();
                updateDetailsPane.setDisable(true);
            }
        } else {
            Image image;
            if (product.getImage() == null) {
                image = new Image(getClass().getResourceAsStream(INPUT_STREAM_TO_NO_IMAGE));
            } else {
                try {
                    byte[] decodedImage = Base64.getDecoder().decode(product.getImage());
                    File productImageFile = new File(FileUtils.getTempDirectoryPath().concat("\\_brownTech\\_gallery\\").concat(RandomStringUtils.randomAlphabetic(10)).concat(".png"));
                    FileUtils.writeByteArrayToFile(productImageFile, decodedImage);
                    image = new Image(new FileInputStream(productImageFile));
                    productImageFile.deleteOnExit();
                } catch (Exception e) {
                    image = new Image(getClass().getResourceAsStream(INPUT_STREAM_TO_NO_IMAGE));
                }
            }
            view_image_with_dropShadow_effect(image, oldProdImageView);
            oldProdNameLbl.setText(product.getName());
            oldDescriptionLbl.setText(product.getDescription());
            oldRatingsImageView.setImage(get_image_of_ratings(product.getRating()));
            oldStockQuantityLbl.setText(String.format("%d", product.getStock()));
            oldBuyingPriceLbl.setText("Ksh ".concat(String.format("%,.1f", product.getBuyingPrice())));
            oldMarkedPriceLbl.setText("Ksh ".concat(String.format("%,.1f", product.getMarkedPrice())));
            if (updateSearchResultLbl.getOpacity() > 0) {
                new FadeOut(updateSearchResultLbl).play();
                updateResultsPane.toFront();
                new FadeIn(updateResultsPane).play();
            }
            if (updateDetailsPane.getOpacity() < 1) {
                new FadeInRight(updateDetailsPane).play();
                updateDetailsPane.setDisable(false);
            }
        }
        this.myProduct = product;
    }

    @NotNull
    @Contract("_ -> new")
    private Image get_image_of_ratings(int count) {
        switch (count) {
            case 1:
                return new Image("/org/_brown_tech/_images/_icons/1_star.png");
            case 2:
                return new Image("/org/_brown_tech/_images/_icons/2_star.png");
            case 3:
                return new Image("/org/_brown_tech/_images/_icons/3_star.png");
            case 5:
                return new Image("/org/_brown_tech/_images/_icons/5_star.png");
            case 4:
            default:
                return new Image("/org/_brown_tech/_images/_icons/4_star.png");
        }
    }

    private void view_image_with_dropShadow_effect(Image image, @NotNull ImageView imageView) {
        imageView.setImage(image);
        final Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(5);
        clip.setArcHeight(5);
        imageView.setClip(clip);
        final SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        final WritableImage writableImage = imageView.snapshot(parameters, null);
        imageView.setClip(null);
        imageView.setEffect(new DropShadow(10.0, Color.rgb(0, 0, 0, 0.22)));
        imageView.setImage(writableImage);
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    private Task<Object> reset_add_stock_item_pane_for_the_next_one() {
        return new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                Platform.runLater(() -> no_image_for_product(prodView));
                Thread.sleep(500);

                Platform.runLater(() -> prodSerialTF.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> prodQtyTF.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> prodRatingsCbx.getSelectionModel().clearSelection());
                Thread.sleep(500);

                Platform.runLater(() -> prodNameTF.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> prodDescriptionTF.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> prodMarkedPriceTF.setText(""));
                Thread.sleep(500);

                Platform.runLater(() -> prodBuyingPriceTF.setText(""));
                return null;
            }
        };
    }

    private void no_image_for_product(@NotNull ImageView imageView) {
        PATH_TO_IMAGE_OF_NEW_PRODUCT = INPUT_STREAM_TO_NO_IMAGE;
        imageView.setImage(new Image(getClass().getResourceAsStream(PATH_TO_IMAGE_OF_NEW_PRODUCT)));
    }

    @NotNull
    private Boolean email_is_in_correct_format(String param) {
        return Pattern.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", param);
    }

    private void make_a_textField_numeric(@NotNull JFXTextField[] jfxTextFields) {
        for (JFXTextField jfxTextField : jfxTextFields) {
            TextFormatter<Integer> textFormatter = new TextFormatter<>(converter, null, integerFilter);
            jfxTextField.setTextFormatter(textFormatter);
        }
    }

    private void setup_stock_pane() {
        prodView.setImage(new Image(getClass().getResourceAsStream(INPUT_STREAM_TO_NO_IMAGE)));
        Platform.runLater(() -> display_stock_items_based_on_observableList_param(get_stock_items_based_on_param("#all")));
        if (!stockSearchSuggestions.isEmpty()) {
            stockSearchSuggestions.clear();
        }
        if (!stockSerials.isEmpty()) {
            stockSerials.clear();
        }
        stockSerials.addAll(get_product_serials());
        stockSearchSuggestions.addAll(stockSerials);
        stockSearchSuggestions.addAll(get_stock_suggestions());
        update_autoComplete_suggestions(stockSuggestionsPopup, stockSearchSuggestions);
        stockParameterTF.setText("");
        update_autoComplete_suggestions(badStockSerialSuggestionsPopup, stockSerials);
        update_autoComplete_suggestions(searchStockSerialForUpdateSuggestionsPopup, stockSerials);
        update_autoComplete_suggestions(searchStockSerialForUpdateSuggestionsPopup1, stockSerials);
        update_autoComplete_suggestions(searchStockSerialForDeleteSuggestionsPopup, stockSerials);
    }

    private void update_autoComplete_suggestions(@NotNull JFXAutoCompletePopup<String> jfxAutoCompletePopup, ObservableList<String> observableList) {
        if (jfxAutoCompletePopup.getSuggestions().size() > 0) {
            jfxAutoCompletePopup.getSuggestions().clear();
        }
        jfxAutoCompletePopup.getSuggestions().addAll(observableList);
    }

    public void assign_text_fields_suggestions_popups(@NotNull JFXTextField jfxTextField, @NotNull JFXAutoCompletePopup<String> jfxAutoCompletePopup) {
        jfxAutoCompletePopup.setMinSize(400, 400);
        jfxAutoCompletePopup.setSelectionHandler(event -> {
            jfxTextField.setText(event.getObject());
            if (jfxTextField.equals(stockParameterTF)) {
                Platform.runLater(() -> display_stock_items_based_on_observableList_param(get_stock_items_based_on_param(jfxTextField.getText().trim())));
            } else if (jfxTextField.equals(prodSerialTF)) {
                if (stockSerials.contains(jfxTextField.getText().trim())) {
                    error_message("Serial number is known!", "The serial already exists, enter a new one").show();
                }
            } else if (jfxTextField.equals(prodCodeUpParTF)) {
                if (!jfxTextField.getText().trim().isEmpty() || !jfxTextField.getText().isEmpty() || jfxTextField.getText() != null) {
                    display_old_details_of_an_item(get_specific_product(jfxTextField.getText().trim()));
                }
            } else if (jfxTextField.equals(prodCodeDelParTF)) {
                if (!jfxTextField.getText().trim().isEmpty() || !jfxTextField.getText().isEmpty() || jfxTextField.getText() != null) {
                    display_current_details_of_an_item(get_specific_product(jfxTextField.getText().trim()));
                }
            } else if (jfxTextField.equals(rcptNoParamTF)) {
                if (!jfxTextField.getText().trim().isEmpty() || !jfxTextField.getText().isEmpty() || jfxTextField.getText() != null) {
                    display_receipts_based_on_observableList_param(get_receipts_based_on_param(jfxTextField.getText().trim()));
                }
            } else if (jfxTextField.equals(accountParamTF)) {
                if (!jfxTextField.getText().trim().isEmpty() || !jfxTextField.getText().isEmpty() || jfxTextField.getText() != null) {
                    display_users_based_on_observableList_param(get_staff_users_based_on_param(jfxTextField.getText().trim()));
                }
            } else if (jfxTextField.equals(newUsernameTF)) {
                if (listOfIn_ActiveStaffUsername.contains(jfxTextField.getText().trim()) || listOfActiveStaffUsername.contains(jfxTextField.getText().trim()) || myAccount.getUsername().equals(jfxTextField.getText().trim())) {
                    warning_message("Taken!", "The username you have selected is already taken, enter a new one").show();
                }
            }
        });
        jfxTextField.textProperty().addListener(observable -> {
            jfxAutoCompletePopup.filter(string -> string.toLowerCase().contains(jfxTextField.getText().toLowerCase()));
            if (jfxAutoCompletePopup.getFilteredSuggestions().isEmpty() || jfxTextField.getText().isEmpty()) {
                Platform.runLater(jfxAutoCompletePopup::hide);
            } else {
                Platform.runLater(() -> jfxAutoCompletePopup.show(jfxTextField));
            }
        });
    }

    private void display_stock_items_based_on_observableList_param(ObservableList<StockModel> stockModelObservableList) {
        final JFXTreeTableView<StockModel> jfxTreeTableView = stockTable;
        prodCodeCol.setCellValueFactory(param -> param.getValue().getValue().serial);
        prodNameCol.setCellValueFactory(param -> param.getValue().getValue().name);
        prodDescriptionCol.setCellValueFactory(param -> param.getValue().getValue().description);
        prodCountCol.setCellValueFactory(param -> param.getValue().getValue().quantity);
        prodRatingsCol.setCellValueFactory(param -> param.getValue().getValue().imageView);
        prodBuyPriceCol.setCellValueFactory(param -> param.getValue().getValue().buyingPrice);
        prodMarkedPriceCol.setCellValueFactory(param -> param.getValue().getValue().markedPrice);
        prodStatusCol.setCellValueFactory(param -> param.getValue().getValue().status);
        final TreeItem<StockModel> root = new RecursiveTreeItem<>(stockModelObservableList, RecursiveTreeObject::getChildren);
        jfxTreeTableView.setRoot(root);
        jfxTreeTableView.setShowRoot(false);
        if (!stockModelObservableList.isEmpty()) {
            jfxTreeTableView.refresh();
        }
    }

    private void setup_Dashboard() {
        final String dateToday = new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
        final JsonObject jsonObject = get_dashboard_values(dateToday);
        final double productTotal = jsonObject.get("productReturns").getAsDouble();
        final double serviceTotal = jsonObject.get("serviceReturns").getAsDouble();
        final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        pieChartData.addAll(new PieChart.Data("Product", productTotal), new PieChart.Data("Services", serviceTotal));
        productVersusServicesPie.setData(pieChartData);
        returnsSumLbl.setText("Ksh ".concat(String.format("%,.1f", (productTotal + serviceTotal))));
    }

    private void format_datePickers_to_show_my_preferred_date_style(@NotNull JFXDatePicker jfxDatePicker) {
        final StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            @NotNull
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateTimeFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateTimeFormatter);
                } else {
                    return null;
                }
            }
        };
        jfxDatePicker.setConverter(converter);
    }

    private void show_hide_password(@NotNull MaterialDesignIconView materialDesignIconView, JFXTextField textField, JFXPasswordField passwordField) {
        if (materialDesignIconView.getGlyphName().equalsIgnoreCase("EYE")) {
            if (!(passwordField.getText().trim().isEmpty() || passwordField.getText() == null)) {
                textField.setText(passwordField.getText());
            }
            new FadeOut(passwordField).play();
            new FadeIn(textField).play();
            textField.toFront();
            textField.requestFocus();
            materialDesignIconView.setGlyphName("EYE_OFF");
        } else {
            if (!(textField.getText().trim().isEmpty() || textField.getText() == null)) {
                passwordField.setText(textField.getText());
            }
            new FadeOut(textField).play();
            new FadeIn(passwordField).play();
            passwordField.toFront();
            passwordField.requestFocus();
            materialDesignIconView.setGlyphName("EYE");
        }
    }

    private void animate_show_statistics(JFXButton jfxButton) {
        if (statisticsPane.getOpacity() < 1) {
            for (int index = 1; index <= 6; ++index) {
                if (index != 5) {
                    if (desk_stackPanes.get(index).getOpacity() > 0) {
                        new FadeOut(desk_stackPanes.get(index)).play();
                    }
                } else {
                    desk_stackPanes.get(index).toFront();

                    new FadeIn(desk_stackPanes.get(index)).setDelay(Duration.seconds(0.5)).play();
                    topBarPane.setDisable(true);
                    for (int inner_index = 1; inner_index <= 4; ++inner_index) {
                        if (sub_menu_anchorPanes.get(inner_index).getOpacity() > 0) {
                            new FadeOut(sub_menu_anchorPanes.get(inner_index)).play();
                        }
                    }
                }
            }
            set_background_color_for_active_buttons(jfxButton);
        }
    }

    private void animate_show_payments(JFXButton jfxButton) {

        if (paymentsPane.getOpacity() < 1) {
            for (int index = 1; index <= 6; ++index) {
                if (index != 4) {
                    if (desk_stackPanes.get(index).getOpacity() > 0) {
                        new FadeOut(desk_stackPanes.get(index)).play();
                    }
                } else {
                    desk_stackPanes.get(index).toFront();

                    new FadeIn(desk_stackPanes.get(index)).setDelay(Duration.seconds(0.5)).play();
                    topBarPane.setDisable(false);
                    for (int inner_index = 1; inner_index <= 4; ++inner_index) {
                        if (inner_index != 3) {
                            if (sub_menu_anchorPanes.get(inner_index).getOpacity() > 0) {
                                new SlideOutRight(sub_menu_anchorPanes.get(inner_index)).play();
                                sub_menu_anchorPanes.get(inner_index).setOpacity(0);
                            }
                        } else {
                            if (sub_menu_anchorPanes.get(inner_index).getOpacity() < 1) {
                                sub_menu_anchorPanes.get(inner_index).toFront();
                                sub_menu_anchorPanes.get(inner_index).setOpacity(1);
                                new SlideInLeft(sub_menu_anchorPanes.get(inner_index)).play();
                            }
                        }
                    }
                }
            }
            set_background_color_for_active_buttons(jfxButton);
        }
    }

    private void animate_show_purchases(JFXButton jfxButton) {

        if (purchasesPane.getOpacity() < 1) {
            for (int index = 1; index <= 6; ++index) {
                if (index != 3) {
                    if (desk_stackPanes.get(index).getOpacity() > 0) {
                        new FadeOut(desk_stackPanes.get(index)).play();
                    }
                } else {
                    desk_stackPanes.get(index).toFront();

                    new FadeIn(desk_stackPanes.get(index)).setDelay(Duration.seconds(0.5)).play();
                    topBarPane.setDisable(false);
                    for (int inner_index = 1; inner_index <= 4; ++inner_index) {
                        if (inner_index != 2) {
                            if (sub_menu_anchorPanes.get(inner_index).getOpacity() > 0) {
                                new SlideOutRight(sub_menu_anchorPanes.get(inner_index)).play();
                                sub_menu_anchorPanes.get(inner_index).setOpacity(0);
                            }
                        } else {
                            if (sub_menu_anchorPanes.get(inner_index).getOpacity() < 1) {
                                sub_menu_anchorPanes.get(inner_index).toFront();
                                sub_menu_anchorPanes.get(inner_index).setOpacity(1);
                                new SlideInLeft(sub_menu_anchorPanes.get(inner_index)).play();
                            }
                        }
                    }
                }
            }
            set_background_color_for_active_buttons(jfxButton);
        }
    }

    private void animate_show_stock(JFXButton jfxButton) {

        if (stockPane.getOpacity() < 1) {
            for (int index = 1; index <= 6; ++index) {
                if (index != 2) {
                    if (desk_stackPanes.get(index).getOpacity() > 0) {
                        new FadeOut(desk_stackPanes.get(index)).play();
                    }
                } else {
                    desk_stackPanes.get(index).toFront();
                    new FadeIn(desk_stackPanes.get(index)).setDelay(Duration.seconds(0.5)).play();
                    topBarPane.setDisable(false);
                    for (int inner_index = 1; inner_index <= 4; ++inner_index) {
                        if (inner_index != 1) {
                            if (sub_menu_anchorPanes.get(inner_index).getOpacity() > 0) {
                                new SlideOutRight(sub_menu_anchorPanes.get(inner_index)).play();
                                sub_menu_anchorPanes.get(inner_index).setOpacity(0);
                            }
                        } else {
                            if (sub_menu_anchorPanes.get(inner_index).getOpacity() < 1) {
                                sub_menu_anchorPanes.get(inner_index).toFront();
                                sub_menu_anchorPanes.get(inner_index).setOpacity(1);
                                new SlideInLeft(sub_menu_anchorPanes.get(inner_index)).play();
                            }
                        }
                    }
                }
            }
            set_background_color_for_active_buttons(jfxButton);
        }
    }

    private void animate_show_dashboard(JFXButton jfxButton) {

        if (dashBoardPane.getOpacity() < 1) {
            for (int index = 1; index <= 6; ++index) {
                if (index != 1) {
                    if (desk_stackPanes.get(index).getOpacity() > 0) {
                        new FadeOut(desk_stackPanes.get(index)).play();
                    }
                } else {
                    desk_stackPanes.get(index).toFront();
                    new FadeIn(desk_stackPanes.get(index)).setDelay(Duration.seconds(0.5)).play();
                    for (int inner_index = 1; inner_index <= 4; ++inner_index) {
                        if (sub_menu_anchorPanes.get(inner_index).getOpacity() > 0) {
                            new FadeOut(sub_menu_anchorPanes.get(inner_index)).play();
                        }
                    }
                    topBarPane.setDisable(true);
                }
            }
            set_background_color_for_active_buttons(jfxButton);
        }
    }

    @NotNull
    private Boolean animate_show_dashboard() {
        if (dashBoardPane.getOpacity() < 1) {
            for (int index = 1; index <= 6; ++index) {
                if (index != 1) {
                    if (desk_stackPanes.get(index).getOpacity() > 0) {
                        new FadeOut(desk_stackPanes.get(index)).play();
                    }
                } else {
                    desk_stackPanes.get(index).toFront();
                    new FadeIn(desk_stackPanes.get(index)).setDelay(Duration.seconds(0.5)).play();
                    for (int inner_index = 1; inner_index <= 4; ++inner_index) {
                        if (sub_menu_anchorPanes.get(inner_index).getOpacity() > 0) {
                            new FadeOut(sub_menu_anchorPanes.get(inner_index)).play();
                        }
                    }
                    topBarPane.setDisable(true);
                }
            }
        }
        return true;
    }

    private void arrange_nodes_in_their_appropriate_hashmap() {
        assign_hashmap_for_deskPanes();
        assign_hashmap_for_sub_menu_items_panes();
        assign_hashmap_for_inner_stock_panes();
        assign_hashmap_for_inner_purchases();
        assign_hashmap_for_inner_payments();
        assign_hashmap_for_inner_accounts();

        assign_hashmap_for_stock_subMenu_underlines();
        assign_hashmap_for_purchases_subMenu_underlines();
        assign_hashmap_for_payments_subMenu_underlines();
        assign_hashmap_for_accounts_subMenu_underlines();
    }

    private void style_clicked_sub_menu_items(int selectedIndex, JFXButton selectedButton, @NotNull HashMap<Integer, AnchorPane> under_lines) {
        for (int index = 1; index <= under_lines.size(); ++index) {
            if (index != selectedIndex) {
                under_lines.get(index).setStyle("-fx-background-color: transparent;");
                ObservableList<Node> vBoxNodes = ((VBox) under_lines.get(index).getParent()).getChildren();
                for (Node node : vBoxNodes) {
                    if (node.getClass().equals(JFXButton.class)) {
                        node.setStyle("-fx-text-fill: rgb(0, 0, 0);");
                    }
                }
            }
        }
        selectedButton.setStyle("-fx-text-fill: #FFFFFF;" +
                "-fx-background-radius: 0px;");
        under_lines.get(selectedIndex).setStyle("-fx-background-color: #FFFFFF;");
    }

    private void assign_hashmap_for_accounts_subMenu_underlines() {
        accounts_underline.put(1, accountsViewLine);
        accounts_underline.put(2, addUserLine);
        accounts_underline.put(3, editUserLine);
        accounts_underline.put(4, removeUserLine);
    }

    private void assign_hashmap_for_payments_subMenu_underlines() {
        payments_underline.put(1, paySummaryLine);
        payments_underline.put(2, chequeLine);
    }

    private void assign_hashmap_for_purchases_subMenu_underlines() {
        purchases_underline.put(1, summaryLine);
        purchases_underline.put(2, receiptLine);
    }

    private void assign_hashmap_for_stock_subMenu_underlines() {
        stock_underline.put(1, viewLine);
        stock_underline.put(2, addLine);
        stock_underline.put(3, updateLine);
        stock_underline.put(4, deleteLine);
    }

    private void assign_hashmap_for_inner_accounts() {
        accounts_leaflets.put(1, accountsViewPane);
        accounts_leaflets.put(2, accountsAddPane);
        accounts_leaflets.put(3, accountsEditPane);
        accounts_leaflets.put(4, accountsRemovePane);
    }

    private void assign_hashmap_for_inner_payments() {
        payments_leaflets.put(1, paymentsSummaryPane);
        payments_leaflets.put(2, paymentsChequePane);
    }

    private void assign_hashmap_for_inner_purchases() {
        purchases_leaflets.put(1, purchaseSummaryPane);
        purchases_leaflets.put(2, purchaseReceiptsPane);
    }

    private void assign_hashmap_for_inner_stock_panes() {
        stock_leaflets.put(1, stockViewPane);
        stock_leaflets.put(2, stockaddPane);
        stock_leaflets.put(3, stockUpdatePane);
        stock_leaflets.put(4, stockDeletePane);
    }

    @NotNull
    private Boolean animate_show_accounts(JFXButton jfxButton) {
        boolean animation_has_occurred = false;
        if (accountsPane.getOpacity() < 1) {
            for (int index = 1; index <= 6; ++index) {
                if (index != 6) {
                    if (desk_stackPanes.get(index).getOpacity() > 0) {
                        new FadeOut(desk_stackPanes.get(index)).play();
                    }
                } else {
                    desk_stackPanes.get(index).toFront();

                    new FadeIn(desk_stackPanes.get(index)).setDelay(Duration.seconds(0.5)).play();
                    topBarPane.setDisable(false);
                    for (int inner_index = 1; inner_index <= 4; ++inner_index) {
                        if (inner_index != 4) {
                            if (sub_menu_anchorPanes.get(inner_index).getOpacity() > 0) {
                                new SlideOutRight(sub_menu_anchorPanes.get(inner_index)).play();
                                sub_menu_anchorPanes.get(inner_index).setOpacity(0);
                            }
                        } else {
                            if (sub_menu_anchorPanes.get(inner_index).getOpacity() < 1) {
                                sub_menu_anchorPanes.get(inner_index).toFront();
                                sub_menu_anchorPanes.get(inner_index).setOpacity(1);
                                new SlideInLeft(sub_menu_anchorPanes.get(inner_index)).play();
                            }
                        }
                    }
                }
            }
            set_background_color_for_active_buttons(jfxButton);
            animation_has_occurred = true;
        }
        return animation_has_occurred;
    }

    private void set_background_color_for_active_buttons(JFXButton jfxButton) {
        if (jfxButton != null) {
            ObservableList<Node> nodeObservableArray = ((VBox) jfxButton.getParent()).getChildren();
            for (Node node : nodeObservableArray) {
                if (node.equals(jfxButton)) {
                    jfxButton.getGraphic().setStyle("-fx-fill : rgb(142, 75, 2);");
                    node.setStyle("-fx-background-radius: 0px;" +
                            "-fx-background-color: linear-gradient(#FFB900, #F0D801);" +
                            "-fx-text-fill: rgb(142, 75, 2);");
                } else {
                    if (node.getClass().equals(JFXButton.class)) {
                        final JFXButton jfxButton1 = (JFXButton) node;
                        jfxButton1.getGraphic().setStyle("-fx-fill : rgb(134, 134, 134);");
                        node.setStyle("-fx-background-radius: 0px;" +
                                "-fx-background-color: transparent;" +
                                "-fx-text-fill: rgb(134, 134, 134);");
                    }
                }
            }
        }
    }

    private void assign_hashmap_for_deskPanes() {
        desk_stackPanes.put(1, dashBoardPane);
        desk_stackPanes.put(2, stockPane);
        desk_stackPanes.put(3, purchasesPane);
        desk_stackPanes.put(4, paymentsPane);
        desk_stackPanes.put(5, statisticsPane);
        desk_stackPanes.put(6, accountsPane);
    }

    private void assign_hashmap_for_sub_menu_items_panes() {
        sub_menu_anchorPanes.put(1, stockSubMenuItemsPane);
        sub_menu_anchorPanes.put(2, purchasesSubMenuItemsPane);
        sub_menu_anchorPanes.put(3, paymentsSubMenuItemsPane);
        sub_menu_anchorPanes.put(4, accountsSubMenuItemsPane);
    }

}
