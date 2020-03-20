package Client;

import org.apache.commons.lang3.StringUtils;

import Database.Messages;
import Database.Question;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Client {
	private final String serverName;
	private final int serverPort;
	private Socket socket;
	// private OutputStream serverOut;
	// private BufferedReader bufferedIn;
	private String login;
	private ObjectOutputStream objectServerOut;
	private ObjectInputStream objectServerIn;
	// flag = 1 means there is something doing, GUI should wait
	// private int flag = 0;

	private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
	private ArrayList<MessageListener> messageListeners = new ArrayList<>();
	private HashMap<String, ArrayList<Question>> questionMap = new HashMap<>();
	private HashMap<String, ArrayList<Messages>> messageMap = new HashMap<>();

	/**
	 * @return the messageMap
	 */
	public HashMap<String, ArrayList<Messages>> getMessageMap() {
		return messageMap;
	}

	/**
	 * @return the questionMap
	 */
	public HashMap<String, ArrayList<Question>> getQuestionMap() {
		return questionMap;
	}

	public Client(String serverName, int serverPort) {
		this.serverName = serverName;
		this.serverPort = serverPort;

	}

	public void msg(String sendTo, String msgBody) throws IOException {
		String cmd = "msg " + sendTo + " " + msgBody;
		objectServerOut.writeObject(cmd);
	}

	public void logoff() throws IOException {
		String cmd = "logoff" + "\n";
		objectServerOut.writeObject(cmd);
	}

	public boolean login(String login, String password) throws IOException, ClassNotFoundException {
		String cmd = "login " + login + " " + password + "\n";
		objectServerOut.writeObject(cmd);

		Object obj = objectServerIn.readObject();

		if (obj != null) {
			String response = (String) obj;
			System.out.println("Response Line: " + response);
			if ("ok login\n".equalsIgnoreCase(response)) {
				startMessageReader();
				this.login = login;
				return true;
			} else {
				return false;
			}
		}
		return false;

	}

	public String register(String login, String password, String password2) throws IOException, ClassNotFoundException {
		String cmd = "register " + login + " " + password + " " + password2 + "\n";
		objectServerOut.writeObject(cmd);
		Object obj = objectServerIn.readObject();
		String response = "";
		if (obj != null) {
			response = (String) obj;
			System.out.println("Response Line: " + response);
		}
		return response;
	}

	// format: invite #topic <user1> <user2>
	public boolean invite(String inviteCmd) throws IOException, ClassNotFoundException {
		String[] inviteTokens = StringUtils.split(inviteCmd);
		if (inviteTokens[1].charAt(0) != '#') {
			return false;
		}
		objectServerOut.writeObject(inviteCmd);
		return true;
	}

	private void startMessageReader() {
		Thread readLoop = new Thread() {
			@Override
			public void run() {
				try {
					readMessageLoop();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		readLoop.start();
	}

	private void readMessageLoop() throws ClassNotFoundException {
		try {
			String line;
			Object buf;
			while (true) {
				try {
					buf = objectServerIn.readObject();
					line = (String) buf;

					String[] tokens = StringUtils.split(line);
					if (tokens != null && tokens.length > 0) {
						String cmd = tokens[0];
						if ("online".equalsIgnoreCase(cmd)) {
							handleOnline(tokens);
						} else if ("offline".equalsIgnoreCase(cmd)) {
							handleOffline(tokens);
						} else if ("msg".equalsIgnoreCase(cmd)) {
							String[] tokensMsg = StringUtils.split(line, null, 4);
							handleMessage(tokensMsg);
						} else if ("questions".equalsIgnoreCase(cmd)) {
							ArrayList<Question> questions = new ArrayList<Question>();
							Object obj = objectServerIn.readObject();

							System.out.println("questions obj received");

							if (obj != null) {
								questions = (ArrayList<Question>) obj;
								System.out.println("questions got. ");
							}
							String topicName = tokens[1];
							questionMap.put(topicName, questions);
						} else if ("history".equalsIgnoreCase(cmd)) {
							ArrayList<Messages> msg = new ArrayList<Messages>();
							Object obj = objectServerIn.readObject();

							System.out.println("messages obj received");

							if (obj != null) {
								msg = (ArrayList<Messages>) obj;
								System.out.println("history got. ");
							}
							String sendTo = tokens[1];
							messageMap.put(sendTo, msg);
						}
					}
				} catch (EOFException e) {
					System.out.println("Loop ended");
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	// format: "history" <user1> <user2>
	public void getChatHistory(String sendTo) throws IOException, ClassNotFoundException {
		String historyCmd = "history " + login + " " + sendTo + "\n";
		objectServerOut.writeObject(historyCmd);
	}

	public void send(String cmd) {
		try {
			objectServerOut.writeObject(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getQuestion(String cmd) {
		try {
			objectServerOut.writeObject(cmd);
//			flag = 1;
//			while (true) {
//				if (flag == 0)
//					break;
//			}
			System.out.println("while ended");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleMessage(String[] tokensMsg) {
		String login = tokensMsg[1];
		String msgTimeStamp = tokensMsg[2];
		String msgBody = tokensMsg[3];

		for (MessageListener listener : messageListeners) {
			listener.onMessage(login, msgBody, msgTimeStamp);
		}
	}

	private void handleOffline(String[] tokens) {
		String login = tokens[1];
		for (UserStatusListener listener : userStatusListeners) {
			listener.offline(login);
		}
	}

	private void handleOnline(String[] tokens) {
		String login = tokens[1];
		try {
			Thread.currentThread().sleep(1000);// 毫秒
		} catch (Exception e) {
		}
		for (UserStatusListener listener : userStatusListeners) {
			listener.online(login);
		}
	}

	public boolean connect() {
		try {
			this.socket = new Socket(serverName, serverPort);
			System.out.println("Client port is " + socket.getLocalPort());
			this.objectServerOut = new ObjectOutputStream(socket.getOutputStream());
			this.objectServerIn = new ObjectInputStream(socket.getInputStream());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void addUserStatusListener(UserStatusListener listener) {
		userStatusListeners.add(listener);
	}

	public void removeUserStatusListener(UserStatusListener listener) {
		userStatusListeners.remove(listener);
	}

	public void addMessageListener(MessageListener listener) {
		messageListeners.add(listener);
	}

	public String getLogin() {
		return login;
	}

	public void removeaMessageListener(MessageListener listener) {
		messageListeners.remove(listener);
	}

}
