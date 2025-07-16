package Main;

import Main.Utils.RememberMe;
import Main.Utils.mailSender;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Main File that contains the psvm function for execution
// Will only be used once to launch the application rest will be done by other classes

public class Main extends Application {

    public static String stored_email = RememberMe.preferences.get(RememberMe.stored_email, null);

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/FXML_files/Form/login.fxml"));
        if(stored_email != null){
            if(RememberMe.checkUUID(stored_email)){
                loader = new FXMLLoader(getClass().getResource("/Resources/FXML_files/Form/skipLogin.fxml"));
                RememberMe.createUUID(stored_email);
            }
        }


        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bank App");
        primaryStage.show();


        primaryStage.setOnCloseRequest(windowEvent -> {
            DBconnect.closeConnection();
            mailSender.clearOtpFiles();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
