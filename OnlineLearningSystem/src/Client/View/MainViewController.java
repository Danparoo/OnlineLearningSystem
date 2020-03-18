package Client.View;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import Client.Client;
import Client.MessageListener;
import Client.UserStatusListener;
import Database.Messages;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainViewController extends AnchorPane implements UserStatusListener, MessageListener {

	private Stage stage;
	private Scene scene;
	private Client client;
	private LoginController controller;
	@FXML
	TextArea messageArea;

	private ObservableList<String> userStrList = FXCollections.observableArrayList();
	@FXML
	private ListView<String> userList = new ListView<>(userStrList);

	private ObservableList<String> messageStrList = FXCollections.observableArrayList();
	@FXML
	private ListView<String> messageList = new ListView<>(userStrList);

	@FXML
	private Button inviteButton;

	public MainViewController(Stage stage, Client client) throws IOException {
		this.client = client;
		this.stage = stage;

		client.addUserStatusListener(this);
		client.addMessageListener(this);
	}

	public void initialize() {
		userList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				messageList.getItems().clear();
//				try {
//					ArrayList<Messages> history = client.getChatHistory(newValue);
//					for (Messages msg : history) {
//						String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//								.format(new Date(msg.getSendtime().getTime()));
//						messageStrList.add(msg.getFromuserid() + ": " + msg.getPostcontent() + " \n" + time);
//						messageList.setItems(messageStrList);
//
//					}
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}

				System.out.println("Selected user: " + newValue);
			}
		});
	}

	@FXML
	public void sendMessage() throws IOException {
		String text = messageArea.getText();
		String login = userList.getSelectionModel().getSelectedItem();
		client.msg(login, text);
		String nowtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		if (!(login.charAt(0) == '#')) {
			messageStrList.add("You: " + text + " \n" + nowtime);
			// messageStrList.add(nowtime);
			messageList.setItems(messageStrList);
		}

		messageArea.setText("");
	}

	@FXML
	public void goToInvitation() throws IOException {
		if (userStrList.size() < 2) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("You need at least 2 onine user to create a group chat.");
			// alert.setContentText(message);

			alert.showAndWait();
			return;
		}

		Stage invitationStage = new Stage();
		invitationStage.setTitle("Invite to a group chat");

		InvitationController controller = new InvitationController(invitationStage, client, userStrList);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("InvitationView.fxml"));

		loader.setRoot(controller);
		loader.setController(controller);
		Parent invitationRoot = loader.load();

		invitationStage.setScene(new Scene(invitationRoot));
		invitationStage.show();
	}

	@Override
	public void online(String login) {
		userStrList.add(login);
		System.out.println(login + " online");
		userList.setItems(userStrList);
	}

	@Override
	public void offline(String login) {
		userStrList.remove(login);
		System.out.println(login + " offline");
		userList.setItems(userStrList);

	}

	@Override
	public void onMessage(String fromLogin, String msgBody, String msgTimeStamp) {
		String login = userList.getSelectionModel().getSelectedItem();
		if (login.equalsIgnoreCase(fromLogin)) {
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(msgTimeStamp)));
			messageStrList.add(fromLogin + ": " + msgBody + " \n" + time);
			// messageStrList.add(nowtime);
			messageList.setItems(messageStrList);

		}

	}

	public static void main(String[] args) throws IOException {
		Stage anotherStage = new Stage();
		MainViewController controller = new MainViewController(anotherStage, new Client("localhost", 8818));
		FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("MainView.fxml"));

		loader.setRoot(controller);
		loader.setController(controller);

		Parent anotherRoot = loader.load();

		anotherStage.setTitle("Overview demo ");
		anotherStage.setScene(new Scene(anotherRoot));

		anotherStage.setOnCloseRequest(ActionEvent -> System.exit(1));
		anotherStage.show();
	}

}
