package Client;

import org.apache.commons.lang3.StringUtils;

import Database.Messages;
import Database.Question;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {
	private final String serverName;
	private final int serverPort;
	private Socket socket;
	private OutputStream serverOut;
	private InputStream serverIn;
	private BufferedReader bufferedIn;
	private String login;
	private ObjectInputStream objectServerIn;

	private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
	private ArrayList<MessageListener> messageListeners = new ArrayList<>();

	public Client(String serverName, int serverPort) {
		this.serverName = serverName;
		this.serverPort = serverPort;

	}

	public void msg(String sendTo, String msgBody) throws IOException {
		String cmd = "msg " + sendTo + " " + msgBody + "\n";
		serverOut.write(cmd.getBytes());
	}

	public void logoff() throws IOException {
		String cmd = "logoff" + "\n";
		serverOut.write(cmd.getBytes());
	}

	public boolean login(String login, String password) throws IOException {
		String cmd = "login " + login + " " + password + "\n";
		serverOut.write(cmd.getBytes());
		String response = bufferedIn.readLine();
		System.out.println("Response Line: " + response);

		if ("ok login".equalsIgnoreCase(response)) {
			startMessageReader();
			this.login = login;

			return true;
		} else {
			return false;
		}
	}

	public String register(String login, String password, String password2) throws IOException {
		String cmd = "register " + login + " " + password + " " + password2 + "\n";
		serverOut.write(cmd.getBytes());
		String response = bufferedIn.readLine();
		System.out.println("Response Line: " + response);
		return response;
	}

	// format: invite #topic <user1> <user2>
	public boolean invite(String inviteCmd) throws IOException {
		String[] inviteTokens = StringUtils.split(inviteCmd);
		if (inviteTokens[1].charAt(0) != '#') {
			return false;
		}
		// if lose "\n",server will be always waiting
		serverOut.write((inviteCmd + "\n").getBytes());
		String response = bufferedIn.readLine();
		System.out.println("Response Line: " + response);

		if ("ok invited".equalsIgnoreCase(response)) {
			return true;
		} else {
			return false;
		}
	}

	private void startMessageReader() {
		Thread t = new Thread() {
			@Override
			public void run() {
				readMessageLoop();
			}
		};
		t.start();
	}

	private void readMessageLoop() {
		try {
			String line;
			while ((line = bufferedIn.readLine()) != null) {
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
					}
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
	public ArrayList<Messages> getChatHistory(String sendTo) throws IOException, ClassNotFoundException {
		String historyCmd = "history " + login +" "+ sendTo + "\n";
		serverOut.write((historyCmd).getBytes());
//		String response = bufferedIn.readLine();
//		System.out.println("Response Line: " + response);
		
		ArrayList<Messages> history = new ArrayList<Messages>();
		Object obj= objectServerIn.readObject();
		
		if (obj != null) {
			history = (ArrayList<Messages>)obj;
			System.out.println("history got. ");
		}

		return history;
	}

	public ArrayList<Question> getQuestions(String TopicName) throws IOException, ClassNotFoundException {
		String getQuestionCmd = "getQuestions " + TopicName + "\n";
		serverOut.write((getQuestionCmd).getBytes());
//		String response = bufferedIn.readLine();
//		System.out.println("Response Line: " + response);
		
		ArrayList<Question> questions = new ArrayList<Question>();
		Object obj= objectServerIn.readObject();
		
		System.out.println("obj received");
		
		if (obj != null) {
			questions = (ArrayList<Question>)obj;
			System.out.println("questions got. ");
		}

		return questions;
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
			this.serverOut = socket.getOutputStream();
			this.serverIn = socket.getInputStream();
			this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
			this.objectServerIn = new ObjectInputStream(serverIn);
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

	public static void main(String[] args) throws IOException {
		Client client = new Client("localhost", 8818);
		client.addUserStatusListener(new UserStatusListener() {
			@Override
			public void online(String login) {
				System.out.println("ONLINE: " + login);
			}

			@Override
			public void offline(String login) {
				System.out.println("OFFLINE: " + login);

			}
		});
		client.addMessageListener(new MessageListener() {
			@Override
			public void onMessage(String fromLogin, String msgBody, String msgTimeStamp) {
				System.out.println("You got a message from " + fromLogin + " ===>" + msgBody + " " + msgTimeStamp);
			}
		});

		if (!client.connect()) {
			System.err.println("Connect failed");
		} else {
			System.out.println("Connect successful");

			if (client.login("guest", "guest")) {
				System.out.println("Login successful");

				client.msg("DP", "hello world!");
			} else {
				System.out.println("Login failed");
			}
			// client.logoff();
		}
	}

}
