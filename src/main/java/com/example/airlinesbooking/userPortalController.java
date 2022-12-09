package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

public class userPortalController implements Initializable {
    // injections
    @FXML
    Button userPortal_logoutBtn,bookingHistoryBtn,cancelFlightBtn,domesticFlightBtn,internationalFlightBtn,refreshBtn;

    @FXML
    Label displayUserName_and_photo_Label;

    @FXML
    ScrollPane cardLayout;

    @FXML
    BorderPane userPortal_main_borderPane;
    @FXML
    private Label userPortal_walletMoney_Label;


    public static String user_name;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    public BorderPane getReferenceOfBorderPane(){
        return userPortal_main_borderPane;
    }


    public void showTransactions(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("transactionLayout.fxml"));
        ScrollPane scrollPane = fxmlLoader.load();
        transactionLayoutController controller = fxmlLoader.getController();
        controller.setUserName(user_name);
        userPortal_main_borderPane.setCenter(scrollPane);
    }

    public void cancelMyFlightLayout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cancelFlight.fxml"));
        VBox vBox = fxmlLoader.load();
        cancelFlightController controller = fxmlLoader.getController();
        controller.setUserName(user_name);
        userPortal_main_borderPane.setCenter(vBox);
    }

    public void changeBorderPaneCenterToAddMoney(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addMoneyToWalletLayout.fxml"));
        VBox vBox = fxmlLoader.load();
        addMoneyToWalletController controller = fxmlLoader.getController();
        controller.setUserName(user_name);
        userPortal_main_borderPane.setCenter(vBox);
    }


    public void setDomesticFlightBtnEvent(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("domesticFlightLayout.fxml"));
        VBox vBox = fxmlLoader.load();
        domesticFlightLayoutController controller = fxmlLoader.getController();

        userPortal_main_borderPane.setCenter(vBox);
    }

    public void setInternationalFlightBtnEvent(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("internationalFlightLayout.fxml"));
        VBox vBox = fxmlLoader.load();
        userPortal_main_borderPane.setCenter(vBox);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("domesticFlightLayout.fxml"));
            VBox vBox = null;
            try {
            vBox = fxmlLoader.load();
             } catch (IOException e) {
             e.printStackTrace();
            }
            userPortal_main_borderPane.setCenter(vBox);

        userPortal_logoutBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        DataBaseConnection.changeScene(event ,"login.fxml" , "Login" , null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
    }
    public void setRefreshBtn(ActionEvent event){
        setUserInformation(user_name);
    }
    public void setUserInformation(String userName){
        displayUserName_and_photo_Label.setText("Hey "+userName);
        Connection con = connectionProvider.getConnection();
        String query = "SELECT user_money FROM userdata WHERE user_name = ?";
        PreparedStatement pstmtWalletMoney = null;
        try {
            pstmtWalletMoney = con.prepareStatement(query);

        pstmtWalletMoney.setString(1,userName);
        ResultSet resultSet = pstmtWalletMoney.executeQuery();
        if (resultSet.next()){

            Double walletMoney = Double.valueOf(resultSet.getString("user_money"));
            userPortal_walletMoney_Label.setText("Wallet Money : ₹"+df.format(walletMoney));
        }
        con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        user_name = userName;
    }
    public static String getUserName(){
        return user_name;
    }
    public void showBoardingPasses(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ticketLayout.fxml"));
        ScrollPane scrollPane = fxmlLoader.load();
        ticketLayoutController controller = fxmlLoader.getController();
        controller.setUserName(user_name);
        userPortal_main_borderPane.setCenter(scrollPane);
    }
}

