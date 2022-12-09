package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

class paymentDetails{
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    private String paymentMethod,amount,receiver,sender;

}

public class cancelFlightController {

    @FXML
    private TextField cancelFlight_enterBookingId;

    private String user_name;
    public void setUserName(String UserName){
        user_name = UserName;
    }
    private double main_amount;
    private paymentDetails getPaymentDetails(String transactionId) throws SQLException {
        paymentDetails pd = new paymentDetails();
        Connection con = connectionProvider.getConnection();
        String query1 = "select * from transactions where transaction_id = ?";
        PreparedStatement pstmtGetPaymentDetails = null;
        try {
            pstmtGetPaymentDetails = con.prepareStatement(query1);
        pstmtGetPaymentDetails.setInt(1, Integer.parseInt(transactionId));
        ResultSet resultSet1 = pstmtGetPaymentDetails.executeQuery();
        resultSet1.next();
        pd.setPaymentMethod(resultSet1.getString("transaction_method"));
        pd.setAmount(resultSet1.getString("amount"));
        pd.setSender(resultSet1.getString("sender"));
        pd.setReceiver(resultSet1.getString("receiver"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.close();
        return pd;
    }

    private void makeRefundTransaction(paymentDetails pd){
        Connection con = connectionProvider.getConnection();
        String query = "Insert into transactions(sender , receiver , amount ,transaction_method ,description , user_name) values(?,?,?,?,?,?);";
        try {
            PreparedStatement pstmtRefund = con.prepareStatement(query);
            pstmtRefund.setString(1,pd.getReceiver());
            pstmtRefund.setString(2,pd.getSender());
            double amount = 0.75*Double.parseDouble(pd.getAmount());
            main_amount = amount;
            pstmtRefund.setString(3, String.valueOf(amount));
            pstmtRefund.setString(4,"Internet-Banking");
            pstmtRefund.setString(5,"Refund for flight cancellation");
            pstmtRefund.setString(6,user_name);
            pstmtRefund.executeUpdate();
            // UPDATE `airline-booking`.`userdata` SET `user_money` = '303' WHERE (`user_id` = '1');
            if (pd.getPaymentMethod().equals("BMF(Wallet)")){
                query = "select user_money from userdata where user_name = ?";
                PreparedStatement pstmtGetWalletDetails = con.prepareStatement(query);
                pstmtGetWalletDetails.setString(1,user_name);
                ResultSet resultSet = pstmtGetWalletDetails.executeQuery();
                resultSet.next();
                double walletBalance = Double.parseDouble(resultSet.getString("user_money"));
                double newWalletBalance = walletBalance+amount;
                query = "update userdata set user_money = ? where user_name = ?";
                PreparedStatement pstmtUpdateWallet = con.prepareStatement(query);
                pstmtUpdateWallet.setString(1, String.valueOf(newWalletBalance));
                pstmtUpdateWallet.setString(2,user_name);
                pstmtUpdateWallet.executeUpdate();
            }
            query = "";
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void cancelMyFlight(ActionEvent event){
        if (Objects.equals(cancelFlight_enterBookingId.getText(), "")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please Enter your booking id!!");
            alert.show();
        } else{
            try {
                int booking_id = Integer.parseInt(cancelFlight_enterBookingId.getText());

                Connection con = connectionProvider.getConnection();
                String query = "Select * from userbookingdata where booking_id = ?";
                PreparedStatement pstmtGetBookingDetails = con.prepareStatement(query);
                pstmtGetBookingDetails.setInt(1,booking_id);
                ResultSet resultSet = pstmtGetBookingDetails.executeQuery();
                if (resultSet.next()){
                    String user_nameTobeChecked;
                    user_nameTobeChecked = resultSet.getString("user_name");
                    if (Objects.equals(user_name, user_nameTobeChecked)){
                        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
                        // current date
                        Date currentDate = new Date();
                        LocalDate dateOfDeparture = LocalDate.parse(resultSet.getString("dept_date"));
                        Date dateOfDepartureNew = java.sql.Date.valueOf(dateOfDeparture);
                        if (dateOfDepartureNew.compareTo(currentDate)<0){
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Your flight is not eligible for cancellation");
                            alert.show();
                        }else {
                           String transactionId = resultSet.getString("transaction_id");
                           paymentDetails pd = getPaymentDetails(transactionId);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setContentText("Your flight cancelled successfully and amount will be refunded shortly");
                            alert.show();
                            query = "delete from userbookingdata where booking_id = ?";
                            PreparedStatement pstmtDeleteBookingDetailFromDatabase = con.prepareStatement(query);
                            pstmtDeleteBookingDetailFromDatabase.setInt(1,booking_id);
                            pstmtDeleteBookingDetailFromDatabase.executeUpdate();
                            makeRefundTransaction(pd);
                            query = "select * from userdata where user_name = ?";
                            PreparedStatement pstmtGetMail = con.prepareStatement(query);
                            pstmtGetMail.setString(1,user_name);
                            ResultSet resultSet1 = pstmtGetMail.executeQuery();
                            resultSet1.next();
                            String msg = "Your Booking id "+booking_id+" has been cancelled successfully and refund of "+main_amount+ " will be credited soon";
                            sendMail.send_Mail(resultSet1.getString("user_email"),msg);
                        }

                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Please enter booking id of your user name");
                        alert.show();
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("booking id does not exist please enter a valid booking id");
                    alert.show();
                }
                con.close();
            }catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please enter a valid booking_id");
                alert.show();
            }
        }
    }

}
