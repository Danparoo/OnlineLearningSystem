package Client.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OverallApp extends Application {
	
	@Override
	public void start(Stage primaryStage)  {
		PersonOverviewController overviewController = new PersonOverviewController();
		Scene scene = new Scene(overviewController);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Login");
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
