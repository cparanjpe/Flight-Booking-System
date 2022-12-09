package com.example.airlinesbooking;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class transactionLayoutController{

    @FXML
    private ScrollPane transactionLayout;

    @FXML
    private VBox transactionsKoIsmeDalnaHai;
    private String userName;

    public void setUserName(String user_name){
        this.userName = user_name;
        showMyTransactions();
    }

    private List<transactionData> addDataToArrayFromDataBase() throws SQLException {
        List<transactionData> ls = new ArrayList<>();
        transactionData data;
        Connection con = connectionProvider.getConnection();
        String query = "select * from transactions where user_name = ?;";
        PreparedStatement pstmtTransactions = con.prepareStatement(query);
        pstmtTransactions.setString(1,userName);
        ResultSet resultSet = pstmtTransactions.executeQuery();
        while (resultSet.next()) {
            data = new transactionData();
            data.setTransactionId(resultSet.getString(1));
            data.setSender(resultSet.getString(2));
            data.setReceiver(resultSet.getString(3));
            data.setAmount(resultSet.getString(4));
            ls.add(data);
        }
        return ls;
    }


    public void showMyTransactions() {
        try {
            ArrayList<transactionData> data = new ArrayList<>(addDataToArrayFromDataBase());

            for (int i=0;i< data.size() ; i++){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("transactionCard.fxml"));
                HBox hBox = fxmlLoader.load();
                transactionCardController cardController = fxmlLoader.getController();
                cardController.setData(data.get(i));
                transactionsKoIsmeDalnaHai.getChildren().add(hBox);
            }


        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
