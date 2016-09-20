import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


public class ServerThread extends Thread {

	DataInputStream dis;
	DataOutputStream dos;
	
	Socket remoteClient;
	Server server;
	
	ArrayList<ServerThread> connectedClients; // keep track of all the other clients connected to the Server
	
	public ServerThread(Socket remoteClient, Server server, ArrayList<ServerThread> connectedClients)
	{
		this.remoteClient = remoteClient;
		this.connectedClients = connectedClients;
		try {
			this.dis = new DataInputStream(remoteClient.getInputStream());
			this.dos = new DataOutputStream(remoteClient.getOutputStream());
			this.server = server;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void run()
	{
		while(true) // main protocol decode loop
		{
			try {
				int mesgType = dis.readInt(); // read the type of message from the client (must be an integer)
				System.err.println(mesgType);
				
				// decode the message type based on the integer sent from the client
				switch(mesgType)
				{
					case ServerConstants.CHAT_MESSAGE:
						String data = dis.readUTF();
						System.err.println(data);
						server.getSystemLog().append(remoteClient.getInetAddress()+":"+remoteClient.getPort()+">"+data+"\n");
						
						for(ServerThread otherClient: connectedClients)
						{
							if(!otherClient.equals(this)) // don't send the message to the client that sent the message in the first place
							{
								otherClient.getDos().writeInt(ServerConstants.CHAT_BROADCAST);
								otherClient.getDos().writeUTF(data);
							}
						}
						
						break;
					case ServerConstants.REGISTER_CLIENT:
						// TODO develop code to handle new client registrations

						// TODO broadcast this registration to all other clients connected to the server (similar to the CHAT_BROADCAST message sent to each client above)
						
						break;	
					case ServerConstants.PRIVATE_MESSAGE:
						// TODO develop code to handle private messages sent by the client
						break;
				}				
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
	}

	public DataOutputStream getDos() {
		return dos;
	}
}
