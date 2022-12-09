package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class signUpController implements Initializable {
    @FXML
    TextField signup_username_textField,signup_email_textField,signup_phone_textField,signup_createPassword_textField,signup_confirmPassword_textField;
    @FXML
    Button signUpBtn,signup_loginBtn;

    @FXML
    TextField signup_fullName;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signUpBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!Objects.equals(signup_fullName.getText(), "")&&!Objects.equals(signup_username_textField.getText(), "")&&!Objects.equals(signup_username_textField.getText(), "BMF(Account)")&&!Objects.equals(signup_email_textField.getText(), "")&&!Objects.equals(signup_phone_textField.getText(), "")&&!Objects.equals(signup_createPassword_textField.getText(), "")&&!Objects.equals(signup_confirmPassword_textField.getText(), "")&& Objects.equals(signup_createPassword_textField.getText(), signup_confirmPassword_textField.getText()))
                DataBaseConnection.signUpUser(event,signup_username_textField.getText(),signup_email_textField.getText(), signup_phone_textField.getText(),signup_confirmPassword_textField.getText(),signup_fullName.getText());
                else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Please Fill all TextFields");
                    alert.show();
                }
            }
        });
        signup_loginBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    DataBaseConnection.changeScene(event,"login.fxml","login",null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
