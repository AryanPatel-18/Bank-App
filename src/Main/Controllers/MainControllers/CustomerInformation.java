package Main.Controllers.MainControllers;

import Main.Controllers.FormControllers.LoginController;
import Main.DBconnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;

public class CustomerInformation {
    Connection connection = DBconnect.getConnection();
    @FXML private ImageView profileImageView;
    @FXML private Label customerName;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label birthLabel;
    @FXML private Label stateLabel;
    @FXML private Label cityLabel;
    @FXML private Label joiningLabel;


    public static int user_id = MainController.user_id;

    public void getProfilePicture() throws SQLException {
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
//            double radius = profileImageView.getFitWidth() / 2;
//            Circle clip = new Circle(radius, radius, radius);
//            profileImageView.setClip(clip);
        }
        connection.commit();
        connection.setAutoCommit(true);
    }


    @FXML
    public void initialize() throws Exception{
        getProfilePicture();
        setInformation();
    }

    public void setInformation() throws Exception{
        String query = "SELECT full_name,email, phone_number, created_at, address, user_state, city, birthdate FROM users WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, user_id);
        ResultSet set = statement.executeQuery();

        if(set.next()){
            customerName.setText(set.getString("full_name"));
            emailLabel.setText(set.getString("email"));
            addressLabel.setText(set.getString("address"));
            stateLabel.setText(set.getString("user_state"));
            cityLabel.setText(set.getString("city"));
            birthLabel.setText(set.getString("birthdate"));
            phoneLabel.setText(set.getString("phone_number"));

            Timestamp ts = set.getTimestamp("created_at");
            LocalDate dateOnly = ts.toLocalDateTime().toLocalDate();
            joiningLabel.setText(dateOnly.toString());
        }
    }

    public void switchToMainScene(ActionEvent event) throws IOException {
        LoginController login = new LoginController();
        login.switchToMainScene(event);
    }
}
