/**
 * 
 */
package Client.View;

import java.io.IOException;

import Client.Client;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author hln177
 *
 */
public class LoginController extends AnchorPane {

	private final Client client;
	private Stage stage;
	@FXML
	private PasswordField passwordField;
	@FXML
	private TextField loginField;
	@FXML
	private Button loginButton;
	@FXML
	private Button registerButton;

	public LoginController(Stage stage, Client client) {
		this.client = client;
		this.stage = stage;
	}

	@FXML
	private void doLogin() {

		String login = loginField.getText();
		String password = passwordField.getText();

		try {
			if (client.login(login, password)) {
				MainViewController controller = new MainViewController(stage, client);
				FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
				// this.client.addUserStatusListener(controller);

				loader.setRoot(controller);
				loader.setController(controller);

				Parent anotherRoot = loader.load();
				Stage anotherStage = new Stage();
				anotherStage.setTitle("Overview for " + login);
				anotherStage.setScene(new Scene(anotherRoot));

				anotherStage.setOnCloseRequest(ActionEvent -> {
					try {
						client.logoff();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.exit(1);
				});
				anotherStage.show();
				stage.close();

			} else {

		        Alert alert = new Alert(AlertType.ERROR);
		        alert.setTitle("Error");
		        alert.setHeaderText("Invalid login/password. ");
		        //alert.setContentText(message);
		        alert.showAndWait();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void goToRegister() throws IOException {
		Stage registerStage = new Stage();
		registerStage.setTitle("Register");

		RegisterController controller = new RegisterController(registerStage, client);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterView.fxml"));

		loader.setRoot(controller);
		loader.setController(controller);
		Parent registerRoot = loader.load();

		registerStage.setScene(new Scene(registerRoot));

		registerStage.show();
		// stage.close();

	}

}
