package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class addMoneyToWalletController {

    @FXML
    private Label addMoneyToWallet_Balance_Label;

    @FXML
    private Button addMoneyToWallet_btn;

    @FXML
    private TextField addMoneyToWallet_textField;
    private String userName;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    public void setUserName(String user_name){
        this.userName = user_name;
        showCurrentBalance();
    }

    public void payBtn(ActionEvent event) throws SQLException, IOException {
        double balance = showCurrentBalance();
        double requestAmt = 0;
        try {
            requestAmt = Double.parseDouble(addMoneyToWallet_textField.getText());
        if (requestAmt < 0){
            requestAmt *= -1;
        }
        double betaBalance = balance + requestAmt;
        if (betaBalance>750000){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Wallet Limit reached!!");
            alert.show();
        }
        else if(requestAmt<=1500){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Amount too Low!!");
            alert.show();
        }else {
            Connection con = connectionProvider.getConnection();
            String query = "update userdata set user_money = ? where user_name = ?";
            String query1 = "Insert into transactions(sender , receiver , amount ,transaction_method ,description , user_name) values(?,?,?,?,?,?);";
            PreparedStatement pstmtAddMoney = con.prepareStatement(query);
            PreparedStatement pstmtTransactionDatabase = con.prepareStatement(query1);
            // to transactions
            pstmtTransactionDatabase.setString(1,userName);
            pstmtTransactionDatabase.setString(2,"BMF(Wallet)");
            pstmtTransactionDatabase.setString(3, String.valueOf(requestAmt));
            pstmtTransactionDatabase.setString(4,"Internet-Banking");
            pstmtTransactionDatabase.setString(5,"Adding Money to BMF(Wallet)");
            pstmtTransactionDatabase.setString(6,userName);
            // to userdata
            pstmtAddMoney.setString(1, String.valueOf(betaBalance));
            pstmtAddMoney.setString(2, userName);
            pstmtAddMoney.executeUpdate();
            pstmtTransactionDatabase.executeUpdate();
            con.close();
            showCurrentBalance();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Payment Done Successfully");
            alert.show();
            con.close();
        }
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid Amount Input");
            alert.show();
        }

    }

    public double showCurrentBalance(){
        double balance=0;
        Connection con = connectionProvider.getConnection();
        String query = "SELECT user_money FROM userdata WHERE user_name = ?";
        PreparedStatement pstmtWalletMoney = null;
        try {
            pstmtWalletMoney = con.prepareStatement(query);

            pstmtWalletMoney.setString(1,userName);
            ResultSet resultSet = pstmtWalletMoney.executeQuery();
            if (resultSet.next()){
                Double walletMoney = Double.valueOf(resultSet.getString("user_money"));

                addMoneyToWallet_Balance_Label.setText("Current Balance : ₹"+df.format(walletMoney));
                balance = Double.parseDouble(resultSet.getString("user_money"));
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }
}
