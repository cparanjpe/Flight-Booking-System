package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import kotlin.sequences.USequencesKt;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class paymentLayoutController implements Initializable {

    @FXML
    private Label finalAmountLabel;

    @FXML
    private Button finalPayBtn;

    @FXML
    private Label flightClassLabel;

    @FXML
    private Label flightTypeLabel;

    @FXML
    private Label noOfTravellersLabel;

    @FXML
    private Label oneWayOrReturn;

    @FXML
    private ChoiceBox<String> paymentMode_ChoiceBox;

    @FXML
    private Label rawFlightPriceLabel;

    @FXML
    private Label taxApplicable;
    @FXML
    private Button cancelPaymentBtn;


    private String UserName, Flight_id, FlightType, TravellerClass;
    private int travellers;
    private Boolean isOneWay;
    private LocalDate departureDate, returnDate;
    private int FlightPriceAccToClass;



    public void setDataForPayment(String user_name, String flight_id, String flightType, int travellers, Boolean isOneWay, LocalDate departureDate, LocalDate returnDate, String travellerClass) {
        this.UserName = user_name;
        this.Flight_id = flight_id;
        this.FlightType = flightType;
        this.travellers = travellers;
        this.isOneWay = isOneWay;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.TravellerClass = travellerClass;
        try {
            displayLabels();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private double amountToBePaid;
    private double bmfWalletBalance;

    private int generateSeatNo() {
        return (int) (Math.floor(Math.random() * 100));
    }

    private String getAirlineName() throws SQLException {
        Connection con = connectionProvider.getConnection();
        String query;
        if (FlightType.equals("Domestic"))
            query = "select airline from DomesticFlightData where flight_id = ?";
        else
            query = "Select * from InternationalFlightData where flight_id = ?";
        PreparedStatement pstmtGetAirline = con.prepareStatement(query);
        pstmtGetAirline.setString(1,Flight_id);
        ResultSet resultSet = pstmtGetAirline.executeQuery();
        resultSet.next();
        return resultSet.getString("airline");
    }

    public void pay(ActionEvent event) throws SQLException, IOException {
        String paymentMethod = paymentMode_ChoiceBox.getValue();
        if (paymentMethod==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select the payment method!!");
            alert.show();
        }else if (paymentMethod.equals("Internet-Banking")){
            // book his ticket
            // making his transaction
            Connection con = connectionProvider.getConnection();
            String query = "insert into transactions(sender,receiver,amount,transaction_method,description,user_name) values(?,?,?,?,?,?)";
            PreparedStatement pstmtCreateTransactions = con.prepareStatement(query);
            pstmtCreateTransactions.setString(1,UserName);
            pstmtCreateTransactions.setString(2,getAirlineName());
            pstmtCreateTransactions.setString(3, String.valueOf(amountToBePaid));
            pstmtCreateTransactions.setString(4,"Internet-Banking");
            pstmtCreateTransactions.setString(5,"Ticket Booking Payment");
            pstmtCreateTransactions.setString(6,UserName);
            pstmtCreateTransactions.executeUpdate();
            query = "select transaction_id from transactions where user_name = ?";
            PreparedStatement getTransactionId = con.prepareStatement(query);
            getTransactionId.setString(1,UserName);
            ResultSet resultSet = getTransactionId.executeQuery();
            int id=-1;
            while (resultSet.next()){
                id = resultSet.getInt("transaction_id");
            }

            query = "insert into userbookingdata(user_name,flight_id,flight_type,seat_no,amount_paid,transaction_id,dept_date) values(?,?,?,?,?,?,?)";
            PreparedStatement pstmtSetBookingDataToDatabase = con.prepareStatement(query);
            pstmtSetBookingDataToDatabase.setString(1,UserName);
            pstmtSetBookingDataToDatabase.setString(2,Flight_id);
            pstmtSetBookingDataToDatabase.setString(3,FlightType);
            pstmtSetBookingDataToDatabase.setString(4, "A"+generateSeatNo());
            pstmtSetBookingDataToDatabase.setString(5, String.valueOf(amountToBePaid));
            pstmtSetBookingDataToDatabase.setString(6, String.valueOf(id));
            pstmtSetBookingDataToDatabase.setString(7, String.valueOf(departureDate));
            pstmtSetBookingDataToDatabase.executeUpdate();

            query = "select * from userdata where user_name = ?";
            PreparedStatement pstmtGetMail = con.prepareStatement(query);
            pstmtGetMail.setString(1,UserName);
            ResultSet resultSet1 = pstmtGetMail.executeQuery();
            resultSet1.next();
            String msg = "Dear "+ resultSet1.getString("user_full_name") +" your airline ticket is booked and confirmed.Show this email to validate your ticket.Thank you for choosing us ! your transaction id is "+id +" and your booking id is "+getMeMyLatestBookingId();
            sendMail.send_Mail(resultSet1.getString("user_email"),msg);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Payment Done Successfully");
            alert.show();
            con.close();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("afterPaymentWindow.fxml"));


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(fxmlLoader.load()));
            afterPaymentWindowController controller = fxmlLoader.getController();
            controller.setData(getMeAnBoardingPassObj(),UserName);
            stage.setTitle("Boarding Pass");
            stage.centerOnScreen();
            stage.setResizable(false);
            stage.show();
            // show ticket layout here
            // ticket booked payment done using Internet-Banking
        }else {
            Connection con = connectionProvider.getConnection();
            String query = "Select user_money from userdata where user_name = ? ";
            PreparedStatement pstmtGetWalletBalanceInfo = con.prepareStatement(query);
            pstmtGetWalletBalanceInfo.setString(1,UserName);
            ResultSet resultSet = pstmtGetWalletBalanceInfo.executeQuery();
            if (resultSet.next()){
                bmfWalletBalance = Double.parseDouble(resultSet.getString(1));
            }
            if (bmfWalletBalance<amountToBePaid){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Insufficient Wallet Balance!!");
                alert.show();
            }else {

                query = "insert into transactions(sender,receiver,amount,transaction_method,description,user_name) values(?,?,?,?,?,?)";
                PreparedStatement pstmtCreateTransactions = con.prepareStatement(query);
                pstmtCreateTransactions.setString(1,"BMF(Wallet)");
                pstmtCreateTransactions.setString(2,getAirlineName());
                pstmtCreateTransactions.setString(3, String.valueOf(amountToBePaid));
                pstmtCreateTransactions.setString(4,"BMF(Wallet)");
                pstmtCreateTransactions.setString(5,"Ticket Booking Payment");
                pstmtCreateTransactions.setString(6,UserName);
                pstmtCreateTransactions.executeUpdate();
                query = "select transaction_id from transactions where user_name = ?";
                PreparedStatement getTransactionId = con.prepareStatement(query);
                getTransactionId.setString(1,UserName);
                resultSet = getTransactionId.executeQuery();
                int id=-1;
                while (resultSet.next()){
                    id = resultSet.getInt("transaction_id");
                }
                query = "insert into userbookingdata(user_name,flight_id,flight_type,seat_no,amount_paid,transaction_id,dept_date) values(?,?,?,?,?,?,?)";
                PreparedStatement pstmtSetBookingDataToDatabase = con.prepareStatement(query);
                pstmtSetBookingDataToDatabase.setString(1,UserName);
                pstmtSetBookingDataToDatabase.setString(2,Flight_id);
                pstmtSetBookingDataToDatabase.setString(3,FlightType);
                pstmtSetBookingDataToDatabase.setString(4, "A"+generateSeatNo());
                pstmtSetBookingDataToDatabase.setString(5, String.valueOf(amountToBePaid));
                pstmtSetBookingDataToDatabase.setString(6, String.valueOf(id));
                pstmtSetBookingDataToDatabase.setString(7, String.valueOf(departureDate));
                pstmtSetBookingDataToDatabase.executeUpdate();
                query = "update userdata set user_money = ? where user_name = ?";
                PreparedStatement pstmtUpdateWalletMoney = con.prepareStatement(query);
                double balanceAfterPaymentDone = bmfWalletBalance - amountToBePaid;
                pstmtUpdateWalletMoney.setString(1, String.valueOf(balanceAfterPaymentDone));
                pstmtUpdateWalletMoney.setString(2,UserName);
                pstmtUpdateWalletMoney.executeUpdate();
                query = "select * from userdata where user_name = ?";
                PreparedStatement pstmtGetMail = con.prepareStatement(query);
                pstmtGetMail.setString(1,UserName);
                ResultSet resultSet1 = pstmtGetMail.executeQuery();
                resultSet1.next();
                String msg = "Dear "+ resultSet1.getString("user_full_name") +" your airline ticket is booked and confirmed.Show this email to validate your ticket.Thank you for choosing us ! your transaction id is "+id +" and your booking id is "+getMeMyLatestBookingId();
                sendMail.send_Mail(resultSet1.getString("user_email"),msg);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Payment Done Successfully");
                alert.show();
                con.close();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("afterPaymentWindow.fxml"));


                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(fxmlLoader.load()));
                afterPaymentWindowController controller = fxmlLoader.getController();
                controller.setData(getMeAnBoardingPassObj(),UserName);
                stage.setTitle("Boarding Pass");
                stage.centerOnScreen();
                stage.setResizable(false);
                stage.show();
                // ticket booked payment done using BMF wallet
                // add fxml file for ticket
                // book his/her ticket
            }
        }


    }

    private void displayLabels() throws SQLException {
        Connection con = connectionProvider.getConnection();
        String query = "";
        if (FlightType.equals("Domestic"))
        query = "SELECT price from DomesticFlightData WHERE flight_id = ?";
        else
            query = "SELECT price from InternationalFlightData WHERE flight_id = ?";
        PreparedStatement pstmtGetData = con.prepareStatement(query);
        pstmtGetData.setString(1,Flight_id);
        ResultSet resultSet = pstmtGetData.executeQuery();
        while (resultSet.next()){
            FlightPriceAccToClass = Integer.parseInt(resultSet.getString("price"));
            rawFlightPriceLabel.setText("Flight Price : ₹"+resultSet.getString("price"));
        }
        con.close();
        flightTypeLabel.setText("Flight Type : "+FlightType);
        if (TravellerClass.equals("Business")&&FlightType.equals("Domestic"))
        FlightPriceAccToClass += 2500;
        if (TravellerClass.equals("Premium Economy")&&FlightType.equals("Domestic"))
            FlightPriceAccToClass += 7500;
        if (TravellerClass.equals("Business")&&FlightType.equals("International"))
        FlightPriceAccToClass += 11050;
        if (TravellerClass.equals("Premium Economy")&&FlightType.equals("International"))
            FlightPriceAccToClass += 25000;
        flightClassLabel.setText("Travel Class : "+TravellerClass+" (₹"+FlightPriceAccToClass+")");
        int priceAccToNoOfTravellers = FlightPriceAccToClass*travellers;
        noOfTravellersLabel.setText("No. of Travellers : "+travellers+" (₹"+priceAccToNoOfTravellers+")");
        int priceForOneWayOrReturn = priceAccToNoOfTravellers;
        if (isOneWay)
            oneWayOrReturn.setText("One Way Flight");
        else {
            priceForOneWayOrReturn *= 2;
            oneWayOrReturn.setText("Return Flight Price : ₹" + priceAccToNoOfTravellers);
        }

        double tax = 0.12*priceForOneWayOrReturn;
        taxApplicable.setText("Tax Applicable : ₹"+tax);
        double finalAmount = priceForOneWayOrReturn+tax;
        amountToBePaid = finalAmount;
        finalAmountLabel.setText("Final Amount : ₹"+finalAmount);
    }
    public void goBackToUserPortal(ActionEvent event) throws IOException {
        DataBaseConnection.changeScene(event,"userPortal.fxml" , "user portal",UserName);
    }

    private String getMeMyLatestBookingId() throws SQLException {
        String booking_id = null;
        Connection con = connectionProvider.getConnection();
        String query = "Select booking_id from userbookingdata where user_name = ? ";
        PreparedStatement pstmtGetMyBookingId = con.prepareStatement(query);
        pstmtGetMyBookingId.setString(1,UserName);
        ResultSet resultSet = pstmtGetMyBookingId.executeQuery();
        while (resultSet.next())
            booking_id = resultSet.getString("booking_id");
        return booking_id;
    }
    public BoardingPassData getMeAnBoardingPassObj() throws SQLException {
        BoardingPassData data;
        Connection con = connectionProvider.getConnection();
        String query = "SELECT * FROM userbookingdata where booking_id=?";
        PreparedStatement pstmtBoardingPassData = con.prepareStatement(query);
        pstmtBoardingPassData.setString(1, getMeMyLatestBookingId());
        ResultSet resultSet = pstmtBoardingPassData.executeQuery();
        resultSet.next();
            data = new BoardingPassData();
            query = "Select user_full_name from userdata where user_name = ?";
            PreparedStatement pstmtGetFullName = con.prepareStatement(query);
            pstmtGetFullName.setString(1, UserName);
            ResultSet resultSet1 = pstmtGetFullName.executeQuery();
            resultSet1.next();
            data.setUserFullName(resultSet1.getString("user_full_name"));
            String flightType = resultSet.getString("flight_type");
            if (flightType.equals("Domestic"))
                query = "Select * from DomesticFlightData where flight_id = ?";
            else
                query = "Select * from InternationalFlightData where flight_id = ?";
            ;
            PreparedStatement pstmtGetFlightData = con.prepareStatement(query);
            pstmtGetFlightData.setString(1, resultSet.getString("flight_id"));
            ResultSet resultSet2 = pstmtGetFlightData.executeQuery();
            resultSet2.next();
            data.setDepartureName(resultSet2.getString("departure_airport"));
            data.setArrivalName(resultSet2.getString("arrival_airport"));
            Time time = resultSet2.getTime("dept_time");
            data.setBoardingTime(String.valueOf(time));
            data.setAirlineName(resultSet2.getString("airline"));
            data.setFlightId(resultSet.getString("flight_id"));
            data.setDepartureDate(resultSet.getString("dept_date"));
            data.setSeatNo(resultSet.getString("seat_no"));
            data.setBookingId("00000" + resultSet.getString("booking_id"));
        return data;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String [] paymentOptions = {"BMF Wallet" , "Internet-Banking"};
        paymentMode_ChoiceBox.getItems().addAll(paymentOptions);
    }
}
