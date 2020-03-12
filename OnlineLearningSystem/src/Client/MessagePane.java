package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessagePane extends JPanel implements MessageListener {

	private final Client client;
	private final String login;

	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private JList<String> messageList = new JList<>(listModel);
	private JTextField inputField = new JTextField();

	public MessagePane(Client client, String login) {
		this.client = client;
		this.login = login;

		client.addMessageListener(this);

		setLayout(new BorderLayout());
		add(new JScrollPane(messageList), BorderLayout.CENTER);
		add(inputField, BorderLayout.SOUTH);

		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = inputField.getText();
					client.msg(login, text);
					String nowtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
					listModel.addElement("You: " + text);
					listModel.addElement(nowtime);
					inputField.setText("");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onMessage(String fromLogin, String msgBody,String msgTimeStamp) {
		if (login.equalsIgnoreCase(fromLogin)) {
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(msgTimeStamp)));
			String line = fromLogin + ": " + msgBody;
			listModel.addElement(line);
			listModel.addElement(time);
			
		}
	}
}
