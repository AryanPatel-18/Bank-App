package Main.Controllers;

import Main.Utils.mailSender;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;

public class otpController {

    @FXML
    private TextField otpField;
    @FXML
    private Button submitButton;
    @FXML
    private Label invalidMessage;
    public static String email;
    LoginController login = new LoginController();

    public void checkValue(ActionEvent e) throws IOException {
        mailSender m = new mailSender();
        int enteredOtp = Integer.parseInt(otpField.getText().trim());
        if(m.checkOtp(enteredOtp, email)){
            System.out.println("valid otp entered");
            login.switchToMainScene(e);
        }else{
            System.out.println("invalid otp");
            login.switchToLoginScene(e);
        }
    }

    @FXML
    private void initialize() {
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

        submitButton.setOnMousePressed(mouseEvent ->{
            ScaleTransition st = new ScaleTransition(Duration.millis(100),submitButton);
            st.setToX(0.98);
            st.setToY(0.98);
            st.play();
        });

        submitButton.setOnMouseReleased(mouseEvent -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), submitButton);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    void setEmail(String email){
        otpController.email = email;
    }


}
