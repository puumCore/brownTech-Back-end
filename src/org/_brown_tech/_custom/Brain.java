package org._brown_tech._custom;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org._brown_tech.Main;
import org._brown_tech._object.Product;
import org._brown_tech._object._actors.Account;
import org._brown_tech._object._actors.User;
import org._brown_tech._object._payments.Cheque;
import org._brown_tech._object._payments.Purchase;
import org._brown_tech._object._payments.Receipt;
import org._brown_tech._outsourced.BCrypt;
import org._brown_tech._response_model.StandardResponse;
import org._brown_tech._response_model.StatusResponse;
import org._brown_tech._table_model.AccountModel;
import org._brown_tech._table_model.PurchaseModel;
import org._brown_tech._table_model.ReceiptModel;
import org._brown_tech._table_model.StockModel;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author Mandela
 */
public class Brain extends Watchdog {

    public static final String INPUT_STREAM_TO_NO_IMAGE = "/org/_brown_tech/_images/_pictures/noimage.png";
    public static Account myAccount = null;
    protected static String BASE_URL = "http://localhost:4567/brownTech/pos/api";


    public Account reset_password(Account account) {
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/recovery"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(account, Account.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    account = new Gson().fromJson(standardResponse.getData(), Account.class);
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
            account = null;
        }
        return account;
    }

    protected Boolean approve_cheque(String dateTimeAsSerial) {
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/purchase/payments/cheque/status"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(dateTimeAsSerial, String.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    return true;
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return false;
    }

    protected Boolean make_user_account_inactive_or_not(User user) {
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/user/active_status"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(user, User.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    return true;
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return false;
    }

    protected ObservableList<User> get_all_users() {
        final ObservableList<User> userObservableList = FXCollections.observableArrayList();
        try {
            final HttpGet httpGet = new HttpGet(BASE_URL.concat("/staff"));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> userObservableList.add(new Gson().fromJson(jsonElement, User.class)));
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return userObservableList;
    }

    protected Boolean create_new_account_for_staff(@NotNull final Account account) {
        boolean accountHasBennCreated = false;
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/staff/new"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(account, Account.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    accountHasBennCreated = true;
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return accountHasBennCreated;
    }

    protected ObservableList<String> get_username_staff_based_on_employment_status(boolean status) {
        final ObservableList<String> stringObservableList = FXCollections.observableArrayList();
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/staff/status"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(status, Boolean.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    final JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> {
                        final String username = new Gson().fromJson(jsonElement, String.class);
                        stringObservableList.add(username);
                    });
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return stringObservableList;
    }

    protected ObservableList<AccountModel> get_staff_users_based_on_param(@NotNull String param) {
        final ObservableList<AccountModel> accountModelObservableList = FXCollections.observableArrayList();
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/staff/find"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(param, String.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> {
                        final Account account = new Gson().fromJson(jsonElement, Account.class);
                        accountModelObservableList.add(
                                new AccountModel(
                                        account.getUsername(),
                                        account.getFname(),
                                        account.getSurname(),
                                        account.getEmail())
                        );
                    });
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return accountModelObservableList;
    }

    @NotNull
    protected HashMap<String, Double> load_profit_and_loss_trend_across_months_of_a_requested_year(@NotNull String parameters) {
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        final HashMap<String, Double> line_graph_data = new HashMap<>();
        for (String month_name : months) {
            String search_parameters = month_name + "-" + parameters;
            double grossProfit = 0.0;
            try {
                final HttpPost httpPost = new HttpPost(BASE_URL.concat("/statistics/report/graph"));
                httpPost.setEntity(new StringEntity(new Gson().toJson(search_parameters, String.class)));
                final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
                final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
                final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                    final String objectAsString = EntityUtils.toString(httpEntity);
                    final JsonParser jsonParser = new JsonParser();
                    final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                    final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                    if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                        grossProfit = new Gson().fromJson(standardResponse.getData(), Double.class);
                    } else {
                        error_message("Failed!", standardResponse.getMessage()).show();
                    }
                } else {
                    error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
                new Thread(stack_trace_printing(e)).start();
                programmer_error(e).show();
                break;
            }
            line_graph_data.put(month_name, grossProfit);
        }
        return line_graph_data;
    }

    protected List<Cheque> get_pending_cheques() {
        final List<Cheque> chequeList = new ArrayList<>();
        try {
            final HttpGet httpGet = new HttpGet(BASE_URL.concat("/purchase/payments/cheque"));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> chequeList.add(new Gson().fromJson(jsonElement, Cheque.class)));
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return chequeList;
    }

    protected JsonObject get_cheque_data() {
        try {
            final HttpGet httpGet = new HttpGet(BASE_URL.concat("/purchase/payments/details/cheque"));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    return new Gson().fromJson(standardResponse.getData(), JsonObject.class);
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return null;
    }

    protected JsonObject get_sum_of_payment_methods() {
        try {
            final HttpGet httpGet = new HttpGet(BASE_URL.concat("/purchase/payments/summary"));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    return new Gson().fromJson(standardResponse.getData(), JsonObject.class);
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return null;
    }

    protected ObservableList<String> get_a_list_of_receipt_numbers() {
        final ObservableList<String> stringObservableList = FXCollections.observableArrayList();
        try {
            final HttpGet httpGet = new HttpGet(BASE_URL.concat("/purchase/receipts_numbers"));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    final JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> {
                        final String receiptNumber = new Gson().fromJson(jsonElement, String.class);
                        stringObservableList.add(receiptNumber);
                    });
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
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

    protected ObservableList<ReceiptModel> get_receipts_based_on_param(@NotNull String receiptNumber) {
        final ObservableList<ReceiptModel> receiptModelObservableList = FXCollections.observableArrayList();
        try {
            HttpGet httpGet;
            if (receiptNumber.isEmpty()) {
                httpGet = new HttpGet(BASE_URL.concat("/purchase/receipt"));
            } else {
                httpGet = new HttpGet(BASE_URL.concat("/purchase/receipt/find/").concat(receiptNumber));
            }
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> {
                        final Receipt receipt = new Gson().fromJson(jsonElement, Receipt.class);
                        receiptModelObservableList.add(
                                new ReceiptModel(
                                        String.format("%d", receipt.getReceiptNumber()),
                                        receipt.getDateAndTime(),
                                        receipt.getSerial(),
                                        String.format("%d", receipt.getQuantitySold()),
                                        "Ksh ".concat(String.format("%,.1f", receipt.getSellingPrice())),
                                        "Ksh ".concat(String.format("%,.1f", receipt.getBuyingPrice())),
                                        decode_type_of_stock_from_boolean(receipt.isProduct())));
                    });
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return receiptModelObservableList;
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

    protected ObservableList<PurchaseModel> get_purchases_based_on_param(@NotNull String param) {
        final ObservableList<PurchaseModel> purchaseModelObservableList = FXCollections.observableArrayList();
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/purchase/find"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(param, String.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    final JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> {
                        final Purchase purchase = new Gson().fromJson(jsonElement, Purchase.class);
                        purchaseModelObservableList.add(new PurchaseModel(purchase.getDateTime(),
                                String.format("%d", purchase.getReceiptId()),
                                ("Ksh ".concat(String.format("%,.1f", purchase.getBillAmount()))),
                                decode_paymentMethod(purchase.getPaymentMethod()).toUpperCase(),
                                purchase.getFullname()));
                    });
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return purchaseModelObservableList;
    }

    protected Boolean delete_product(@NotNull Product product, boolean makePermanent) {
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/stock/products/del"));
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("type", new Gson().toJsonTree(makePermanent, Boolean.class));
            jsonObject.add("product", new Gson().toJsonTree(product, Product.class));
            httpPost.setEntity(new StringEntity(new Gson().toJson(jsonObject, JsonObject.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    return true;
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return false;
    }

    @NotNull
    protected final ObservableList<String> get_data_from_properties_file() {
        final ObservableList<String> results = FXCollections.observableArrayList();
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
            new Thread(new Watchdog().write_log("\n\n" + new Watchdog().time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(new Watchdog().stack_trace_printing(e)).start();
            new Watchdog().programmer_error(e).show();
        }
        return results;
    }

    protected Boolean update_a_stock_item(String originalSerial, @NotNull Product product) {
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/stock/products/mod"));
            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("serial", new Gson().toJsonTree(originalSerial, String.class));
            jsonObject.add("product", new Gson().toJsonTree(product, Product.class));
            httpPost.setEntity(new StringEntity(new Gson().toJson(jsonObject, JsonObject.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    return true;
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return false;
    }

    protected Product get_specific_product(String productSerial) {
        Product product = null;
        try {
            final HttpGet httpGet = new HttpGet(BASE_URL.concat("/stock/find/").concat(productSerial));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    product = new Gson().fromJson(standardResponse.getData(), Product.class);
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return product;
    }

    protected Boolean add_product_to_stock(@NotNull Product product) {
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/stock/products/new"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(product, Product.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    return true;
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return false;
    }

    protected ObservableList<String> get_product_serials() {
        final ObservableList<String> stringObservableList = FXCollections.observableArrayList();
        try {
            final HttpGet httpGet = new HttpGet(BASE_URL.concat("/stock/serials/true"));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    final JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> {
                        final String receiptNumber = new Gson().fromJson(jsonElement, String.class);
                        stringObservableList.add(receiptNumber);
                    });
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return stringObservableList;
    }

    @NotNull
    protected ObservableList<String> get_stock_suggestions() {
        final ObservableList<String> stringObservableList = FXCollections.observableArrayList();
        try {
            final HttpGet httpGet = new HttpGet(BASE_URL.concat("/stock/products/suggestions"));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    final JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> {
                        final String suggestion = new Gson().fromJson(jsonElement, String.class);
                        stringObservableList.add(suggestion);
                    });
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
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

    protected ObservableList<StockModel> get_stock_items_based_on_param(@NotNull String param) {
        final ObservableList<StockModel> stockModelObservableList = FXCollections.observableArrayList();
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/stock/products/find/advanced"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(param, String.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    final JsonArray jsonArray = new Gson().fromJson(standardResponse.getData(), JsonArray.class);
                    jsonArray.forEach(jsonElement -> {
                        final Product product = new Gson().fromJson(jsonElement, Product.class);
                        final StockModel stockModel = new StockModel();
                        stockModel.setSerial(product.getSerial_number());
                        stockModel.setName(product.getName());
                        stockModel.setDescription(product.getDescription());
                        stockModel.setImageView(new ImageView(get_image_of_ratings_for_table(product.getRating())));
                        stockModel.setQuantity(String.format("%d", product.getStock()));
                        stockModel.setMarkedPrice("Ksh ".concat(String.format("%,.1f", product.getMarkedPrice())));
                        stockModel.setBuyingPrice("Ksh ".concat(String.format("%,.1f", product.getBuyingPrice())));
                        stockModel.setStatus(decode_boolean_to_string(product.getAvailable()));
                        stockModelObservableList.add(stockModel);
                    });
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
        }
        return stockModelObservableList;
    }

    protected JsonObject get_dashboard_values(String timeline) {
        try {
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/dashboard"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(timeline, String.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    return new Gson().fromJson(standardResponse.getData(), JsonObject.class);
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            Platform.runLater(() -> programmer_error(e).show());
        }
        return null;
    }

    public Account update_account(@NotNull Account account) {
        try {
            account.setActive(true);
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/user/update"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(account, Account.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            account = null;
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    account = new Gson().fromJson(standardResponse.getData(), Account.class);
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
            account = null;
        }
        return account;
    }

    protected final Account get_user_account(final String username, final String password) {
        Account account = new Account();
        try {
            account.setUsername(username);
            final HttpPost httpPost = new HttpPost(BASE_URL.concat("/user/login"));
            httpPost.setEntity(new StringEntity(new Gson().toJson(account, Account.class)));
            final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
            final CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpPost);
            final int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                final HttpEntity httpEntity = closeableHttpResponse.getEntity();
                final String objectAsString = EntityUtils.toString(httpEntity);
                final JsonParser jsonParser = new JsonParser();
                final JsonElement rootJsonElement = jsonParser.parse(objectAsString);
                final StandardResponse standardResponse = new Gson().fromJson(rootJsonElement, StandardResponse.class);
                if (standardResponse.getStatus().equals(StatusResponse.SUCCESS)) {
                    account = new Gson().fromJson(standardResponse.getData(), Account.class);
                    if (account != null) {
                        if (password != null) {
                            if (!BCrypt.checkpw(password, account.getPassword())) {
                                error_message("Invalid credentials!", "Check your username or password").show();
                                account = null;
                            }
                        }
                    }
                } else {
                    error_message("Failed!", standardResponse.getMessage()).show();
                }
            } else {
                account = null;
                error_message("Error From Server : " + statusCode, "Reason: ".concat(closeableHttpResponse.getStatusLine().getReasonPhrase())).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Thread(write_log("\n\n" + time_stamp() + ": The following Exception occurred,\n" + e, 1)).start();
            new Thread(stack_trace_printing(e)).start();
            programmer_error(e).show();
            account = null;
        }
        return account;
    }

}