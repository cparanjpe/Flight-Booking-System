package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class afterPaymentWindowController {

    @FXML
    private Label airline_name;

    @FXML
    private Label arrival_name;

    @FXML
    private Label boarding_time;

    @FXML
    private Label booking_id;

    @FXML
    private Label departure_date;

    @FXML
    private Label departure_name;

    @FXML
    private Label flight_id;

    @FXML
    private Button goBackToUserPortalBtn;

    @FXML
    private Label passenger_name;

    @FXML
    private Label seat_no;

    public String user_name;
    public void setData(BoardingPassData obj,String UserName){
        // exception here
        passenger_name.setText(obj.getUserFullName());
        departure_name.setText(obj.getDepartureName());
        arrival_name.setText(obj.getArrivalName());
        boarding_time.setText(obj.getBoardingTime());
        airline_name.setText(obj.getAirlineName());
        flight_id.setText(obj.getFlightId());
        departure_date.setText(obj.getDepartureDate());
        seat_no.setText(obj.getSeatNo());
        booking_id.setText(obj.getBookingId());
        user_name = UserName;
    }
    public void goBackToUserPortal(ActionEvent event) throws IOException {
        DataBaseConnection.changeScene(event,"userPortal.fxml" , "user portal",user_name);
    }
}