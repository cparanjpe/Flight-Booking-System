package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseConnection {


    // utility function to change scenes according to situations
    public static void changeScene(ActionEvent event, String fxmlFile, String tile, String userName) throws IOException{

        FXMLLoader fxmlLoader = new FXMLLoader(DataBaseConnection.class.getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setTitle(tile);
        stage.setResizable(false);
        if (userName != null) {
            ((userPortalController) fxmlLoader.getController()).setUserInformation(userName);
//            stage.setFullScreen(true);
        }
        stage.centerOnScreen();
        stage.show();
    }

    // utility function to signup user to our dataBase
    public static void signUpUser(ActionEvent event, String userName, String email, String phoneNo, String Password,String FullName) {
        Connection con = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtCheckUserExist = null;
        ResultSet resultSet = null;
        try {
            con = connectionProvider.getConnection();
            String query = "Select * from userdata where user_name = ?";
            pstmtCheckUserExist = con.prepareStatement(query);
            pstmtCheckUserExist.setString(1, userName);
            resultSet = pstmtCheckUserExist.executeQuery();
            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("username already taken!!");
                alert.show();
            } else {
                query = "Insert into userdata(user_name , user_email , user_phone_no , user_password,user_full_name,user_money) values(?,?,?,?,?,?);";
                pstmtInsert = con.prepareStatement(query);
                pstmtInsert.setString(1, userName);
                pstmtInsert.setString(2, email);
                pstmtInsert.setString(3, phoneNo);
                pstmtInsert.setString(4, Password);
                pstmtInsert.setString(5,FullName);
                pstmtInsert.setString(6,"0");
                pstmtInsert.executeUpdate();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("You Registered successfully");
                alert.setHeaderText("Successfully Signed up");
                alert.setContentText("You can try Login now");
                alert.showAndWait();
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // utility function to log in the user to our userPortal if he had register in our database
    public static void loginUser(ActionEvent event, String userName, String Password) {
        ResultSet resultSet = null;
        PreparedStatement pstmtLogin = null;
        Connection con = null;
        try {
            con = connectionProvider.getConnection();
            String query = "SELECT user_password FROM userdata WHERE user_name = ?";
            pstmtLogin = con.prepareStatement(query);
            pstmtLogin.setString(1, userName);
            resultSet = pstmtLogin.executeQuery();

            if (resultSet.next()) {
                if (Password.equals(resultSet.getString("user_password"))) {
                    changeScene(event, "userPortal.fxml", "userPortal", userName);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Invalid Login Credentials (Incorrect Password)");
                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid Login Credentials (Incorrect Username)");
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (pstmtLogin != null) {
                try {
                    pstmtLogin.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}