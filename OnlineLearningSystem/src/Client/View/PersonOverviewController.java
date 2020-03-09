package Client.View;

import java.io.IOException;

import Client.ChatClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

public class PersonOverviewController extends GridPane {

	
	public PersonOverviewController() {
		FXMLLoader fxmlLoader =
	            new FXMLLoader(getClass().getResource("PersonOverview.fxml"));
	        fxmlLoader.setRoot(this);
	        fxmlLoader.setController(this);
	        try {
				fxmlLoader.load();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
