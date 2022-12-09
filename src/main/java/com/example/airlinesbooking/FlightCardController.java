package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FlightCardController {

    @FXML
    private Label AirlineNameLabel;

    @FXML
    private Label ArrivalLabel;

    @FXML
    private Label ArrivalTimeLabel;

    @FXML
    private Button BookBtn;

    @FXML
    private Label DateLabel;

    @FXML
    private Label DepartureLabel;

    @FXML
    private Label DepartureTimeLabel;

    @FXML
    private Label PriceLabel;

    @FXML
    private HBox card;
    private String flightId,flightType,userNameFinal;

    public void setData(FlightData dataObj){
        AirlineNameLabel.setText(dataObj.getAirline());
        DepartureLabel.setText(dataObj.getDepartutre());
        ArrivalLabel.setText(dataObj.getArrival());
        DepartureTimeLabel.setText(dataObj.getDepartTime());
        ArrivalTimeLabel.setText(dataObj.getArrivalTime());
        PriceLabel.setText("₹"+dataObj.getPrice());
        Date dNow = new Date( );
        SimpleDateFormat ft =  new SimpleDateFormat ("dd/MM/yy");
        DateLabel.setText(ft.format(dNow));
        flightId = dataObj.getFlightId();
        this.flightType = dataObj.getFlightType();
    }

    public void bookFlightEvent(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("preBookingInfoWindow.fxml"));
        VBox vBox = fxmlLoader.load();
        preBookingWindowController controller = fxmlLoader.getController();
        controller.setUser_nameAndFlight_id(userPortalController.getUserName(),flightId ,flightType);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(vBox));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("pre Booking Details");
        stage.show();
    }

}
