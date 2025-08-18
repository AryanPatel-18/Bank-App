package Main.Controllers.FormControllers;

import Main.FormUtils.mailSender;
import Main.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.prefs.BackingStoreException;

import static Main.FormUtils.RememberMe.preferences;

public class skipLoginController {
    @FXML
    public Label emailLabel;
    @FXML
    public Button yesButton;
    @FXML
    public Button noButton;
    LoginController login = new LoginController();

    @FXML
    private void initialize(){
        emailLabel.setText(Main.stored_email);
    }

    public void yesButton(ActionEvent event) throws IOException {
        otpController.setRemember(true);
        mailSender m = new mailSender(Main.stored_email);
        m.sendMail(Main.stored_email);
        login.switchToOtpScene(event);
    }

    public void noButton(ActionEvent event) throws IOException, BackingStoreException {
        otpController.setRemember(false);
        preferences.clear();
        login.switchToLoginScene(event);
    }

}
