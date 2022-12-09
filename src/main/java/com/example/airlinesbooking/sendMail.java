package com.example.airlinesbooking;

import javafx.scene.control.Alert;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class sendMail{
    public static void send_Mail(String To, String msgg) {
        // Recipient's email ID needs to be mentioned.
        String to = To;//ye change hoga acc to user who is logged in

        // Sender's email ID needs to be mentioned
        String from = "bookmyflight22@outlook.com";

        try {


            Properties props = new Properties();
//            props.put("mail.smtp.socketFactory.port", "587");
//            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            props.put("mail.smtp.socketFactory.fallback", "true");
            props.put("mail.smtp.host", "smtp-mail.outlook.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("bookmyflight22@outlook.com","thck2022");
                        }
                    });

//            Session emailSession = Session.getDefaultInstance(props, null);

            String msgBody = msgg;

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            msg.setSubject("BookmyFlight Confirmation: Ticket Booked");
            msg.setText(msgBody);
            Transport.send(msg);
            //logger.error("Email sent successfully...");
        } catch (Exception e) {
            // logger.error(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Turn on your Internet to get emails");
            alert.show();
        }
    }
}