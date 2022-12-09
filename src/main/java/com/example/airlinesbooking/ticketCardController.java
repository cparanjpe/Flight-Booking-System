package com.example.airlinesbooking;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ticketCardController {

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
    private Label passenger_name;

    @FXML
    private Label seat_no;
    @FXML
    private Label flight_id;

    public void setData(BoardingPassData obj){
        passenger_name.setText(obj.getUserFullName());
        departure_name.setText(obj.getDepartureName());
        arrival_name.setText(obj.getArrivalName());
        boarding_time.setText(obj.getBoardingTime());
        airline_name.setText(obj.getAirlineName());
        booking_id.setText(obj.getBookingId());
        flight_id.setText(obj.getFlightId());
        departure_date.setText(obj.getDepartureDate());
        seat_no.setText(obj.getSeatNo());
    }

}

