package Main.FormUtils;

import Main.DBconnect;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;

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
        String adminQuery = String.format("SELECT * FROM admin_accounts WHERE email = '%s'",email);

        Statement statement = con.createStatement();
        Statement adminStatement = con.createStatement();
        ResultSet rs = statement.executeQuery(query);
        ResultSet rsa = adminStatement.executeQuery(adminQuery);

        return rs.next() || rsa.next();
    }

    public static boolean isValidIndianPhoneNumber(String number) {
        // Check if number is exactly 10 digits and starts with 6-9
        return number != null && number.matches("[6-9]\\d{9}");
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        return str.matches("\\d+(\\.\\d+)?");
    }

    public static boolean isAfterToday(LocalDate inputDate) {
        LocalDate today = LocalDate.now();
        return inputDate.isAfter(today);
    }

    public static boolean isValidSql(String sql) throws SQLException {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        String trimmed = sql.trim().toLowerCase();

        return trimmed.startsWith("select") ||
                trimmed.startsWith("insert") ||
                trimmed.startsWith("update") ||
                trimmed.startsWith("delete") ||
                trimmed.startsWith("create") ||
                trimmed.startsWith("drop") ||
                trimmed.startsWith("alter") ||
                trimmed.startsWith("truncate") ||
                trimmed.startsWith("replace") ||
                trimmed.startsWith("merge") ||
                trimmed.startsWith("grant") ||
                trimmed.startsWith("revoke") ||
                trimmed.startsWith("with") ||
                trimmed.startsWith("call") ||
                trimmed.startsWith("explain");
    }

    public static void main(String[] args) throws SQLException {
        System.out.println(isValidSql("asdasdasd"));
    }
}
