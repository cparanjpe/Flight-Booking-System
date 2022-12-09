package com.example.airlinesbooking;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ticketLayoutController{

    @FXML
    private VBox bookingTicketCardKoismeDalnaHai;
    public String UserName;
    public void setUserName(String user_name){
        this.UserName = user_name;
        displayMyPasses();
    }

    private List<BoardingPassData> addDataToArrayFromDataBase() throws SQLException {

        List<BoardingPassData> ls = new ArrayList<>();
        BoardingPassData data;
        Connection con = connectionProvider.getConnection();
        String query = "SELECT * FROM userbookingdata where user_name=?";
        PreparedStatement pstmtBoardingPassData = con.prepareStatement(query);
        pstmtBoardingPassData.setString(1,UserName);
        ResultSet resultSet = pstmtBoardingPassData.executeQuery();
        while (resultSet.next()) {
            data = new BoardingPassData();
            query = "Select user_full_name from userdata where user_name = ?";
            PreparedStatement pstmtGetFullName = con.prepareStatement(query);
            pstmtGetFullName.setString(1,UserName);
            ResultSet resultSet1 = pstmtGetFullName.executeQuery();
            resultSet1.next();
            data.setUserFullName(resultSet1.getString("user_full_name"));
            String flightType = resultSet.getString("flight_type");
            if(flightType.equals("Domestic"))
            query = "Select * from DomesticFlightData where flight_id = ?";
            else
                query = "Select * from InternationalFlightData where flight_id = ?";;
            PreparedStatement pstmtGetFlightData = con.prepareStatement(query);
            pstmtGetFlightData.setString(1,resultSet.getString("flight_id"));
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
            data.setBookingId("00000"+resultSet.getString("booking_id"));
            ls.add(data);
        }
        return ls;
    }


    public void displayMyPasses() {
        List<BoardingPassData> data = null;
        try {
            data = new ArrayList<>(addDataToArrayFromDataBase());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i=0;i< data.size() ; i++){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ticketCard.fxml"));
            HBox hBox = null;
            try {
                hBox = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ticketCardController cardController = fxmlLoader.getController();
            cardController.setData(data.get(i));
            bookingTicketCardKoismeDalnaHai.getChildren().add(hBox);

        }
    }
}
