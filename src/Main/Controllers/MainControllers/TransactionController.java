package Main.Controllers.MainControllers;

import Main.DBconnect;
import Main.Models.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class TransactionController {
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Number> amountColumn;
    @FXML private TableColumn<Transaction, String> timestampColumn;
    @FXML private TableColumn<Transaction, String> senderColumn;
    @FXML private TableColumn<Transaction, String> receiverColumn;


    @FXML
    public void initialize() throws Exception{
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("senderAccount"));
        receiverColumn.setCellValueFactory(new PropertyValueFactory<>("receiverAccount"));
        fetchTransactions();
    }

    private void fetchTransactions() throws SQLException {
        Connection connection = DBconnect.getConnection();
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        String query = "SELECT amount, sender_account, receiver_account, timestamp FROM transactions WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, MainController.user_id);
        ResultSet set = statement.executeQuery();


        while(set.next()){
            transactions.add(new Transaction(
                    set.getDouble("amount"),
                    set.getString("timestamp"),
                    set.getObject("sender_account") == null ? "-" : set.getString("sender_account"),
                    set.getObject("receiver_account") == null ? "-" : set.getString("receiver_account")
            ));
        }
        transactionTable.setItems(transactions);
    }

    public void loadMainScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Menu/Main.fxml")));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

}
