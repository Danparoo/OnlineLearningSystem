package Client;

import java.io.IOException;

import Client.View.LoginViewController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Hua Li
 */
public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) {
		LoginViewController logController = new LoginViewController();
		Scene scene = new Scene(logController);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Login");
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
