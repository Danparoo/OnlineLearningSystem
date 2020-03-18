package Server;

import org.apache.commons.lang3.StringUtils;

import Database.Database;
import Database.Messages;
import Database.Question;
import Database.examDatabase;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread {

	private final Socket clientSocket;
	private final Server server;
	private String login = null;
	private OutputStream outputStream;
	private ObjectOutputStream objectOutputStream;
	private HashSet<String> topicSet = new HashSet<>();

	public ServerWorker(Server server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		try {
			handleClientSocket();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void handleClientSocket() throws IOException, InterruptedException {
		InputStream inputStream = clientSocket.getInputStream();
		this.outputStream = clientSocket.getOutputStream();
		this.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = StringUtils.split(line);

			// deal with first command
			if (tokens != null && tokens.length > 0) {
				String cmd = tokens[0];
				if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
					handleLogoff();
					break;
				} else if ("login".equalsIgnoreCase(cmd)) {
					handleLogin(outputStream, tokens);
				} else if ("register".equalsIgnoreCase(cmd)) {
					handleRegister(tokens);

				} else if ("msg".equalsIgnoreCase(cmd)) {
					String[] tokensMsg = StringUtils.split(line, null, 3);
					handleMessege(tokensMsg);

				} else if ("invite".equalsIgnoreCase(cmd)) {
					handleInvite(tokens);
				} else if ("join".equalsIgnoreCase(cmd)) {
					handleJoin(tokens);
				} else if ("leave".equalsIgnoreCase(cmd)) {
					handleLeave(tokens);
				} else if ("history".equalsIgnoreCase(cmd)) {
					handleHistory(tokens);
				} else if ("getQuestions".equalsIgnoreCase(cmd)) {
					handleGetQuestions(tokens);

				} else {
					String msg = "unknow " + cmd + "\n";
					outputStream.write(msg.getBytes());
				}
			}
		}

		clientSocket.close();
	}

	// format: invite #topic <user1> <user2>
	private void handleInvite(String[] tokens) {
		// TODO Auto-generated method stub
		if (tokens.length > 3 && tokens[1].charAt(0) == '#') {

			String topicName = tokens[1];
			String[] selfJoinCmd = { "join", topicName };
			handleJoin(selfJoinCmd);
			List<ServerWorker> workerList = server.getWorkerList();
			for (int i = 2; i < tokens.length; i++) {
				for (ServerWorker worker : workerList) {
					if (tokens[i].equals(worker.getLogin())) {
						String[] joinCmd = { "join", topicName };
						worker.handleJoin(joinCmd);
					}
				}
			}

//			// send this group chat information to  all related online logins
//			for (ServerWorker worker : workerList) {
//				if (worker.isMemberOfTopic(topicName)) {
//					if (worker.getLogin() != null) {
//						String msg2 = "online " + topicName + "\n";
//						worker.send(msg2);
//					}
//				}
//			}

			String msg = "ok invited\n";
			try {
				outputStream.write(msg.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void handleRegister(String[] tokens) {
		// TODO Auto-generated method stub
		if (tokens.length == 4) {
			String login = tokens[1];
			String password = tokens[2];
			String password2 = tokens[3];

			if (Database.isUserExisted(login)) {
				String msg = "The username is already existed\n";
				System.out.println("register failed because the username is already existed");
				try {
					outputStream.write(msg.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else if (!password.equals(password2)) {
				String msg = "The 2 passwords entered is different\n";
				System.out.println("register failed because the 2 passwords entered is different\n");
				try {
					outputStream.write(msg.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				Database.addUser(login, password);
				String msg = "ok register\n";
				try {
					outputStream.write(msg.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void handleLeave(String[] tokens) {
		if (tokens.length > 1) {
			String topic = tokens[1];
			topicSet.remove(topic);
		}
	}

	public boolean isMemberOfTopic(String topic) {
		return topicSet.contains(topic);
	}

	private void handleJoin(String[] tokens) {

		if (tokens.length > 1 && tokens[1].charAt(0) == '#') {
			String topic = tokens[1];
			topicSet.add(topic);
			String msg2 = "online " + topic + "\n";
			send(msg2);
			System.out.println(login + " join " + topic);
		}
	}

	// format: "msg" "login" body...
	// format: "msg" "#topic" body...
	private void handleMessege(String[] tokens) {
		String sendTo = tokens[1];
		String body = tokens[2];

		boolean isTopic = sendTo.charAt(0) == '#';

		List<ServerWorker> workerList = server.getWorkerList();
		for (ServerWorker worker : workerList) {
			if (isTopic) {
				if (worker.isMemberOfTopic(sendTo) && this.isMemberOfTopic(sendTo)) {
					long msgTimeStamp = System.currentTimeMillis();
					String outMsg = "msg " + sendTo + " " + msgTimeStamp + " " + login + ":" + body + "\n";
					worker.send(outMsg);
				}
			} else if (sendTo.equalsIgnoreCase(worker.getLogin())) {
				long msgTimeStamp = System.currentTimeMillis();
				// Database.insertMessage (login, sendTo,body,msgTimeStamp);
				String outMsg = "msg " + login + " " + msgTimeStamp + " " + body + "\n";
				worker.send(outMsg);
			}
		}
	}

	private void handleLogoff() throws IOException {
		server.removeWorker(this);
		List<ServerWorker> workerList = server.getWorkerList();

		// send other online users current user's status
		String offlineMsg = "offline " + login + "\n";
		for (ServerWorker worker : workerList) {
			if (!login.equals(worker.getLogin())) {
				worker.send(offlineMsg);
			}
		}
		clientSocket.close();

		if (!topicSet.isEmpty()) {
			for (String topic : topicSet) {
				String topicOfflineMsg = "offline " + topic + "\n";
				// simply send topicOfflineMsg to every user, which need some moderation
				for (ServerWorker worker : workerList) {
					worker.send(topicOfflineMsg);
				}
			}
		}

	}

	public String getLogin() {
		return login;
	}

	private void handleLogin(OutputStream outputStream, String[] tokens) {
		if (tokens.length == 3) {
			String login = tokens[1];
			String password = tokens[2];

			if (Database.isValidUser(login, password) || login.equals("guest") && password.equals("guest")
					|| login.equals("DP") && password.equals("1996")) {
				String msg = "ok login\n";
				try {
					outputStream.write(msg.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.login = login;
				System.out.println("User logged in successfully: " + login);

				List<ServerWorker> workerList = server.getWorkerList();

				// send current user all other online logins
				for (ServerWorker worker : workerList) {
					if (!login.equals(worker.getLogin())) {
						if (worker.getLogin() != null) {
							String msg2 = "online " + worker.getLogin() + "\n";
							send(msg2);
						}
					}
				}

				// send other online users current user's status
				String onlineMsg = "online " + login + "\n";
				for (ServerWorker worker : workerList) {
					if (!login.equals(worker.getLogin())) {
						worker.send(onlineMsg);
					}
				}
			} else {
				String msg = "error login\n";
				System.out.println("login failed for " + login);
				try {
					outputStream.write(msg.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// format: "history" <user1> <user2>
	private void handleHistory(String[] tokens) throws IOException {
		String fromUser = tokens[1];
		String toUser = tokens[2];

		ArrayList<Messages> history = new ArrayList<Messages>();
		// Databse.retrieveMessages (fromUser, toUser);

		objectOutputStream.writeObject(history);
	}

	private void handleGetQuestions(String[] tokens) throws IOException {
		String topicName = tokens[1];
		
		examDatabase.makeConnection();
		ArrayList<Question> questions = examDatabase.getQuestions(topicName.toLowerCase());
		objectOutputStream.writeObject(questions);

		System.out.println(topicName + " question sended");
		System.out.println(questions.get(0).getQuestioncontent() + " " + questions.get(0).getCorrectans());

	}

	private void send(String msg) {
		try {
			if (login != null) {
				outputStream.write(msg.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
