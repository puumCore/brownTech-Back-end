package org._brown_tech._custom;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org._brown_tech.Main;
import org._brown_tech._object.AccountObj;
import org._brown_tech._object.ChequeObj;
import org._brown_tech._object.Product;
import org._brown_tech._object.User;
import org._brown_tech._outsourced.BCrypt;
import org._brown_tech._table_model.Account;
import org._brown_tech._table_model.Purchase;
import org._brown_tech._table_model.Receipt;
import org._brown_tech._table_model.Stock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteDataSource;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author Mandela
 */
public class Brain {

    private final SQLiteDataSource ds = new SQLiteDataSource();
    public final String INPUT_STREAM_TO_NO_IMAGE = "/org/_brown_tech/_images/_pictures/noimage.png";
    public static String LOGGED_IN_USER = null;

    protected Connection connect_to_memory() {
        try {
            ds.setUrl("jdbc:sqlite:".concat(Main.RESOURCE_PATH.getAbsolutePath().concat("\\_dataSource\\bt_dataStore.db3")));
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            new Issues().programmer_error(e).show();
            return null;
        }
    }

    protected Boolean approve_cheque(String dateTimeAsSerial) {
        boolean userHasBeenApproved = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _cheque\n" +
                    "   SET hasMatured = true\n" +
                    " WHERE dateTime = ''  '" + dateTimeAsSerial + "'");
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                userHasBeenApproved = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return userHasBeenApproved;
    }

    protected Boolean make_user_account_inactive_or_not(String username, boolean status) {
        boolean userHasBeenDeactivated = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _user\n" +
                    "   SET isActive = " + status + "\n" +
                    " WHERE username =  '" + username + "'");
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                userHasBeenDeactivated = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return userHasBeenDeactivated;
    }

    protected ObservableList<User> get_all_users() {
        final ObservableList<User> userObservableList = FXCollections.observableArrayList();
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT username,\n" +
                    "       (firstname || ' ' || surname) AS fullname,\n" +
                    "       isAdmin,\n" +
                    "       isActive\n" +
                    "  FROM _user");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    userObservableList.add(
                            new User(
                                    resultSet.getString(1),
                                    resultSet.getString(2),
                                    resultSet.getBoolean(3),
                                    resultSet.getBoolean(4)
                            )
                    );
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return userObservableList;
    }

    protected final Boolean update_account_siri(final String username, final String siri) {
        boolean allIsGood = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _user\n" +
                    "   SET password = ?\n" +
                    " WHERE isActive = true AND \n" +
                    "       username = '" + username + "'");
            preparedStatement.setString(1, siri);
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                allIsGood = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return allIsGood;
    }

    protected final Boolean update_account_email(final String username, final String email) {
        boolean allIsGood = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _user\n" +
                    "   SET email = ?\n" +
                    " WHERE isActive = true AND \n" +
                    "       username = '" + username + "'");
            preparedStatement.setString(1, email);
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                allIsGood = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return allIsGood;
    }

    protected final Boolean update_account_surname(final String username, final String surname) {
        boolean allIsGood = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _user\n" +
                    "   SET surname = ?\n" +
                    " WHERE isActive = true AND \n" +
                    "       username = '" + username + "'");
            preparedStatement.setString(1, surname);
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                allIsGood = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return allIsGood;
    }

    protected final Boolean update_account_firstname(final String username, final String firstname) {
        boolean allIsGood = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _user\n" +
                    "   SET firstname = ?\n" +
                    " WHERE isActive = true AND \n" +
                    "       username = '" + username + "'");
            preparedStatement.setString(1, firstname);
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                allIsGood = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return allIsGood;
    }

    protected final Boolean update_account_username(final String oldOne, final String newOne) {
        boolean allIsGood = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _user\n" +
                    "   SET username = ?\n" +
                    " WHERE isActive = true AND \n" +
                    "       username = '" + oldOne + "'");
            preparedStatement.setString(1, newOne);
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                allIsGood = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return allIsGood;
    }

    protected Boolean add_staff_new_account(@NotNull final AccountObj accountObj) {
        boolean accountHasBennCreated = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO _user (\n" +
                    "                      isAdmin,\n" +
                    "                      email,\n" +
                    "                      password,\n" +
                    "                      surname,\n" +
                    "                      firstname,\n" +
                    "                      username\n" +
                    "                  )\n" +
                    "                  VALUES (\n" +
                    "                      ?,\n" +
                    "                      ?,\n" +
                    "                      ?,\n" +
                    "                      ?,\n" +
                    "                      ?,\n" +
                    "                      ?\n" +
                    "                  )");
            preparedStatement.setBoolean(1, accountObj.getAdmin());
            preparedStatement.setString(2, accountObj.getEmail());
            preparedStatement.setString(3, accountObj.getSiri());
            preparedStatement.setString(4, accountObj.getSurname());
            preparedStatement.setString(5, accountObj.getFirstname());
            preparedStatement.setString(6, accountObj.getUsername());
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                accountHasBennCreated = true;
            }
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return accountHasBennCreated;
    }

    protected ObservableList<String> get_username_staff_based_on_employment_status(boolean status) {
        final ObservableList<String> stringObservableList = FXCollections.observableArrayList();
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT username\n" +
                    "  FROM _user\n" +
                    " WHERE isAdmin = false AND \n" +
                    "       isActive = " + status);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    stringObservableList.add(resultSet.getString(1));
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return stringObservableList;
    }

    protected ObservableList<Account> get_staff_users_based_on_param(@NotNull String param) {
        final ObservableList<Account> accountObservableList = FXCollections.observableArrayList();
        final String ROOT_QUERY = "SELECT username,\n" +
                "       firstname,\n" +
                "       surname,\n" +
                "       email\n" +
                "  FROM _user\n" +
                " WHERE isAdmin = false AND \n" +
                "       isActive = true";
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement;
            if (param.isEmpty()) {
                preparedStatement = connection.prepareStatement(ROOT_QUERY);
            } else {
                preparedStatement = connection.prepareStatement(ROOT_QUERY.concat(" AND \n" +
                        "       (username LIKE '%" + param + "%')"));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    accountObservableList.add(
                            new Account(
                                    resultSet.getString(1),
                                    resultSet.getString(2),
                                    resultSet.getString(3),
                                    resultSet.getString(4))
                    );
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return accountObservableList;
    }

    @NotNull
    protected HashMap<String, Double> load_profit_and_loss_trend_across_months_of_a_requested_year(@NotNull String parameters) {
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        final HashMap<String, Double> line_graph_data = new HashMap<>();
        try {
            for (String month_name : months) {
                Connection connection = connect_to_memory();
                String search_parameters = month_name + "-" + parameters;
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(buyPrice) \n" +
                        "  FROM _receipt WHERE (dateTime LIKE '%" + search_parameters + "')");
                ResultSet resultSet = preparedStatement.executeQuery();
                double purchases = 0.0;
                if (resultSet.next()) {
                    purchases = resultSet.getDouble(1);
                }
                resultSet.close();
                preparedStatement.close();
                connection.close();
                final double grossProfit = get_sum_of_real_sales(search_parameters) - purchases;
                line_graph_data.put(month_name, grossProfit);
            }
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return line_graph_data;
    }

    private double get_sum_of_real_sales(@NotNull String date) {
        double result = get_sum_of_sales(date);
        result -= unApproved_cheques(date);
        return result;
    }

    private double get_sum_of_sales(@NotNull String date) {
        double totalAmount = 0.0;
        final String ROOT_QUERY = "SELECT SUM(billAmount) \n" +
                "  FROM _sale";
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement;
            if (date.isEmpty()) {
                preparedStatement = connection.prepareStatement(ROOT_QUERY);
            } else {
                preparedStatement = connection.prepareStatement(ROOT_QUERY.concat(" WHERE (dateTime LIKE '%" + date + "')"));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalAmount += resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return totalAmount;
    }

    private Double unApproved_cheques(@NotNull String param) {
        double totalAmount = 0.0;
        final String ROOT_QUERY = "SELECT SUM(amount) \n" +
                "  FROM _cheque\n" +
                " WHERE hasMatured = false";
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement;
            if (param.isEmpty()) {
                preparedStatement = connection.prepareStatement(ROOT_QUERY);
            } else {
                preparedStatement = connection.prepareStatement(ROOT_QUERY.concat(" AND \n" +
                        "       (dateTime LIKE '%" + param + "%')"));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalAmount += resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return totalAmount;
    }

    protected List<ChequeObj> get_pending_cheques() {
        final List<ChequeObj> chequeObjList = new ArrayList<>();
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT dateTime,\n" +
                    "       receiptNum,\n" +
                    "       chequeNo,\n" +
                    "       drawerName,\n" +
                    "       maturityDate,\n" +
                    "       bank,\n" +
                    "       amount\n" +
                    "  FROM _cheque\n" +
                    " WHERE hasMatured = false");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    final ChequeObj chequeObj = new ChequeObj();
                    chequeObj.setDateTime(resultSet.getString(1));
                    chequeObj.setReceiptNumber(resultSet.getString(2));
                    chequeObj.setChequeNo(resultSet.getString(3));
                    chequeObj.setDrawerName(resultSet.getString(4));
                    chequeObj.setMaturityDate(resultSet.getString(5));
                    chequeObj.setBankName(resultSet.getString(6));
                    chequeObj.setAmount("Ksh ".concat(String.format("%,.1f", resultSet.getDouble(7))));
                    chequeObjList.add(chequeObj);
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return chequeObjList;
    }

    protected Double get_amount_of_cheques_that_have_being_approved_or_not(boolean hasMatured) {
        double amount = 0.0;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(amount) \n" +
                    "  FROM _cheque\n" +
                    " WHERE hasMatured = " + hasMatured);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                amount += resultSet.getDouble(1);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return amount;
    }

    protected Integer get_no_of_cheques_that_have_being_approved_or_not(boolean hasMatured) {
        int chequeCount = 0;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(dateTime) \n" +
                    "  FROM _cheque\n" +
                    " WHERE hasMatured = " + hasMatured);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                chequeCount += resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return chequeCount;
    }

    protected Double get_sum_of_all_payments_based_on_type_param(int typeParam) {
        double totalAmount = 0.0;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(billAmount)\n" +
                    "  FROM _sale\n" +
                    " WHERE paymentMethod = " + typeParam);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalAmount += resultSet.getDouble(1);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return totalAmount;
    }

    protected ObservableList<String> get_a_list_of_receipt_numbers() {
        final ObservableList<String> stringObservableList = FXCollections.observableArrayList();
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT receiptNum\n" +
                    "  FROM _receipt");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    stringObservableList.add(resultSet.getString(1));
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return stringObservableList;
    }

    @NotNull
    private String decode_type_of_stock_from_boolean(boolean type) {
        if (type) {
            return "product".toUpperCase();
        } else {
            return "service".toUpperCase();
        }
    }

    protected ObservableList<Receipt> get_receipts_based_on_param(@NotNull String receiptNumber) {
        final ObservableList<Receipt> receiptObservableList = FXCollections.observableArrayList();
        final String ROOT_QUERY = "SELECT receiptNum,\n" +
                "       dateTime,\n" +
                "       serial,\n" +
                "       quantitySold,\n" +
                "       sellingPrice,\n" +
                "       buyPrice,\n" +
                "       isProduct\n" +
                "  FROM _receipt";
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement;
            if (receiptNumber.isEmpty()) {
                preparedStatement = connection.prepareStatement(ROOT_QUERY);
            } else {
                preparedStatement = connection.prepareStatement(ROOT_QUERY.concat("\n" +
                        " WHERE receiptNum = '" + receiptNumber + "'"));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    receiptObservableList.add(new Receipt(resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            String.format("%d", resultSet.getInt(4)),
                            ("Ksh ".concat(String.format("%,.1f", resultSet.getDouble(5)))),
                            ("Ksh ".concat(String.format("%,.1f", resultSet.getDouble(6)))),
                            decode_type_of_stock_from_boolean(resultSet.getBoolean(7))
                    ));
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return receiptObservableList;
    }

    @NotNull
    private String decode_paymentMethod(int type) {
        switch (type) {
            case 0:
                return "cash";
            case 1:
                return "cheque";
            case 2:
                return "m-pesa";
        }
        return String.format("%d", type);
    }

    protected ObservableList<Purchase> get_purchases_based_on_param(@NotNull String param) {
        final ObservableList<Purchase> purchaseObservableList = FXCollections.observableArrayList();
        final String ROOT_QUERY = "SELECT _sale.dateTime,\n" +
                "       _sale.receiptNum,\n" +
                "       _sale.billAmount,\n" +
                "       _sale.paymentMethod,\n" +
                "       (_user.firstname || ' ' || _user.surname) AS fullname\n" +
                "  FROM _sale\n" +
                "       INNER JOIN\n" +
                "       _user ON _user.username = _sale.username";
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement;
            if (param.isEmpty()) {
                preparedStatement = connection.prepareStatement(ROOT_QUERY);
            } else {
                preparedStatement = connection.prepareStatement(ROOT_QUERY.concat(" \n" +
                        " WHERE _sale.dateTime LIKE '%" + param + "'"));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    purchaseObservableList.add(new Purchase(resultSet.getString(1),
                            String.format("%d", resultSet.getInt(2)),
                            ("Ksh ".concat(String.format("%,.1f", resultSet.getDouble(3)))),
                            decode_paymentMethod(resultSet.getInt(4)).toUpperCase(),
                            resultSet.getString(5)));
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return purchaseObservableList;
    }

    protected Boolean delete_product(@NotNull Product product) {
        boolean allIsGood = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _product\n" +
                    "   SET isAvailable = false\n" +
                    " WHERE serial = '" + product.getSerial() + "'");
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                allIsGood = true;
            }
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return allIsGood;
    }

    @NotNull
    protected final ObservableList<String> get_data_from_properties_file_based_on_param(String param) {
        final ObservableList<String> results = FXCollections.observableArrayList();
        if ("banks".equals(param)) {
            try {
                FileReader fileReader = new FileReader(new File(Main.RESOURCE_PATH.getAbsolutePath().concat("\\_config\\banks.properties")));
                Properties properties = new Properties();
                properties.load(fileReader);
                int noOfBanks = Integer.parseInt(properties.getProperty("bankCount"));
                for (int index = 0; index < noOfBanks; ++index) {
                    results.add(properties.getProperty("bank" + index));
                }
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
                new Issues().programmer_error(e).show();
            }
        } else {
            try {
                FileReader fileReader = new FileReader(new File(Main.RESOURCE_PATH.getAbsolutePath().concat("\\_config\\months.properties")));
                Properties properties = new Properties();
                properties.load(fileReader);
                int count = Integer.parseInt(properties.getProperty("monthCount"));
                for (int index = 0; index < count; ++index) {
                    results.add(properties.getProperty("month" + index));
                }
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
                new Issues().programmer_error(e).show();
            }
        }
        return results;
    }

    protected Boolean update_a_stock_item(@NotNull Product product, String imageName, String originalSerial) {
        boolean allIsGood = false;
        try {
            Connection connection = connect_to_memory();
            int executionStatus = 0;
            if (product.getSerial() != null) {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _product\n" +
                        "   SET serial = ?\n" +
                        " WHERE serial = '" + originalSerial + "'");
                preparedStatement.setString(1, product.getSerial());
                executionStatus = preparedStatement.executeUpdate();
                if (executionStatus > 0) {
                    originalSerial = product.getSerial();
                    allIsGood = true;
                }
                preparedStatement.close();
            }
            if (product.getName() != null) {
                allIsGood = false;
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _product\n" +
                        "   SET name = ?\n" +
                        " WHERE serial = '" + originalSerial + "'");
                preparedStatement.setString(1, product.getName());
                executionStatus = preparedStatement.executeUpdate();
                if (executionStatus > 0) {
                    allIsGood = true;
                }
                preparedStatement.close();
            }
            if (product.getDescription() != null) {
                allIsGood = false;
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _product\n" +
                        "   SET description = ?\n" +
                        " WHERE serial = '" + originalSerial + "'");
                preparedStatement.setString(1, product.getDescription());
                executionStatus = preparedStatement.executeUpdate();
                if (executionStatus > 0) {
                    allIsGood = true;
                }
                preparedStatement.close();
            }
            if (product.getStarCount() != null) {
                allIsGood = false;
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _product\n" +
                        "   SET ratings = ?\n" +
                        " WHERE serial = '" + originalSerial + "'");
                preparedStatement.setInt(1, product.getStarCount());
                executionStatus = preparedStatement.executeUpdate();
                if (executionStatus > 0) {
                    allIsGood = true;
                }
                preparedStatement.close();
            }
            if (product.getStockQuantity() != null) {
                allIsGood = false;
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _product\n" +
                        "   SET quantity = ?\n" +
                        " WHERE serial = '" + originalSerial + "'");
                preparedStatement.setInt(1, product.getStockQuantity());
                executionStatus = preparedStatement.executeUpdate();
                if (executionStatus > 0) {
                    allIsGood = true;
                }
                preparedStatement.close();
            }
            if (product.getMarkedPrice() != null) {
                allIsGood = false;
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _product\n" +
                        "   SET markedPrice = ?\n" +
                        " WHERE serial = '" + originalSerial + "'");
                preparedStatement.setDouble(1, product.getMarkedPrice());
                executionStatus = preparedStatement.executeUpdate();
                if (executionStatus > 0) {
                    allIsGood = true;
                }
                preparedStatement.close();
            }
            if (product.getBuyingPrice() != null) {
                allIsGood = false;
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _product\n" +
                        "   SET buyPrice = ?\n" +
                        " WHERE serial = '" + originalSerial + "'");
                preparedStatement.setDouble(1, product.getBuyingPrice());
                executionStatus = preparedStatement.executeUpdate();
                if (executionStatus > 0) {
                    allIsGood = true;
                }
                preparedStatement.close();
            }
            if (imageName != null) {
                allIsGood = false;
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _product\n" +
                        "   SET imageName = ?\n" +
                        " WHERE serial = '" + originalSerial + "'");
                preparedStatement.setString(1, imageName);
                executionStatus = preparedStatement.executeUpdate();
                if (executionStatus > 0) {
                    allIsGood = true;
                }
                preparedStatement.close();
            }
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return allIsGood;
    }

    protected Product get_specific_product(String serial) {
        Product product = null;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT serial,\n" +
                    "       name,\n" +
                    "       description,\n" +
                    "       ratings,\n" +
                    "       quantity,\n" +
                    "       markedPrice,\n" +
                    "       buyPrice,\n" +
                    "       imageName\n" +
                    "  FROM _product\n" +
                    " WHERE serial = '" + serial + "' AND \n" +
                    "       isAvailable = true");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Image image;
                try {
                    image = new Image(new FileInputStream(Main.RESOURCE_PATH.getAbsolutePath().concat("\\_gallery\\").concat(resultSet.getString(8))));
                } catch (FileNotFoundException e) {
                    image = new Image(getClass().getResourceAsStream(INPUT_STREAM_TO_NO_IMAGE));
                }
                product = new Product();
                product.setSerial(resultSet.getString(1));
                product.setName(resultSet.getString(2));
                product.setDescription(resultSet.getString(3));
                product.setStarCount(resultSet.getInt(4));
                product.setStockQuantity(resultSet.getInt(5));
                product.setMarkedPrice(resultSet.getDouble(6));
                product.setBuyingPrice(resultSet.getDouble(7));
                product.setItemImage(image);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return product;
    }

    protected Boolean add_product_to_stock(@NotNull Product product, String nameOfImage) {
        boolean allIsGood = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO _product (\n" +
                    "                         imageName,\n" +
                    "                         buyPrice,\n" +
                    "                         markedPrice,\n" +
                    "                         quantity,\n" +
                    "                         ratings,\n" +
                    "                         description,\n" +
                    "                         name,\n" +
                    "                         serial\n" +
                    "                     )\n" +
                    "                     VALUES (\n" +
                    "                         ?,\n" +
                    "                         ?,\n" +
                    "                         ?,\n" +
                    "                         ?,\n" +
                    "                         ?,\n" +
                    "                         ?,\n" +
                    "                         ?,\n" +
                    "                         ?\n" +
                    "                     )");
            preparedStatement.setString(1, nameOfImage);
            preparedStatement.setDouble(2, product.getBuyingPrice());
            preparedStatement.setDouble(3, product.getMarkedPrice());
            preparedStatement.setInt(4, product.getStockQuantity());
            preparedStatement.setInt(5, product.getStarCount());
            preparedStatement.setString(6, product.getDescription());
            preparedStatement.setString(7, product.getName());
            preparedStatement.setString(8, product.getSerial());
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                allIsGood = true;
            }
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return allIsGood;
    }

    protected ObservableList<String> get_product_serials() {
        final ObservableList<String> stringObservableList = FXCollections.observableArrayList();
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT serial\n" +
                    "  FROM _product\n" +
                    " WHERE isAvailable = true");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    stringObservableList.addAll(resultSet.getString(1));
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return stringObservableList;
    }

    @NotNull
    protected ObservableList<String> get_stock_suggestions() {
        final ObservableList<String> stringObservableList = FXCollections.observableArrayList();
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT name,\n" +
                    "       description\n" +
                    "  FROM _product");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    stringObservableList.addAll(resultSet.getString(1), resultSet.getString(2));
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return stringObservableList;
    }

    @NotNull
    @Contract(pure = true)
    private String decode_boolean_to_string(boolean status) {
        if (status) {
            return "available".toUpperCase();
        } else {
            return "deleted".toUpperCase();
        }
    }

    @NotNull
    @Contract("_ -> new")
    private Image get_image_of_ratings_for_table(int count) {
        switch (count) {
            case 1:
                return new Image("/org/_brown_tech/_images/_icons/table_1_star.png");
            case 2:
                return new Image("/org/_brown_tech/_images/_icons/table_2_star.png");
            case 3:
                return new Image("/org/_brown_tech/_images/_icons/table_3_star.png");
            case 5:
                return new Image("/org/_brown_tech/_images/_icons/table_5_star.png");
            case 4:
            default:
                return new Image("/org/_brown_tech/_images/_icons/table_4_star.png");
        }
    }

    protected ObservableList<Stock> get_stock_items_based_on_param(@NotNull String param) {
        final ObservableList<Stock> stockObservableList = FXCollections.observableArrayList();
        final String ROOT_QUERY = "SELECT serial,\n" +
                "       name,\n" +
                "       description,\n" +
                "       ratings,\n" +
                "       quantity,\n" +
                "       markedPrice,\n" +
                "       buyPrice,\n" +
                "       isAvailable\n" +
                "  FROM _product";
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement;
            if (param.isEmpty()) {
                preparedStatement = connection.prepareStatement(ROOT_QUERY);
            } else if (param.equalsIgnoreCase("#deleted") || param.equalsIgnoreCase("#false")) {
                preparedStatement = connection.prepareStatement(ROOT_QUERY
                        .concat(" WHERE ( (serial LIKE '%" + param + "%') OR \n" +
                                "         (name LIKE '%" + param + "%') OR \n" +
                                "         (description LIKE '%" + param + "%') OR \n" +
                                "         (quantity LIKE '%" + param + "%') OR \n" +
                                "         (isAvailable = false) )"));
            } else {
                preparedStatement = connection.prepareStatement(ROOT_QUERY
                        .concat(" WHERE ( (serial LIKE '%" + param + "%') OR \n" +
                                "         (name LIKE '%" + param + "%') OR \n" +
                                "         (description LIKE '%" + param + "%') OR \n" +
                                "         (quantity LIKE '%" + param + "%') )"));
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.isBeforeFirst()) {
                while (resultSet.next()) {
                    final Stock stock = new Stock();
                    stock.setSerial(resultSet.getString(1));
                    stock.setName(resultSet.getString(2));
                    stock.setDescription(resultSet.getString(3));
                    stock.setImageView(new ImageView(get_image_of_ratings_for_table(resultSet.getInt(4))));
                    stock.setQuantity(String.format("%d", resultSet.getInt(5)));
                    stock.setMarkedPrice("Ksh ".concat(String.format("%,.1f", resultSet.getDouble(6))));
                    stock.setBuyingPrice("Ksh ".concat(String.format("%,.1f", resultSet.getDouble(7))));
                    stock.setStatus(decode_boolean_to_string(resultSet.getBoolean(8)));
                    stockObservableList.add(stock);
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return stockObservableList;
    }

    protected Double get_sum_of_all_services_charged_in_the_given_timeline_param(String timeline) {
        double totalSum = 0;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(sellingPrice) AS tReturns\n" +
                    "  FROM _receipt\n" +
                    " WHERE isProduct = false AND \n" +
                    "       dateTime LIKE '%" + timeline + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalSum += resultSet.getDouble("tReturns");
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return totalSum;
    }

    protected Double get_sum_of_all_products_sold_in_the_given_timeline_param(String timeline) {
        double totalSum = 0;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(sellingPrice) AS tReturns\n" +
                    "  FROM _receipt\n" +
                    " WHERE isProduct = true AND \n" +
                    "       dateTime LIKE '%" + timeline + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalSum += resultSet.getDouble("tReturns");
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return totalSum;
    }

    protected Boolean update_password(String username, String hashedPassword) {
        boolean allIsGood = false;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE _user\n" +
                    "   SET password = ?\n" +
                    " WHERE username = '" + username + "'");
            preparedStatement.setString(1, hashedPassword);
            final int executionStatus = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
            if (executionStatus > 0) {
                allIsGood = true;
            }
        } catch (SQLException e) {
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            e.printStackTrace();
            new Issues().programmer_error(e).show();
        }
        return allIsGood;
    }

    protected final AccountObj get_details_of_a_user(final String userName, boolean isAdmin) {
        AccountObj accountObj = null;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT username,\n" +
                    "       firstname,\n" +
                    "       surname,\n" +
                    "       password,\n" +
                    "       email,\n" +
                    "       isAdmin\n" +
                    "  FROM _user\n" +
                    " WHERE username = '" + userName + "' AND isAdmin = " + isAdmin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                accountObj = new AccountObj(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getBoolean(6));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return accountObj;
    }

    protected final String verify_user(final String username, final String password, boolean isAdmin) {
        String firstName = null;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT firstname,\n" +
                    "       password\n" +
                    "  FROM _user\n" +
                    " WHERE isActive = 1 AND \n" +
                    "       username = '" + username + "'" +
                    "       AND isAdmin = " + isAdmin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                final String hashedText = resultSet.getString(2);
                if (BCrypt.checkpw(password, hashedText)) {
                    firstName = resultSet.getString(1);
                }
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return firstName;
    }

    protected final String check_if_the_username_is_legit(final String username) {
        String fullname = null;
        try {
            Connection connection = connect_to_memory();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT firstname,\n" +
                    "       surname\n" +
                    "  FROM _user\n" +
                    " WHERE isActive = 1 AND \n" +
                    "       username = '" + username + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                fullname = resultSet.getString(1).concat(" ").concat(resultSet.getString(2));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            new Thread(new Issues().write_log("\n\n" + new Issues().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Issues().stack_trace_printing(e.getStackTrace())).start();
            new Issues().programmer_error(e).show();
        }
        return fullname;
    }

}
