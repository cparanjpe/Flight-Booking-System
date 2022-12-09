package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class preBookingWindowController implements Initializable {

    @FXML
    private DatePicker ReturnDatePicker;

    @FXML
    private DatePicker departureDatePicker;

    @FXML
    private Spinner<Integer> mySpinner;

    @FXML
    private RadioButton oneWay_radioBtn;

    @FXML
    private Button proceedBtn;

    @FXML
    private RadioButton return_RadioBtn;

    @FXML
    private ToggleGroup singleOrReturn;

    @FXML
    private ChoiceBox<String> travelClassChosser;
    @FXML
    Label ReturnDateLabel;

    public void radioBtnAndReturnDatePickerVisibility(ActionEvent event){
        if(oneWay_radioBtn.isSelected()){
            ReturnDateLabel.setVisible(false);
            ReturnDatePicker.setVisible(false);
        }else {
            ReturnDateLabel.setVisible(true);
            ReturnDatePicker.setVisible(true);
        }

    }


    public void goBackToUserPortal(ActionEvent event) throws IOException {
        DataBaseConnection.changeScene(event, "userPortal.fxml", "userPortal", user_name);
    }


   public void loadBookingDetailsLayout(ActionEvent event) throws IOException {
       if(departureDatePicker.getValue()!=null && (ReturnDatePicker.getValue()!=null || oneWay_radioBtn.isSelected()) && travelClassChosser.getValue()!=null) {
           FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BookingLayout.fxml"));
           VBox vBox = fxmlLoader.load();
           bookingLayoutcontroller layoutcontroller = fxmlLoader.getController();
           // true means oneWay & false means return
           if (oneWay_radioBtn.isSelected())
           layoutcontroller.booking_setUser_nameAndFlight_id(user_name, flightId, flightType,mySpinner.getValue(),true,departureDatePicker.getValue(),null,travelClassChosser.getValue());
           else
           layoutcontroller.booking_setUser_nameAndFlight_id(user_name, flightId, flightType,mySpinner.getValue(),false,departureDatePicker.getValue(),ReturnDatePicker.getValue(),travelClassChosser.getValue());
           Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
           stage.setScene(new Scene(vBox));
           stage.setTitle("Booking Details");
           stage.show();
       }
    }
    private String user_name,flightId,flightType;
   public void setUser_nameAndFlight_id(String user_name,String flightId ,String flightType){
        this.user_name = user_name;
        this.flightId = flightId;
        this.flightType = flightType;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,5);
        valueFactory.setValue(1);
        mySpinner.setValueFactory(valueFactory);
        // disabling all past dates from date picker for safety
        Callback<DatePicker, DateCell> callB = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        LocalDate today = LocalDate.now();
                        setDisable(empty || item.compareTo(today) < 0);
                    }

                };
            }

        };
        departureDatePicker.setDayCellFactory(callB);
        ReturnDatePicker.setDayCellFactory(callB);
        // initializing choice box
        String [] travellClass = {"Economy" , "Business" , "Premium Economy"};
        travelClassChosser.getItems().addAll(travellClass);
    }
}
