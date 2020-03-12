/**
 * 
 */
package Client.View;

import java.io.IOException;

import Client.Client;
import Client.UserListPane;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author hln177
 *
 */
public class LoginController extends AnchorPane{

	private final Client client;
	private Stage stage;
	@FXML private PasswordField passwordField; 
	@FXML private TextField loginField;
	@FXML private Button loginButton;
	@FXML private Button registerButton;

	
	
	 
	
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
				//this.client.addUserStatusListener(controller);
				
				loader.setRoot(controller);
				loader.setController(controller);
				
				Parent anotherRoot = loader.load();
	            Stage anotherStage = new Stage();
	            anotherStage.setTitle("Overview for "+login);
	            anotherStage.setScene(new Scene(anotherRoot));
	           
	            anotherStage.setOnCloseRequest(ActionEvent -> System.exit(1));
	            anotherStage.show();
	            stage.close();
				
			} else {
				
				
				// show error
			}
		} catch (IOException e) {
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
        
        registerStage.setOnCloseRequest(ActionEvent -> System.exit(1));
        registerStage.show();
        //stage.close();
		
	}

}
