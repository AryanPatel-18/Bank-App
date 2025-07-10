package Main.Controllers;

import Main.DBconnect;
import Main.Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import Main.Utils.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateController {
    @FXML
    private TextField nameField;
    @FXML
    private DatePicker dateField;
    @FXML
    private TextField numberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField addressField;
    @FXML
    private ComboBox<String> stateField;
    @FXML
    private ComboBox<String> cityField;
    @FXML
    private PasswordField passwordField;

    public void createAccount(ActionEvent e) throws SQLException {
        Create create = new Create();

        String password_hash = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt(12));
        String email = emailField.getText();

        if(checkAllFields()){
            Main.Utils.Validators.showInfo("Empty Fields", "Please enter all the values");
            return;
        }
        if(Main.Utils.Validators.isValidEmail(email)){
            Main.Utils.Validators.showInfo("Invalid Email", "The email you have entered is not valid");
            emailField.clear();
            return;
        }
        if(Main.Utils.Validators.checkEmail(email)){
            Main.Utils.Validators.showInfo("Invalid Email", "The email you have entered already exists");
            emailField.clear();
            return;
        }
        if(!Main.Utils.Validators.isValidIndianPhoneNumber(numberField.getText())){
            Main.Utils.Validators.showInfo("Invalid number", "The number you have entered is not valid");
            numberField.clear();
            return;
        }

        create.createUserCall(new User(
                nameField.getText(),
                emailField.getText(),
                password_hash,
                numberField.getText(),
                addressField.getText(),
                stateField.getValue(),
                cityField.getValue(),
                dateField.getValue()
        ));
    }

    @FXML
    public void initialize(){
        ArrayList<String> states = new ArrayList<>();

        // Setting the states in the state combo box
        try{
            Connection connection = DBconnect.getConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT name FROM states";
            ResultSet set = statement.executeQuery(query);

            while(set.next()){
                states.add(set.getString("name"));
            }

            stateField.getItems().addAll(states);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Setting the city values in the city combo box according to the value entered in the state combo box
    @FXML
    public void setCityValue(ActionEvent e) throws SQLException {
        Connection connection = DBconnect.getConnection();
        String state = stateField.getValue();
        String query = "SELECT name FROM cities WHERE state_id = (SELECT id FROM states WHERE name = ?)";
        ArrayList<String> city = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, state);

        System.out.println(statement);

        ResultSet set = statement.executeQuery();

        while(set.next()){
            city.add(set.getString("name"));
        }

        cityField.getItems().clear();
        cityField.getItems().addAll(city);
    }

    boolean checkAllFields() {
        return (
                emailField.getText().isEmpty() ||
                nameField.getText().isEmpty() ||
                numberField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                cityField.getValue() == null ||
                stateField.getValue() == null ||
                dateField.getValue() == null ||
                addressField.getText().isEmpty()
        );
    }

}
