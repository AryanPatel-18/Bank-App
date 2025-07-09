package Main.Controllers;

import Main.ObjectFiles.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import Main.Operations.*;

import java.sql.SQLException;

public class CreateController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField dateField;
    @FXML
    private TextField numberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField stateField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField passwordField;

    public void createAccount(ActionEvent e) throws SQLException {
        Create create = new Create();

        String password_hash = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt(12));

        create.createUserCall(new User(
                nameField.getText(),
                emailField.getText(),
                password_hash,
                numberField.getText(),
                addressField.getText(),
                stateField.getText(),
                cityField.getText(),
                dateField.getText()
        ));
    }


}
