package Main.Controllers;

import Main.DBconnect;
import Main.Utils.BCrypt;
import Main.Utils.Validators;
import Main.Utils.mailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;




public class LoginController {
    @FXML
    private Button submitButton;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Button resetButton;
    private String password;
    private static String resetEmail;

    public void login(ActionEvent event) throws Exception {
        String email = emailField.getText().trim();

        if(!Validators.checkEmail(email)){
            Validators.showInfo("Failed", "Invalid email was entered");
            return;
        }

        String password = getPasswordHash(email);

        if(BCrypt.checkpw(passwordField.getText(), password)){
            otpController o = new otpController();
            mailSender m = new mailSender(email);
            m.sendMail(email);
            switchToOtpScene(event);
            o.setEmail(email);
        }else{
            Main.Utils.Validators.showInfo("Login Failed", "password entered was incorrect please try again");
            passwordField.clear();
        }
    }


    public void resetPassword(ActionEvent e) throws SQLException, IOException {
        resetEmail = emailField.getText().trim();
        mailSender mail = new mailSender(resetEmail);

        if(resetEmail.isEmpty()){
            Main.Utils.Validators.showInfo("No Input", "Please enter the email");
            emailField.clear();
            return;
        }

        otpController o = new otpController();
        ResetPasswordController.setResetEmail(resetEmail);
        o.setEmail(resetEmail);
        if(!Validators.checkEmail(resetEmail)){
            Validators.showInfo("Failed", "Invalid email was entered");
            return;
        }
        mail.sendMail(resetEmail);
        switchToResetpasswordOtpScreen(e);
    }

    public void switchToOtpScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Form/otpScreen.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void switchToLoginScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Form/login.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void switchToMainScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Menu/Main.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void switchToResetpasswordScreen(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Form/ResetPassword.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void switchToResetpasswordOtpScreen(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Form/ResetOtpScreen.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void initialize() {
        submitButton.setDisable(true);
        passwordField.textProperty().addListener((obs, oldText, newText) ->{
            submitButton.setDisable(Validators.isValidEmail(emailField.getText().trim()) || passwordField.getText().trim().isEmpty());
        });
        emailField.textProperty().addListener((obs, oldText, newText) ->{
            submitButton.setDisable(Validators.isValidEmail(emailField.getText().trim())|| passwordField.getText().trim().isEmpty());
        });
    }

    private String getPasswordHash(String email) throws SQLException {
        Connection connection = DBconnect.getConnection();
        String query = "SELECT password_hash FROM users WHERE email = ?";
        String hash = "";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();

        while(set.next()){
            hash = set.getString("password_hash");
        }

        return hash;
    }

}

