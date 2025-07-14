package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Main File that contains the psvm function for execution
// Will only be used once to launch the application rest will be done by other classes

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/FXML_files/Form/createAccount.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bank App");
        primaryStage.show();

        primaryStage.setOnCloseRequest(windowEvent -> {
            DBconnect.closeConnection();
            System.out.println("The connection was closed");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
