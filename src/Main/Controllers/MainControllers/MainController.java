package Main.Controllers.MainControllers;

import Main.DBconnect;
import Main.Models.Transaction;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.util.Duration;

import javax.swing.*;
import java.sql.*;
import java.util.Optional;

public class MainController {

    @FXML
    private Button viewIdLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Button addMoneyButton;
    @FXML
    private Label accountTypeLabel;
    @FXML
    private Label accountIDLabel;
    @FXML
    private Button showBalanceButton;
    @FXML
    private Label customerIDLabel;
    @FXML
    private Label moneyLabel;
    public static String email = "aryanpatel2593@gmail.com";
    public static int user_id;
    Connection connection = DBconnect.getConnection();

    public void initialize() throws Exception{
        // Apply animation to all buttons
        addClickAnimation(viewIdLabel);
        getUserId();
        addClickAnimation(addMoneyButton);
        addClickAnimation(showBalanceButton);
        nameLabel.setText(getAAccountName());
        accountTypeLabel.setText(getAccountType());
        accountIDLabel.setText(getAccountId());
    }

    public static void setEmail(String email){
        MainController.email = email;
    }

    private void getUserId() throws SQLException{
        String query = "SELECT user_id FROM users WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();

        if(set.next())
            MainController.user_id = set.getInt(1);
        else
            System.out.println("There was a problem in fetching the user id ");
    }


    private void addClickAnimation(javafx.scene.Node node) {
        node.setOnMouseClicked(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), node);
            st.setFromX(1.0);
            st.setFromY(1.0);
            st.setToX(1.05);   // Scale up to 120%
            st.setToY(1.05);
            st.setAutoReverse(true);
            st.setCycleCount(2);  // Scale up and back down

            st.play();
        });
    }

    private String getAAccountName() throws SQLException {
        String query = "SELECT full_name FROM users WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();

        if(set.next())
            return set.getString(1);
        return null;
    }

    private String getAccountType() throws SQLException{
        String query = "SELECT account_type FROM bank_account WHERE user_id = (SELECT user_id FROM users WHERE email = ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();
        if(set.next())
            return set.getString(1);
        return null;
    }

    private String getAccountId() throws SQLException{
        String query = "SELECT account_number FROM bank_account WHERE user_id = (SELECT user_id FROM users WHERE email = ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        System.out.println(email);
        ResultSet set = statement.executeQuery();

        if(set.next())
            return String.valueOf(set.getLong(1));
        return null;
    }

    public void checkBalance(ActionEvent e) throws SQLException{
        moneyLabel.setText(checkBalance());
    }

    private String checkBalance() throws SQLException{
        String query = "SELECT balance FROM bank_account WHERE user_id = (SELECT user_id FROM users WHERE email = ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1,email);
        ResultSet set = statement.executeQuery();

        if(set.next())
            return String.valueOf(set.getDouble(1));
        return null;
    }

    public void checkCustomerId(ActionEvent e) throws SQLException{
        String query = "SELECT user_id FROM users WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();

        if(set.next())
            customerIDLabel.setText(String.valueOf(set.getInt(1)));
        else
            System.out.println("There was a problem while fetching the account id");
    }

    private void addMoney() throws SQLException{
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Amount required");
        dialog.setHeaderText("Please enter the amount");
        dialog.setContentText("Amount : ");

        Optional<String> result = dialog.showAndWait();
        double amount = result.isPresent()?Double.parseDouble(result.get()):0;
        Transaction.updateBalanceCall(user_id, amount);
        moneyLabel.setText(checkBalance());
    }

    public void addMoneCall(ActionEvent e) throws SQLException {
        addMoney();
    }
}
