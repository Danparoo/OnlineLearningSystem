package Client.View;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Client.Client;
import Client.MessageListener;
import Client.UserStatusListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainViewController extends AnchorPane implements UserStatusListener, MessageListener{

	private Stage stage;
	private Scene scene;
	private Client client;
	private LoginController controller;
	private ObservableList<String> userStrList = FXCollections.observableArrayList();
	@FXML
	private ListView<String> userList = new ListView<>(userStrList);

	@FXML
	TextArea messageArea;
	
	private ObservableList<String> messageStrList = FXCollections.observableArrayList();
	@FXML
	private ListView<String> messageList = new ListView<>(userStrList);

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
		        // Your action here
		    	messageList.getItems().clear();
		        System.out.println("Selected item: " + newValue);
		    }
		});
	}

	@FXML
	public void sendMessage() throws IOException {
		String text = messageArea.getText();
		String login = userList.getSelectionModel().getSelectedItem();
		client.msg(login, text);
		String nowtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		messageStrList.add("       You: " + text + " \n       " + nowtime);
		//messageStrList.add(nowtime);
		messageList.setItems(messageStrList);

		messageArea.setText("");
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
			messageStrList.add( fromLogin + ": " + msgBody+" \n" +time);
			//messageStrList.add(nowtime);
			messageList.setItems(messageStrList);

			
		}
		
	}
	

}
