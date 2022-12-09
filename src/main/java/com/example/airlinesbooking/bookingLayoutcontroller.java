package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class bookingLayoutcontroller implements Initializable {

    @FXML
    private VBox BookingWindowVBox;

    @FXML
    private Label bookingWindow_ArrivalTime_Label;

    @FXML
    private Label bookingWindow_DepartTime_Label;

    @FXML
    private Button bookingWindow_MakePayement_btn;

    @FXML
    private Label bookingWindow_TotalFlightTime_Label;

    @FXML
    private Label bookingWindow__Price_Label;

    @FXML
    private Label bookingWindow_airline_label;

    @FXML
    private Label bookingWindow_arrival_Label;

    @FXML
    private Label bookingWindow_departure_Label;

    @FXML
    private Label bookingWindow_flightId_Label;


    @FXML
    private Label bookingWindow_userEmail;

    @FXML
    private Label bookingWindow_userName;

    @FXML
    private Label bookingWindow_userPhoneNo;
    @FXML
    private Label bookingWindow_TravelClass_Label;

    @FXML
    private Label bookingWindow_TravellerNo_label;

    @FXML
    private Label bookingWindow_ReturnDate_Label;
    @FXML
    private Label bookingWindow_departureDate_Label;

    @FXML
    private Label bookingWindow_wayOfFlying_Label;
    private String clientUserName,Flight_id,FlightType,TravellerClass;
    private int travellers;
    private Boolean isOneWay;
    private LocalDate departureDate,returnDate;



    public void booking_setUser_nameAndFlight_id(String user_name, String flight_id, String flightType, int travellers, Boolean isOneWay, LocalDate departureDate,LocalDate returnDate,String travellerClass){
        this.clientUserName = user_name;
        this.Flight_id = flight_id;
        this.FlightType = flightType;
        this.travellers = travellers;
        this.isOneWay = isOneWay;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.TravellerClass = travellerClass;
        BookMyFlight();
    }

    public void makePaymentBtn(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("paymentLayout.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(fxmlLoader.load()));
        paymentLayoutController controller = fxmlLoader.getController();
        controller.setDataForPayment( clientUserName,Flight_id,FlightType,travellers,isOneWay,departureDate,returnDate,TravellerClass);
        stage.setTitle("Payment Window");
        stage.centerOnScreen();
        stage.show();
    }

    public void goBackToUserPortal(ActionEvent event) throws IOException {
        DataBaseConnection.changeScene(event, "userPortal.fxml", "userPortal", clientUserName);
    }

    public void BookMyFlight(){

        // getting info from userdata
        Connection con  = connectionProvider.getConnection();
        String query = "SELECT * FROM userdata WHERE user_name = ?";
        try {
            PreparedStatement pstmtBooking = con.prepareStatement(query);
            pstmtBooking.setString(1,clientUserName);
            ResultSet resultSet = pstmtBooking.executeQuery();
            while (resultSet.next()){
                bookingWindow_userName.setText("Full Name : "+resultSet.getString(6));
                bookingWindow_userEmail.setText("Email : "+resultSet.getString(3));
                bookingWindow_userPhoneNo.setText("Phone no : "+resultSet.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Connection con1 = connectionProvider.getConnection();
        String query1 = "SELECT * FROM DomesticFlightData where flight_id = ?";
        String query2 = "SELECT * FROM InternationalFlightData where flight_id = ?";
        if(FlightType.equals("Domestic")){
            query = query1;
        }else {
            query = query2;
        }
        try {
            PreparedStatement pstmtBookFlight = con1.prepareStatement(query);
            String flightType = FlightType+"FlightData";
            pstmtBookFlight.setString(1,Flight_id);
            ResultSet resultSet = pstmtBookFlight.executeQuery();
            if (resultSet.next()){
                bookingWindow_flightId_Label.setText("Flight ID : "+resultSet.getString(8));
                bookingWindow_departure_Label.setText("Departure : "+resultSet.getString(1));
                bookingWindow_arrival_Label.setText("Arrival : "+resultSet.getString(2));
                bookingWindow_airline_label.setText("Airline : "+resultSet.getString(3));
                bookingWindow_DepartTime_Label.setText("Departure Timing : "+resultSet.getTime(4));
                bookingWindow_ArrivalTime_Label.setText("Arrival Timing : "+resultSet.getTime(5));
                bookingWindow_TotalFlightTime_Label.setText("Total Flight Hrs : "+resultSet.getTime(6));
//                Date dNow = new Date( );
//                SimpleDateFormat ft =  new SimpleDateFormat ("dd/MM/yy");
//                booking_window_doteOfFlight.setText("Date of Flight : "+ft.format(dNow));
                double amount = (int) resultSet.getInt(7);
                if(TravellerClass.equals("Business")&&FlightType.equals("Domestic"))
                    amount+=2500;
                if (TravellerClass.equals("Business")&&FlightType.equals("International"))
                    amount+=11050;
                if(TravellerClass.equals("Premium Economy")&&FlightType.equals("Domestic"))
                    amount+=7500;
                if (TravellerClass.equals("Premium Economy")&&FlightType.equals("International"))
                    amount+=25000;
                if(!isOneWay)
                    amount = amount*2;
                // tax of 12%
                amount = amount*travellers;
                amount += amount*0.12;
                String FlightAmount = String.valueOf(amount);
                bookingWindow__Price_Label.setText("Final Amount (Tax Included) : ₹"+FlightAmount);
                bookingWindow_departureDate_Label.setText("Departure Date : "+departureDate);
                bookingWindow_TravellerNo_label.setText("No of Travellers : "+travellers);
                if (isOneWay) {
                    bookingWindow_wayOfFlying_Label.setText("One Way Flight");
                    bookingWindow_ReturnDate_Label.setText("Return Date : null");
                }
                else {
                    bookingWindow_wayOfFlying_Label.setText("Return Flight");
                    bookingWindow_ReturnDate_Label.setText("Return Date : "+returnDate);
                }
                bookingWindow_TravelClass_Label.setText("Traveller Class : "+TravellerClass);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }
}