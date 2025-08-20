package Main.Controllers.FormControllers;

import Main.Controllers.MainControllers.MainController;
import Main.DBconnect;
import Main.FormUtils.RememberMe;
import Main.FormUtils.Validators;
import Main.FormUtils.mailSender;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// This file contains all the methods that are required for the opt generation, deletion as well as validation

public class otpController {

    @FXML
    private TextField otpField;
    @FXML
    private Button submitButton;
    @FXML
    private Label invalidMessage;
    public static String email;
    public static boolean remember;
    LoginController login = new LoginController();

    // Checks the otp value from the text file in which it is temporarily saved
    public void checkValue(ActionEvent e) throws Exception {
        mailSender m = new mailSender();
        boolean adminAccount = isAdminAccount();
        String actualEmail = remember?RememberMe.preferences.get(RememberMe.stored_email, null):email;


        int enteredOtp = Integer.parseInt(otpField.getText().trim());
        if(m.checkOtp(enteredOtp, actualEmail)){
            System.out.println("valid otp entered");
            MainController.setEmail(actualEmail);
            if(adminAccount)
                login.switchToAdminScene(e);
            else
                login.switchToMainScene(e);
        }else{
            Validators.showInfo("Invalid Otp", "You have entered the wrong otp");
            if(remember) RememberMe.preferences.clear();
            login.switchToLoginScene(e);
        }
    }

    // For checking the otp for reset password
    public void checkOtpResetPassword(ActionEvent e) throws IOException {
        mailSender m = new mailSender();
        int enteredOtp = Integer.parseInt(otpField.getText().trim());
        System.out.println(enteredOtp);
        if(m.checkOtp(enteredOtp, email)){
            System.out.println("valid otp entered");
            login.switchToResetpasswordScreen(e);
        }else{
            Validators.showInfo("Invalid Otp", "You have entered the wrong otp");
            login.switchToLoginScene(e);
            System.out.println(email);
        }
    }

    @FXML
    private void initialize() {

        // disables the submit button until the otp that is entered is of 4 digits and only contains numbers
        otpField.textProperty().addListener((obs, oldText, newText) -> {
            submitButton.setDisable(true);
            if(otpField.getText().matches("\\d{4}")){
                submitButton.setDisable(false);
                invalidMessage.setOpacity(0);
//                System.out.println("Valid value has been entered");
            }else{
                submitButton.setDisable(true);
                invalidMessage.setOpacity(1);
            }
        });
    }

    // For setting the static email variable
    void setEmail(String email){
        otpController.email = email;
    }
    public static void setRemember(boolean value) {remember = value; }

    private boolean isAdminAccount() throws SQLException {
        String actualEmail = remember?RememberMe.preferences.get(RememberMe.stored_email, null):email;
        Connection connection = DBconnect.getConnection();

        String query = "SELECT * FROM admin_accounts WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, actualEmail);
        ResultSet set = statement.executeQuery();

        return set.next();
    }
}
