package Client.View;

import java.io.IOException;

import Client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RegisterController extends AnchorPane {
	private final Client client;
	private Stage stage;

	@FXML
	private TextField loginField;
	@FXML
	private PasswordField passwordField1;
	@FXML
	private PasswordField passwordField2;

	@FXML
	private Button registerButton;

	public RegisterController(Stage stage, Client client) {
		this.client = client;
		this.stage = stage;
	}

	@FXML
	private void doRegister() {

		String login = loginField.getText();
		String password1 = passwordField1.getText();
		String password2 = passwordField2.getText();

		try {
			if (client.register(login, password1, password2)) {


				this.stage.hide();

			} else {

				// show error
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
