package Main.Controllers.MainControllers;

import Main.FormUtils.Validators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TransactionMenuController {

    @FXML private Button weekButton;
    @FXML private Button monthButton;
    @FXML private Button yearButton;
    @FXML private Button customButton;
    @FXML private DatePicker startDateSelector;
    @FXML private DatePicker endDateSelector;

    String currentDate;

    String getOneWeekBefore(String endDate) {
        LocalDate localEnd = Date.valueOf(endDate).toLocalDate();
        LocalDate start = localEnd.minusWeeks(1);
        return start.toString();
    }

    String getOneMonthBefore(String endDate) {
        LocalDate localEnd = Date.valueOf(endDate).toLocalDate();
        LocalDate start = localEnd.minusMonths(1);
        return start.toString();
    }

    String getOneYearBefore(String endDate) {
        LocalDate localEnd = Date.valueOf(endDate).toLocalDate();
        LocalDate start = localEnd.minusYears(1);
        return start.toString();
    }

    public void setMonth(ActionEvent event){
        TransactionController.startDate = getOneMonthBefore(currentDate);
        loadTransactionScene(event);
    }

    public void setWeek(ActionEvent event){
        TransactionController.startDate = getOneWeekBefore(currentDate);
        loadTransactionScene(event);
    }

    public void setYear(ActionEvent event){
        TransactionController.startDate = getOneYearBefore(currentDate);
        loadTransactionScene(event);
    }

    public void customDate(ActionEvent event) throws Exception{
        String startDate = startDateSelector.getValue().toString();
        String endDate = endDateSelector.getValue().toString();

        if(isValidEndDate(endDate, currentDate)){
            TransactionController.startDate = startDate;
            TransactionController.endDate = endDate;
            loadTransactionScene(event);
        }else{
            Validators.showInfo("Invalid date", "Please enter the date once again");
        }
    }

    boolean isValidEndDate(String inputDate, String currentDate){
        LocalDate chosenEndDate = LocalDate.parse(inputDate);
        LocalDate today = LocalDate.parse(currentDate);

        return !chosenEndDate.isAfter(today);
    }

    String getCurrentDate(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return today.format(formatter);
    }

    @FXML
    public void initialize(){
        currentDate = getCurrentDate();
        TransactionController.endDate = currentDate;
    }

    void loadTransactionScene(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Menu/Transactions.fxml")));
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadCustomDateScene(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Resources/FXML_files/Menu/CustomDate.fxml")));
            Scene scene = new Scene(root);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


