package Main.FormUtils;

import Main.DBconnect;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// This file contains all the methods that would be used for data validation as well as an alert prompt method

public class Validators {
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        boolean valid =  email != null && email.matches(emailRegex);
        return  !valid;
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Error Window"); // optional: removes the header
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean checkEmail(String email) throws SQLException {
        Connection con = DBconnect.getConnection();
        String query = String.format("SELECT * FROM users WHERE email = '%s'",email);

        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(query);

        return rs.next();
    }

    public static boolean isValidIndianPhoneNumber(String number) {
        // Check if number is exactly 10 digits and starts with 6-9
        return number != null && number.matches("[6-9]\\d{9}");
    }
}
