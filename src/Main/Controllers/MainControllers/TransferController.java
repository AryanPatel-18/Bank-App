package Main.Controllers.MainControllers;

import Main.Controllers.FormControllers.LoginController;
import Main.DBconnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;


public class TransferController {
    @FXML private TextField numberField;
    @FXML private TextField amountField;
    Connection connection = DBconnect.getConnection();
    private static final int user_id = MainController.user_id;
    private static long user_number  = 0;

    public void switchToAdminScene(ActionEvent event) throws IOException {
        LoginController login = new LoginController();
        login.switchToAdminScene(event);
    }

    @FXML
    public void initialize() throws Exception{
        user_number = getAccountNumber();
    }

    private double getBalance() throws SQLException {
        double balance = 0;
        String query = "SELECT balance FROM bank_account WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, user_id);
        ResultSet set = statement.executeQuery();

        if(set.next())
            balance = set.getDouble(1);
        return balance;
    }

    public void sendAmount(ActionEvent event) throws Exception{
        connection.setAutoCommit(false);
        double enteredAmount = Double.parseDouble(amountField.getText());
        long accountNumber = Long.parseLong(numberField.getText());


        LoginController login = new LoginController();

        double currentBalance = getBalance();

        if(enteredAmount <= 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Value"); // optional, can show a header
            alert.setContentText("Please enter an amount greater than 0");
            alert.show();
            login.switchToMainScene(event);
        }

        if(enteredAmount > currentBalance){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Not enough balance"); // optional, can show a header
            alert.setContentText("The balance you have is not sufficient for the entered amount");
            alert.show();
            login.switchToMainScene(event);
        }

        if(!accountExists(accountNumber)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Number"); // optional, can show a header
            alert.setContentText("The account number you have entered is not valid");
            alert.show();
            login.switchToMainScene(event);
        }

        double newBalance = currentBalance - enteredAmount;
        double receiverBalance = 0;
        String query = "SELECT balance FROM bank_account WHERE account_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, accountNumber);
        ResultSet set = statement.executeQuery();
        if(set.next())
            receiverBalance = set.getDouble(1);

        receiverBalance += enteredAmount;

        String updateQuery = "UPDATE bank_account SET balance = " + receiverBalance + " WHERE account_number = " + accountNumber;
        Statement statement1 = connection.createStatement();
        int row  = statement1.executeUpdate(updateQuery);
        System.out.println(row>0?"updated balance" :"Could not update the balance");

        String updateUserQuery = "UPDATE bank_account SET balance = " + newBalance + " WHERE account_number = " + user_number;
        Statement statement2 = connection.createStatement();
        int row1 = statement2.executeUpdate(updateUserQuery);
        System.out.println(row1>0?"updated user balance" :"Could not update user balance");

        //Adding Transactions
        String insertUserQuery = "INSERT INTO transactions (user_id, transaction_type, amount, sender_account, receiver_account) VALUES (?,?,?,?,?)";
        String insertReceiverQuery = "INSERT INTO transactions (user_id, transaction_type, amount, sender_account, receiver_account) VALUES (?,?,?,?,?)";

        PreparedStatement userStatement = connection.prepareStatement(insertUserQuery);
        PreparedStatement receiverStatement = connection.prepareStatement(insertReceiverQuery);

        userStatement.setInt(1,user_id);
        userStatement.setString(2,"debit");
        userStatement.setDouble(3, enteredAmount);
        userStatement.setLong(4, user_number);
        userStatement.setLong(5, accountNumber);

        receiverStatement.setInt(1,getReceiverId(accountNumber));
        receiverStatement.setString(2,"credit");
        receiverStatement.setDouble(3, enteredAmount);
        receiverStatement.setLong(4, user_number);
        receiverStatement.setLong(5, accountNumber);

        int row2 = userStatement.executeUpdate();
        int row3 = receiverStatement.executeUpdate();

        System.out.println((row2>0 && row3 > 0)?"Added transactions":"Could not add transactions");
        connection.commit();
        connection.setAutoCommit(true);
        login.switchToMainScene(event);
    }

    private boolean accountExists(long number) throws SQLException{
        String query = "SELECT * FROM bank_account WHERE account_number = ? ";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, number);
        ResultSet set = statement.executeQuery();
        return set.next();
    }

    private Long getAccountNumber() throws SQLException{
        String query = "SELECT account_number FROM bank_account WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, user_id);
        ResultSet set = statement.executeQuery();
        set.next();
        return set.getLong(1);
    }

    private int getReceiverId(long number) throws SQLException{
        String query = "SELECT user_id FROM bank_account WHERE account_number = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, number);
        ResultSet set = statement.executeQuery();
        set.next();
        return set.getInt(1);
    }

}
