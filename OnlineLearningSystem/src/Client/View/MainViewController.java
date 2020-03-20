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
import Database.Question;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
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

	private ObservableList<String> testStrList = FXCollections.observableArrayList();
	@FXML
	private ListView<String> testList = new ListView<>(userStrList);

	@FXML
	private Button inviteButton;

	@FXML
	private Button nextQueButton;

	ArrayList<Question> questions;
	@FXML
	private Text questionContent;
	@FXML
	private RadioButton AOption;
	@FXML
	private RadioButton BOption;
	@FXML
	private RadioButton COption;
	@FXML
	private RadioButton DOption;
	private int currentQueIndex = 0;
	private String[] answer;

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
				try {
					client.getChatHistory(newValue);
					try {
						Thread.currentThread().sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ArrayList<Messages> history = client.getMessageMap().get(newValue);
					if (history != null) {
						for (Messages msg : history) {
							String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(new Date(msg.getSendtime().getTime()));
							if (msg.getFromuser().equalsIgnoreCase(client.getLogin())) {
								messageStrList.add("You: " + msg.getPostcontent() + " \n" + time);
								messageList.setItems(messageStrList);
							}
							messageStrList.add(msg.getFromuser() + ": " + msg.getPostcontent() + " \n" + time);
							messageList.setItems(messageStrList);

						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				System.out.println("Selected user: " + newValue);
			}
		});

		testStrList.add("Arithmetic");
		testStrList.add("Geography");
		testList.setItems(testStrList);

		ToggleGroup Tgroup = new ToggleGroup();
		AOption.setUserData("a");
		AOption.setToggleGroup(Tgroup);
		BOption.setUserData("b");
		BOption.setToggleGroup(Tgroup);
		COption.setUserData("c");
		COption.setToggleGroup(Tgroup);
		DOption.setUserData("d");
		DOption.setToggleGroup(Tgroup);

		testList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//				try {
//					Thread getQuestions = new Thread() {
//						@Override
//						public void run() {
//							client.send("getQuestions " + newValue);
//						}
//					};
//					getQuestions.start();
//					getQuestions.join();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				client.send("getQuestions " + newValue);
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				questions = client.getQuestionMap().get(newValue);
				if (questions != null) {
					currentQueIndex = 0;
					Question q = questions.get(currentQueIndex);
					answer = new String[questions.size()];
					questionContent.setText(
							q.getQuestioncontent() + "(" + (currentQueIndex + 1) + "/" + questions.size() + ")");
					AOption.setText(q.getA());
					BOption.setText(q.getB());
					COption.setText(q.getC());
					DOption.setText(q.getD());
				} else {
					questionContent.setText("No question got");
				}

			}
		});
		Tgroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (Tgroup.getSelectedToggle() != null) {
					String selectedOption = Tgroup.getSelectedToggle().getUserData().toString();
					System.out.println(selectedOption);
					answer[currentQueIndex] = selectedOption;
				}
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
			messageStrList.add("You: " + text + "\n" + nowtime);
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

	@FXML
	public void lastQue() {
		if (questions != null && currentQueIndex > 0 && currentQueIndex < questions.size() - 1) {
			currentQueIndex--;
			setNewQuestion();
		} else if (currentQueIndex == questions.size() - 1) {
			nextQueButton.setText("Next");
			currentQueIndex--;
			setNewQuestion();
		} else if (currentQueIndex == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("It is the first question");
			// alert.setContentText(message);
			alert.showAndWait();
		}
	}

	@FXML
	public void nextQue() {
		if (questions != null && currentQueIndex < questions.size() - 2) {
			currentQueIndex++;
			setNewQuestion();
		} else if (currentQueIndex == questions.size() - 2) {
			currentQueIndex++;
			setNewQuestion();
			nextQueButton.setText("Submit");
			// handle submit
		} else if (currentQueIndex == questions.size() - 1) {
			int score = 0;
			int i = 0;
			for (Question q : questions) {
				if (answer[i] != null && q.getCorrectans().equals(answer[i])) {
					score++;
				}
				i++;
			}

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Result!");
			alert.setHeaderText("You got " + score + "/" + answer.length + " marks!");
			// alert.setContentText(message);
			alert.showAndWait();
			nextQueButton.setText("Next");
		}

	}

	public void setNewQuestion() {
		Question q = questions.get(currentQueIndex);
		questionContent.setText(q.getQuestioncontent() + "(" + (currentQueIndex + 1) + "/" + questions.size() + ")");
		AOption.setText(q.getA());
		BOption.setText(q.getB());
		COption.setText(q.getC());
		DOption.setText(q.getD());

		if (answer[currentQueIndex] != null) {
			if (answer[currentQueIndex].equals("a")) {
				AOption.setSelected(true);
			} else if (answer[currentQueIndex].equals("b")) {
				BOption.setSelected(true);
			} else if (answer[currentQueIndex].equals("c")) {
				COption.setSelected(true);
			} else if (answer[currentQueIndex].equals("d")) {
				DOption.setSelected(true);
			}
		} else {
			AOption.setSelected(false);
			BOption.setSelected(false);
			COption.setSelected(false);
			DOption.setSelected(false);

		}
	}

	@Override
	public void online(String login) {
		userStrList.add(login);
		System.out.println(login + " online");
		userList.setItems(userStrList);
	}

	@Override
	public void offline(String login) {
		boolean isTopic = login.charAt(0) == '#';
		if (isTopic) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Group Chat Ended");
			alert.setHeaderText("Someone leave the group chat "+login+". So this group chat is ended");
			// alert.setContentText(message);
			alert.showAndWait();
		}
		userStrList.remove(login);
		System.out.println(login + " offline");
		userList.setItems(userStrList);
	}

	@Override
	public void onMessage(String fromLogin, String msgBody, String msgTimeStamp) {
		String login = userList.getSelectionModel().getSelectedItem();
		if (login != null && login.equalsIgnoreCase(fromLogin)) {
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(msgTimeStamp)));
			messageStrList.add(fromLogin + ": " + msgBody + " \n" + time);
			// messageStrList.add(nowtime);
			messageList.setItems(messageStrList);
		}
//		 else if (userStrList.contains(fromLogin)) {
//			int i = userStrList.indexOf(fromLogin);
//			userStrList.set(i, fromLogin + " *");
//			userList.setItems(userStrList);
//		}
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
