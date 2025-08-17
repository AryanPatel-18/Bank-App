package Main.Controllers.MainControllers;

import Main.Controllers.FormControllers.LoginController;
import Main.DBconnect;
import Main.Models.Transaction;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Objects;
import java.util.Optional;
import java.util.PropertyPermission;
import java.util.prefs.Preferences;

import static Main.FormUtils.RememberMe.preferences;


public class MainController {

    @FXML private Button viewIdLabel;
    @FXML private Label nameLabel;
    @FXML private Button addMoneyButton;
    @FXML private Label accountTypeLabel;
    @FXML private Label accountIDLabel;
    @FXML private Button showBalanceButton;
    @FXML private Label customerIDLabel;
    @FXML private Label moneyLabel;
    @FXML private Label depositLabel;
    @FXML private Label withdrawLabel;
    @FXML private Label TransactionLabel;
    @FXML private ImageView profileImageView;

    public static String email = "aryanpatel2593@gmail.com";
    public static int user_id;
    Connection connection = DBconnect.getConnection();

    @FXML
    public void initialize() throws Exception{
        // Apply animation to all buttons
        addClickAnimation(viewIdLabel);
        getUserId();
        addClickAnimation(addMoneyButton);
        addClickAnimation(showBalanceButton);
        nameLabel.setText(getAAccountName());
        accountTypeLabel.setText(getAccountType());
        accountIDLabel.setText(getAccountId());
        getProfilePicture();
        getValues();
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

    public void loadTransactionScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Menu/Transactions.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
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

        String query = "SELECT account_number FROM bank_account WHERE user_id = " + user_id;
        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery(query);


        Optional<String> result = dialog.showAndWait();
        double amount = result.isPresent()?Double.parseDouble(result.get()):0;
        if(set.next())
            Transaction.updateBalanceCall(user_id, amount, set.getLong(1), set.getLong(1));
        moneyLabel.setText(checkBalance());
        getValues();
    }

    public void addMoneyCall(ActionEvent e) throws SQLException {
        addMoney();
    }

    public void getProfilePicture() throws SQLException{
        connection.setAutoCommit(false);
        String query = "SELECT photo FROM user_photos WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, user_id);
        ResultSet set = statement.executeQuery();
        InputStream inputStream;
        if(set.next()){
            byte[] imageBytes = set.getBytes("photo");
            // Load image directly from InputStream
            Image image = new Image(new ByteArrayInputStream(imageBytes));
            profileImageView.setImage(image);
            // Apply circular clip
            double radius = profileImageView.getFitWidth() / 2;
            Circle clip = new Circle(radius, radius, radius);
            profileImageView.setClip(clip);
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    void getValues() throws SQLException{
        String query = "SELECT user_id FROM users WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();
        int userId = 0;

        if(set.next())
            userId = set.getInt(1);

        String queryValues = "SELECT get_transaction_sum(?,?)";
        PreparedStatement debitStatement = connection.prepareStatement(queryValues);
        PreparedStatement creditStatement = connection.prepareStatement(queryValues);
        debitStatement.setInt(1, userId);
        creditStatement.setInt(1, userId);
        debitStatement.setString(2, "debit");
        creditStatement.setString(2, "credit");
        double creditValue = 0;
        double debitValue = 0;

        ResultSet debitSet = debitStatement.executeQuery();
        ResultSet creditSet = creditStatement.executeQuery();

        if(debitSet.next())
            debitValue = Math.abs(debitSet.getDouble(1));
        if(creditSet.next())
            creditValue = Math.abs(creditSet.getDouble(1));

        String lastStatementQuery = "SELECT amount FROM transactions WHERE user_id = ? ORDER BY timestamp DESC LIMIT 1";
        PreparedStatement transactionStatement = connection.prepareStatement(lastStatementQuery);
        transactionStatement.setInt(1, userId);
        ResultSet lastSet = transactionStatement.executeQuery();
        double lastTransactionValue = 0;

        if(lastSet.next())
            lastTransactionValue = Math.abs(lastSet.getDouble(1));

        depositLabel.setText(String.valueOf(creditValue));
        withdrawLabel.setText(String.valueOf(debitValue));
        TransactionLabel.setText(String.valueOf(lastTransactionValue));
    }

    public void switchToProfileScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Menu/CustomerInformation.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void switchToTransferScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Menu/TransferMoney.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void logout(ActionEvent event) throws Exception{
        LoginController login = new LoginController();
        login.switchToLoginScene(event);
        preferences.clear();
    }
}
