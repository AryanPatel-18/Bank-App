package Main.Controllers;

import Main.Utils.mailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
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

        if(!Main.Utils.Validators.checkEmail(email)){
            Main.Utils.Validators.showInfo("Failed", "Invalid email was entered");
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
            submitButton.setDisable(Main.Utils.Validators.isValidEmail(emailField.getText().trim()) || passwordField.getText().trim().isEmpty());
        });
        emailField.textProperty().addListener((obs, oldText, newText) ->{
            submitButton.setDisable(Main.Utils.Validators.isValidEmail(emailField.getText().trim())|| passwordField.getText().trim().isEmpty());
        });
    }







}

