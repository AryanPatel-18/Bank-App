package Main.Controllers;

import Main.DBconnect;
import Main.Utils.BCrypt;
import Main.Utils.Validators;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResetPasswordController {
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private Button resetButton;
    private static String resetEmail;
    @FXML
    private void initialize() {
        resetButton.setDisable(true);

        ChangeListener<String> fieldListener = (obs, oldText, newText) -> {
            boolean bothFilled = !newPassword.getText().isEmpty() && !confirmPassword.getText().isEmpty();
            resetButton.setDisable(!bothFilled);
        };

        newPassword.textProperty().addListener(fieldListener);
        confirmPassword.textProperty().addListener(fieldListener);
    }

    public void verifyPassword(ActionEvent e) throws SQLException, IOException {
        Connection connection = DBconnect.getConnection();
        LoginController login = new LoginController();

        String password = newPassword.getText();
        String verifyPassword = confirmPassword.getText();


        if(password.equals(verifyPassword)){
            String query = "UPDATE users SET password_hash = ? WHERE email = ?";
            System.out.println(resetEmail);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, BCrypt.hashpw(password, BCrypt.gensalt(12)));
            statement.setString(2, resetEmail);
            int rows = statement.executeUpdate();
            System.out.println(rows>0?"Password was updated":"Password was not updated");

            if(rows > 0){
                login.switchToMainScene(e);
            }else{
                login.switchToLoginScene(e);
            }
        }else{
            Main.Utils.Validators.showInfo("Password Mismatch", " Please make sure both the passwords are the same");
            newPassword.clear();
            confirmPassword.clear();
        }
    }

    public static void setResetEmail(String email){
        resetEmail = email;
    }
}
