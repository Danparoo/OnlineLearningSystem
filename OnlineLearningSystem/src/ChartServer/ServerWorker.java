package ChartServer;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread {

	private final Socket clientSocket;
	private final Server server;
	private String login = null;
	private OutputStream outputStream;
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

				} else if ("msg".equalsIgnoreCase(cmd)) {
					String[] tokensMsg = StringUtils.split(line, null, 3);
					handleMessege(tokensMsg);

				} else if ("join".equalsIgnoreCase(cmd)) {
					handleJoin(tokens);

				} else if ("leave".equalsIgnoreCase(cmd)) {
					handleLeave(tokens);

				} else {
					String msg = "unknow " + cmd + "\n";
					outputStream.write(msg.getBytes());
				}
			}
		}

		clientSocket.close();
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
		if (tokens.length > 1) {
			String topic = tokens[1];
			topicSet.add(topic);
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
				if (worker.isMemberOfTopic(sendTo)) {
					String outMsg = "msg " + sendTo + ":" + login + " " + body + "\n";
					worker.send(outMsg);
				}
			} else if (sendTo.equalsIgnoreCase(worker.getLogin())) {
				String outMsg = "msg " + login + " " + body + "\n";
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
	}

	public String getLogin() {
		return login;
	}

	private void handleLogin(OutputStream outputStream, String[] tokens) {
		if (tokens.length == 3) {
			String login = tokens[1];
			String password = tokens[2];

			if (login.equals("guest") && password.equals("guest") || login.equals("DP") && password.equals("1996")) {
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