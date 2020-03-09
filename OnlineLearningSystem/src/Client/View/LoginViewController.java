/**
 * 
 */
package Client.View;

import java.io.IOException;

import Client.ChatClient;
import Client.UserListPane;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * @author hln177
 *
 */
public class LoginViewController extends GridPane {

	private final ChatClient client;
	@FXML private PasswordField passwordField; 
	@FXML private TextField loginField;
	@FXML private Button loginButton;
	@FXML private Button registerButton;
	
	 
	
	public LoginViewController() {
		this.client = new ChatClient("localhost", 8818);
		client.connect();
		FXMLLoader fxmlLoader =
	            new FXMLLoader(getClass().getResource("LoginView.fxml"));
	        fxmlLoader.setRoot(this);
	        fxmlLoader.setController(this);
	        try {
				fxmlLoader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	private void doLogin() {
		String login = loginField.getText();
		String password = passwordField.getText();

		try {
			if (client.login(login, password)) {
				loginButton.setOnAction(e->{
					OverallApp open = new OverallApp();
					try {
						open.start(new Stage());
						
					}catch(Exception e1) {
						e1.printStackTrace();
					}
				});
				
			} else {
				// show error
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
