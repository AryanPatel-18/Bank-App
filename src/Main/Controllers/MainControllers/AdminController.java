package Main.Controllers.MainControllers;

import Main.Controllers.FormControllers.LoginController;
import Main.DBconnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import static Main.FormUtils.RememberMe.preferences;


public class AdminController {

    public static long accountNumber = 0;
    public static String email = "cixeyi4568@mardiek.com";

    @FXML
    public void initialize(){
        CustomerInformation.isAdmin = false;
    }


    public void switchToCreateAccountScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Form/createAccount.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    public void viewInformation(ActionEvent event) throws Exception{
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Required");
        dialog.setHeaderText("Please Enter the account number");
        dialog.setContentText("number:");
        TransferController controller = new TransferController();
        LoginController login = new LoginController();
        MainController controller1 = new MainController();

        Optional<String> result = dialog.showAndWait();
        String accountNumber = "";
        if(result.isPresent())
            accountNumber = result.get();

        if(controller.accountExists(Long.parseLong(accountNumber))){
            CustomerInformation.user_id = controller.getReceiverId(Long.parseLong(accountNumber));
            CustomerInformation.isAdmin = true;
            controller1.switchToProfileScene(event);
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Number"); // optional, can show a header
            alert.setContentText("The account number you have entered is not valid");
            alert.show();
            login.switchToAdminScene(event);
        }
    }

    public void suspendAccount(ActionEvent event) throws Exception{
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Required");
        dialog.setHeaderText("Please Enter the account number");
        dialog.setContentText("number:");
        Optional<String> result = dialog.showAndWait();
        String accountNumber = "";
        String reason = "";
        boolean valid = true;

        TransferController controller = new TransferController();

        if(result.isPresent()){
            accountNumber = result.get();
            if(controller.accountExists(Long.parseLong(accountNumber))){
                dialog.setTitle("Input Required");
                dialog.setHeaderText("Please Enter the reason");
                dialog.setContentText("reason:");
                Optional<String> result1 = dialog.showAndWait();

                if(result1.isPresent())
                    reason = result1.get();
                else valid = false;
            }else valid = false;
        }else valid = false;

        if(!valid){
            LoginController login = new LoginController();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid Number"); // optional, can show a header
            alert.setContentText("Suspension failed please enter valid information");
            alert.show();
            login.switchToAdminScene(event);
        }

        Connection connection = DBconnect.getConnection();
        String query = "INSERT INTO deletion_logs (account_number, deleted_by, reason, admin_id) VALUES (?,?,?,?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, Long.parseLong(accountNumber));
        statement.setString(2, email);
        statement.setString(3, reason);
        statement.setInt(4, getAdminId(email));

        int row = statement.executeUpdate();
        System.out.println(row>0?"deleted":"not deleted");
        TransferController controller1 = new TransferController();
        int user_id = controller1.getReceiverId(Long.parseLong(accountNumber));

        String delete2 = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement statement2 = connection.prepareStatement(delete2);
        String delete1 = "DELETE FROM bank_account WHERE account_number = ?";
        PreparedStatement statement1 = connection.prepareStatement(delete1);
        String delete3 = "DELETE FROM transactions WHERE user_id = ?";
        PreparedStatement statement3 = connection.prepareStatement(delete3);

        statement1.setLong(1, Long.parseLong(accountNumber));
        statement2.setInt(1, user_id);
        statement3.setInt(1, user_id);

        statement3.executeUpdate();
        statement1.executeUpdate();
        statement2.executeUpdate();

    }

    public void logout(ActionEvent event) throws Exception{
        LoginController login = new LoginController();
        login.switchToLoginScene(event);
    }

    int getAdminId(String email) throws SQLException {
        Connection connection = DBconnect.getConnection();
        String query = "SELECT id FROM admin_accounts WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, email);
        ResultSet set = statement.executeQuery();
        if(set.next())
            return set.getInt(1);
        else{
            System.out.println("email : " + email);
            return 1;
        }
    }

}
