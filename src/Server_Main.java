import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server_Main 
{
	private ServerSocket serverSocket = null;
	private Server_GUI gui;
	private Socket clientSocket;
	private Server server;
	
	public static void main(String[] args)
	{
		int portNumber = 5000;
		Server_Main ss = new Server_Main(portNumber);
	}
	
	public Server_Main(int portNumber)
	{
		try
		{
			serverSocket = new ServerSocket(portNumber);
			gui = new Server_GUI(serverSocket);
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		
		boolean isServerOn = true;
		while(isServerOn)
		{
			try
			{
				clientSocket = serverSocket.accept();
				this.server = new Server(clientSocket, gui);	//Simply creates GUI
				Thread thread = new Thread(server); //Create a new thread to process the request.
				thread.start();
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
				isServerOn = false;
			}
		}
	}
}
