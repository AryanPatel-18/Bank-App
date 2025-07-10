package Main.Controllers;

import Main.DBconnect;
import Main.Utils.mailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class LoginController {
    @FXML
    private Button submitButton;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    private String password;

    public void login(ActionEvent event) throws Exception {
        String email = emailField.getText().trim();

        if(!checkEmail(email)){
            showInfo("Failed", "Invalid email was entered");
            return;
        }

        otpController o = new otpController();
        mailSender m = new mailSender(email);
        m.sendMail(email);
        switchToOtpScene(event);
        o.setEmail(email);
    }

    public void switchToOtpScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/otpScreen.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void switchToLoginScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/login.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void switchToMainScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/main.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void initialize() {
        submitButton.setDisable(true);
        passwordField.textProperty().addListener((obs, oldText, newText) ->{
            submitButton.setDisable(isValidEmail(emailField.getText().trim()) || passwordField.getText().trim().isEmpty());
        });
        emailField.textProperty().addListener((obs, oldText, newText) ->{
            submitButton.setDisable(isValidEmail(emailField.getText().trim())|| passwordField.getText().trim().isEmpty());
        });
    }

    boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        boolean valid =  email != null && email.matches(emailRegex);
        return  !valid;
    }

    boolean checkEmail(String email) throws SQLException {
        Connection con = DBconnect.getConnection();
        String query = String.format("SELECT * FROM users WHERE email = '%s'",email);

        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(query);

        return rs.next();
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Error Window"); // optional: removes the header
        alert.setContentText(message);
        alert.showAndWait();
    }

}

