package Main.Controllers.MainControllers;

import Main.Controllers.FormControllers.LoginController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SelectController {
    @FXML TableView<ObservableList<String>> tableView;

    public static ResultSet rs;

    public void populateTableView() throws SQLException {
        tableView.getItems().clear();
        tableView.getColumns().clear();

        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            final int colIndex = i - 1;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(meta.getColumnName(i));
            column.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(data.getValue().get(colIndex))
            );
            tableView.getColumns().add(column);
        }

        while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getString(i));
            }
            tableView.getItems().add(row);
        }
    }

    @FXML
    public void initialize() throws Exception{
        populateTableView();
    }

    public void goBack(ActionEvent event) throws Exception{
        LoginController login = new LoginController();
        login.switchToAdminScene(event);
    }

}
