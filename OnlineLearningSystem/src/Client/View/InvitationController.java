package Client.View;

import java.io.IOException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.CheckListView;

import Client.Client;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class InvitationController extends AnchorPane {
	private final Client client;
	private Stage stage;

	@FXML
	private TextField groupNameField;
	@FXML
	private TextArea groupDescriptionField;

	@FXML
	private Button finishButton;
	@FXML
	private Button cancelButton;

	private ObservableList<String> onlineStrList = FXCollections.observableArrayList();
	@FXML
	private ListView<String> onlineList = new ListView<>(onlineStrList);

	private ObservableList<String> selectedStrList = FXCollections.observableArrayList();
	@FXML
	private ListView<String> selectedList = new ListView<>(selectedStrList);

	public InvitationController(Stage stage, Client client, ObservableList<String> onlineStrList) {
		this.client = client;
		this.stage = stage;
		this.onlineStrList = onlineStrList;
	}

	public void initialize() {
		onlineList.setItems(onlineStrList);
		//onlineList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		onlineList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// ensure nothing selected after removal:
//				onlineList.getSelectionModel().clearSelection();				
//				onlineStrList.remove(newValue);
//				onlineList.setItems(onlineStrList);
				if (!selectedStrList.contains(newValue)) {
					selectedStrList.add(newValue);
					selectedList.setItems(selectedStrList);
					System.out.println("Selected item: " + newValue);
				}
			}
		});

	}

	// format: invite #topic <user1> <user2>
	@FXML
	private void finishInvitation() throws IOException {
		if (groupNameField.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Please enter the group name.");
			// alert.setContentText(message);
			alert.showAndWait();
			return;
		}
		if (selectedStrList.size() < 2) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("You need select least 2 onine user to create a group chat.");
			// alert.setContentText(message);

			alert.showAndWait();
			return;
		}
		// Convert elements to strings and concatenate them, separated by " "
		String inviteCmd = selectedList.getItems().stream().map(Object::toString).collect(Collectors.joining(" "));

		inviteCmd = "invite " + "#" + groupNameField.getText() + " " + client.getLogin() + " " + inviteCmd;
		System.out.println(inviteCmd);
		try {
			client.invite(inviteCmd);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stage.close();

	}

	@FXML
	private void cancel() {
		stage.close();
	}

}
