package Client.View;

import java.io.IOException;

import Client.Client;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Hua Li
 */
public class Main extends Application {

	private Stage stage;
	private Scene scene;
	private Client client;
	private LoginController controller;

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	@Override
	public void start(Stage stage) throws IOException {
		this.stage = stage;

		client = new Client("localhost", 8818);
		boolean isConnected = client.connect();

		controller = new LoginController(stage, client);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
		loader.setRoot(controller);
		loader.setController(controller);
		Parent root = loader.load();

		scene = new Scene(root);
		stage.setTitle("Login");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setOnCloseRequest(ActionEvent -> System.exit(1));

		stage.show();
		if (!isConnected) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Sorry, the server can not be found. Please try later.");
			// alert.setContentText(message);

			alert.showAndWait();
			stage.close();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
