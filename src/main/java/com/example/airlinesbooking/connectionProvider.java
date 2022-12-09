package com.example.airlinesbooking;
import java.sql.*;
public class connectionProvider {
    // creating reference

    // public method with return type connection object
    public static Connection getConnection(){
        try {
            Connection con = null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://127.0.0.1:3306/airline-booking";
            String user = "root";
            String pass = "ruYIUtdyeWT83dehZFJK";
            con = DriverManager.getConnection(url, user, pass);
            return con;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
