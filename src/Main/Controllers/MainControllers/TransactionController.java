package Main.Controllers.MainControllers;

import Main.DBconnect;
import Main.FormUtils.FileCreation;
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

    public static String startDate;
    public static String endDate;
    public static ObservableList<Transaction> transactions = FXCollections.observableArrayList();

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
        transactions = FXCollections.observableArrayList();

        String query = "SELECT amount, sender_account, receiver_account, timestamp " +
                "FROM transactions " +
                "WHERE user_id = ? AND timestamp BETWEEN ? AND ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, MainController.user_id);
        statement.setDate(2, java.sql.Date.valueOf(startDate));
        statement.setDate(3, java.sql.Date.valueOf(endDate));

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

    public void convertFile(ActionEvent event){
        FileCreation creation = new FileCreation();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        creation.exportToExcel(transactions,stage , event);
    }

}
