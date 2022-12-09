package com.example.airlinesbooking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class internationalFlightLayoutController implements Initializable {

    @FXML
    private VBox cardKoIsmeDalnaHai_international;
    @FXML
    private ChoiceBox<String> search_international_arrival;

    @FXML
    private ChoiceBox<String> search_international_departure;

    @FXML
    private Button search_international_searchBtn;
    private List<FlightData> data;
    private int sizeOfInitializeList;


    public void search_InternationalFlights(ActionEvent event) throws SQLException, IOException {
        for (int i = 0; i < sizeOfInitializeList; i++) {
            cardKoIsmeDalnaHai_international.getChildren().remove(0);
        }
        List<FlightData> ls = new ArrayList<>();
        FlightData data;
        Connection con = connectionProvider.getConnection();
        String query = "SELECT * FROM InternationalFlightData where departure_airport=? and arrival_airport=?";
        PreparedStatement pstmtInternational = con.prepareStatement(query);
        pstmtInternational.setString(1, search_international_departure.getValue());
        pstmtInternational.setString(2, search_international_arrival.getValue());
        ResultSet resultSet = pstmtInternational.executeQuery();
        while (resultSet.next()) {
            data = new FlightData();
            data.setAirline(resultSet.getString(3));
            data.setDepartutre(resultSet.getString(1));
            data.setArrival(resultSet.getString(2));
            data.setDepartTime(resultSet.getString(4));
            data.setArrivalTime(resultSet.getString(5));
            data.setPrice(resultSet.getString(7));
            data.setFlightId(resultSet.getString(8));
            data.setFlightType("International");
            ls.add(data);
        }

        sizeOfInitializeList = ls.size();
        for (int i = 0; i < ls.size(); i++) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FlightCard.fxml"));
            HBox hBox = fxmlLoader.load();
            FlightCardController cardController = fxmlLoader.getController();
            cardController.setData(ls.get(i));
            cardKoIsmeDalnaHai_international.getChildren().add(hBox);
        }
       con.close();
    }




    private List<FlightData> addDataToArrayFromDataBase() throws SQLException {
        List<FlightData> ls = new ArrayList<>();
        FlightData data;
        Connection con = connectionProvider.getConnection();
        String query = "SELECT * FROM InternationalFlightData";
        PreparedStatement pstmtInternational = con.prepareStatement(query);
        ResultSet resultSet = pstmtInternational.executeQuery();
        while (resultSet.next()){
            data = new FlightData();
            data.setAirline(resultSet.getString(3));
            data.setDepartutre(resultSet.getString(1));
            data.setArrival(resultSet.getString(2));
            data.setDepartTime(resultSet.getString(4));
            data.setArrivalTime(resultSet.getString(5));
            data.setPrice(resultSet.getString(7));
            data.setFlightId(resultSet.getString(8));
            data.setFlightType("International");
            ls.add(data);
        }
        sizeOfInitializeList = ls.size();

        return ls;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] airports = {"Mumbai" , "Delhi" , "Lucknow" , "Chennai" , "Paris" , "London" , "Tokyo" , "Riyadh" , "Abu Dhabi"};
        search_international_departure.getItems().addAll(airports);
        search_international_arrival.getItems().addAll(airports);
        try {
            data = new ArrayList<>(addDataToArrayFromDataBase());

            for (int i=0;i< data.size() ; i++){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FlightCard.fxml"));
                HBox hBox = fxmlLoader.load();
                FlightCardController cardController = fxmlLoader.getController();
                cardController.setData(data.get(i));
                cardKoIsmeDalnaHai_international.getChildren().add(hBox);

            }


        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
