package Main.Controllers.FormControllers;

import Main.Controllers.MainControllers.MainController;
import Main.DBconnect;
import Main.FormUtils.BCrypt;
import Main.FormUtils.RememberMe;
import Main.FormUtils.Validators;
import Main.FormUtils.mailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

// Login file contains all the methods that are required for the login fxml file form


public class LoginController {
    @FXML
    private RadioButton rememberMe;
    @FXML
    private Button submitButton;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;



    private static String resetEmail;
    private static Scene scene;

    // Main login controller file ( activates when the submit button is clicked )
    public void login(ActionEvent event) throws Exception {
        String email = emailField.getText().trim();

        // Validating the email
        if(!Validators.checkEmail(email)){
            Validators.showInfo("Failed", "Invalid email was entered");
            return;
        }

        String password = getPasswordHash(email);
        System.out.println("Password : "+password);
        // Checking the password and sending the otp
        System.out.println(password);
        if(BCrypt.checkpw(passwordField.getText(), password)){
            otpController o = new otpController();
            mailSender m = new mailSender(email);
            m.sendMail(email);
            if(rememberMe.isSelected())
                storeInformation();
            else
                RememberMe.preferences.clear();
            switchToOtpScene(event);
            o.setEmail(email);
        }else{
            Main.FormUtils.Validators.showInfo("Login Failed", "password entered was incorrect please try again");
            passwordField.clear();
        }
    }


    // Reset password ( executed when the reset button is pressed )
    public void resetPassword(ActionEvent e) throws SQLException, IOException {
        resetEmail = emailField.getText().trim();
        mailSender mail = new mailSender(resetEmail);

        if(resetEmail.isEmpty()){
            Main.FormUtils.Validators.showInfo("No Input", "Please enter the email");
            emailField.clear();
            return;
        }

        otpController o = new otpController();
        // Updating all the static emails in the otp file and rest file
        ResetPasswordController.setResetEmail(resetEmail);
        o.setEmail(resetEmail);

        if(!Validators.checkEmail(resetEmail)){
            Validators.showInfo("Failed", "Invalid email was entered");
            return;
        }
        mail.sendMail(resetEmail);
        switchToResetpasswordOtpScreen(e);
    }

    // All the switching scenes required for the login page as well as all the forms
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

    public void switchToAdminScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Menu/Admin.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    // initialize function ( executed when the fxml file is loaded )
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

    // Fetching the hash from the database
    private String getPasswordHash(String email) throws SQLException {
        Connection connection = DBconnect.getConnection();
        String query = "SELECT password_hash FROM users WHERE email = ?";
        String adminQuery = "SELECT password_hash FROM admin_accounts WHERE email = ?";
        String hash = "";

        PreparedStatement statement = connection.prepareStatement(query);
        PreparedStatement adminStatement = connection.prepareStatement(adminQuery);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();
        adminStatement.setString(1, email);
        ResultSet adminSet = adminStatement.executeQuery();

        if(set.next())
            hash = set.getString(1);
        if(adminSet.next())
            hash = adminSet.getString(1);
        return hash;
    }

    private void storeInformation() throws Exception {
        String email = emailField.getText();
        RememberMe.preferences.clear();
        RememberMe.createUUID(email);
    }

}

