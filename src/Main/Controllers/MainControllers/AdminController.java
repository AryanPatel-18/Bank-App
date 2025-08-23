package Main.Controllers.MainControllers;

import Main.AiConnection;
import Main.Controllers.FormControllers.LoginController;
import Main.DBconnect;
import Main.FormUtils.Validators;
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
import java.sql.*;
import java.util.Objects;
import java.util.Optional;


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
        else
            return;

        if(!Validators.isNumeric(accountNumber)){
            Validators.showInfo("Invalid Value","Please enter a number only");
            return;
        }

        if(controller.accountExists(Long.parseLong(accountNumber))){
            MainController.user_id = controller.getReceiverId(Long.parseLong(accountNumber));
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
        String accountNumber;
        String reason;

        TransferController controller = new TransferController();

        if(result.isPresent()){
            accountNumber = result.get();

            if(!Validators.isNumeric(accountNumber)){
                Validators.showInfo("Invalid Value","Please enter a valid account number");
                return;
            }

            if(controller.accountExists(Long.parseLong(accountNumber))){
                dialog.setTitle("Input Required");
                dialog.setHeaderText("Please Enter the reason");
                dialog.setContentText("reason:");
                Optional<String> result1 = dialog.showAndWait();

                if(result1.isPresent())
                    reason = result1.get();
                else{
                    Validators.showInfo("Reason required","You need to provide the reason");
                    return;
                }
                if(reason.isEmpty()){
                    Validators.showInfo("Reason required","You need to provide the reason");
                    return;
                }
            }else {
                Validators.showInfo("Invalid Account number","The account number does not exist");
                return;
            }
        }else {
            return;
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
        String delete3 = "DELETE FROM transactions WHERE sender_account = ? OR receiver_account = ? ";
        PreparedStatement statement3 = connection.prepareStatement(delete3);


        statement1.setLong(1, Long.parseLong(accountNumber));
        statement2.setInt(1, user_id);
        statement3.setLong(1, Long.parseLong(accountNumber));
        statement3.setLong(2, Long.parseLong(accountNumber));

        statement3.executeUpdate();

        Thread.sleep(1000);

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

    public void createQuery(ActionEvent event){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Required");
        dialog.setHeaderText("Please Enter the account number");
        dialog.setContentText("number:");
        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            if(!AiConnection.validatePrompt(result.get(), event))
                Validators.showInfo("Invalid Prompt","Please don't use any prompts that updates the table");
        }else{
            Validators.showInfo("Error","Please enter a prompt");
        }
    }

    public void createAndExecuteSql(ActionEvent event) throws Exception{
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Required");
        dialog.setHeaderText("Please enter your sql query");
        dialog.setContentText("query:");
        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            String prompt = result.get();
            if(Validators.isValidSql(prompt)){
                SelectController.rs = AiConnection.getResultSet(prompt);
                if((SelectController.rs == null)){
                    Validators.showInfo("Invalid Result set","No statements returned");
                    return;
                }
                loadSelectScene(event);
            }
            else
                Validators.showInfo("Invalid sql","Please enter a valid sql query");
        }
    }

    public void loadSelectScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Menu/SelectView.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }



}
