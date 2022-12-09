package com.example.airlinesbooking;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class loginController implements Initializable {
    @FXML
    TextField login_username_email_textField;
    @FXML
    PasswordField login_password_textField;
    @FXML
    Button loginBtn;
    @FXML
    Button login_signUpBtn;
    @FXML
    Button login_forgotPasswordBtn;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!Objects.equals(login_username_email_textField.getText(), "") && !Objects.equals(login_password_textField.getText(), "")) {

                    DataBaseConnection.loginUser(event, login_username_email_textField.getText(), login_password_textField.getText());

                }
            }
        });
        login_signUpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DataBaseConnection.changeScene(event,"signUp.fxml" , "sign up" , null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
