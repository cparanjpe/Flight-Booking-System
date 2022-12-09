package com.example.airlinesbooking;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class transactionCardController {

    @FXML
    private Label amount;

    @FXML
    private HBox card;

    @FXML
    private Label from;

    @FXML
    private Label to;

    @FXML
    private Label transactionId;


    public void setData(transactionData obj){
        from.setText(obj.getSender());
        to.setText(obj.getReceiver());
        amount.setText("₹"+obj.getAmount());
        transactionId.setText("Transaction Id : "+obj.getTransactionId());
    }
}
