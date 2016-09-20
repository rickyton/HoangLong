import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Client extends JFrame implements ActionListener, Runnable{
	
	private static final long serialVersionUID = 980389841528802556L;
	
	// define the user interface components
	JTextField chatInput = new JTextField(50);
	JTextArea chatHistory = new JTextArea(5,50);
	JButton chatMessage = new JButton("Send");
	
	// define the socket and io streams
	Socket client;
	DataInputStream dis;
	DataOutputStream dos;
	
	public Client()
	{
		// create the user interface and setup an action listener linked to the send message button
		
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		//TODO develop prompt to allow a user (client) to enter the nickname / handle for their client suggest using JOptionPane
		
		//TODO add in extra user interface components to allow a user to select the remote client that they want to send a message to suggest using a JList
		
		contentPane.add(chatInput,BorderLayout.CENTER);
		contentPane.add(chatMessage,BorderLayout.EAST);
		contentPane.add(new JScrollPane(chatHistory),BorderLayout.NORTH);
		
		pack();
		setVisible(true);
		
		chatMessage.addActionListener(this);
		
		// attempt to connect to the defined remote host
		try {
			client = new Socket("localhost",10000);
			dis = new DataInputStream(client.getInputStream());
			dos = new DataOutputStream(client.getOutputStream());
			
			// define a thread to take care of messages sent from the server
			Thread clientThread = new Thread(this);
			clientThread.start();
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Method to respond to button press events, and send a chat message to the Server
	 */
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		try {
			dos.writeInt(ServerConstants.CHAT_MESSAGE); // determine the type of message to be sent
			dos.writeUTF(chatInput.getText()); // message payload
			
			dos.flush(); // force the message to be sent (sometimes data can be buffered)
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	// process messages from the server
	@Override
	public void run()
	{
		while(true)
		{
			try {
				int messageType = dis.readInt(); // receive a message from the server, determine message type based on an integer
				
				// decode message and process
				switch(messageType)
				{
					case ServerConstants.CHAT_BROADCAST:
						chatHistory.append(dis.readUTF()+"\n");
						break;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args)
	{
		Client client = new Client();
		client.run();
		client.setVisible(true);
	}
}
