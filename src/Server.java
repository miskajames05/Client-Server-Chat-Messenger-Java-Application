import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server implements Runnable
{
	private static final char USR_LOGIN = '1';
	private static final char SHOW_USRS = '2';
	private static final char CLIENT_CONN = '3';
	private static final char ACCEPT_CALL = '4';
	private static final char REJECT_CALL = '5';
	private static final char END_CALL = '6';
	private static final char USR_LOGOUT = '7';
	private Socket socket = null;
	private Server_GUI gui;
	private BufferedReader br = null;
	private PrintWriter pw = null;
	private PrintWriter pw2 = null;
	private String username;
	private String otherName;
	private Thread thread;
	private boolean inChat = false;

	//Server() is just GUI stuff like setting up the JFrame panel
	public Server(Socket socket, Server_GUI gui) throws IOException 
	{   
		this.socket = socket;
		this.gui = gui;
		this.pw = new PrintWriter(this.socket.getOutputStream(), true);
		this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		username = "";
		otherName = "";
	}

	public void run() 
	{
		String message;
		try 
		{
			while((message = br.readLine() + "\n") != null && gui.isServerOn() == true) //Constantly waiting for messages
			{
				char header = message.charAt(0);
				if(header == USR_LOGIN) 			//'1' A new user is now online
				{
					setUsername(message.substring(2,message.length()).trim()); 
					gui.addUser(getUsername(), getThread(),  pw);	//save username, thread, pw, and br to gui
					showUsersOnline();
				}
				else if(header == SHOW_USRS) 		//'2' client update button was pressed
				{
					showUsersOnline();
				}
				else if(header == CLIENT_CONN) 		//'3' Client sends invite to chat to their server
				{
					connectTwoClients(message);
				}
				else if(header == ACCEPT_CALL) 		//'4' Client A gets ACK Client B Accepts call
				{
					int index = gui.getIndex(username);
					pw2 = gui.getConnHandle().get(index).getPw2();
					inChat = true;
				}
				else if(header == REJECT_CALL) 		//'5' Client B Rejects call
				{
					pw2 = null;
				}
				else if(header == END_CALL) 		//'6' Client ends call
				{
					if(message.charAt(1) == 'a')
					{
						int index = gui.getIndex(username);
						gui.removeConnHandle(index);
						inChat = false;
						pw2.println("6 " + username + " ended chat");
						showMessage(username + " ended chat");
					}
					else //message.charAt(1) == 'a'
					{
						otherName = "";
						inChat = false;
						showUsersOnline();
					}
				}
				else if(header == USR_LOGOUT) //'7'
				{
					if(message.charAt(1) == 'a') //Client ending chat tells its server. Server tells other client
					{
						gui.removeUser(message.substring(2,message.length()).trim());
						if(pw2 != null)
						{
							pw2.println("5 " + username + " logged out");
						}
						showMessage(username + " logged out\n"); 
						username = "";
						pw2 = null;
						inChat = false;
					}
					else	//other client tells its server to close out connection if present
					{
						int index = gui.getIndex(username);
						if(index != -1)
						{
							gui.removeConnHandle(index);
						}
						otherName = "";
						pw2 = null;
						inChat = false;
					}
					
				}
				else	//actual message from user
				{
					if(inChat)
					{
						pw2.println(message);
						showMessage(message);
					}
				}
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			try 
			{
				getSocket().close();
			} 
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
		}
	}
	
	public void connectTwoClients(String message)
	{
		otherName = message.substring(2,message.length()).trim(); 
		ArrayList<String> namelist = gui.getNames();
		ArrayList<PrintWriter> pwlist = gui.getWriters();
		int counter = 0;
		if(gui.getIndex(otherName) == -1)	//Client B is not in chat with someone
		{
			while(counter < namelist.size())	//loop through list of online users for Client B
			{
				if(namelist.get(counter).trim().equals(otherName))
				{
					PrintWriter tempPw = pwlist.get(counter);
					if(message.charAt(1) == 'a')
					{
						tempPw.println("2 " + username); //Server A sends request to Client B
					}
					else if(message.charAt(1) == 'b')//'b' Client B accepts the call. Server B sends that to Client A
					{
						ConnectionHandler connHandler = new ConnectionHandler(otherName, username, tempPw, pw);
						gui.setConnHandle(connHandler);
						pw2 = tempPw;
						inChat = true;
						tempPw.println("3 " + username.trim()); //Server B tells Client A call is accepted
					}
					else	//Client B rejects the call. Server B sends that to Client A
					{
						pw2 = tempPw;
						tempPw.println("4 " + username.trim() + " has rejected your call\n"); //Server B tells Client A call is rejected
					}
					
				}
				counter++;
			}
		}
		else	//Client B is in chat with someone
		{
			pw.println(otherName + " is already in a chat session");
		}
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	public void showUsersOnline()
	{
		ArrayList<String> nameList = gui.getNames();
		int counter = 0;
		while(counter < nameList.size())
		{
			pw.println("1 " + nameList.get(counter));
			counter++;
		}
		pw.println("0");
		
	}
	
	public void showMessage(final String text)
	{
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run()
                    {
                    	if(!text.equals(null))
                    	{
                    		gui.getTextArea().setEditable(true); //disallows text editing in chatWindow
                            gui.getTextArea().append(text); //appends text, which was passed in from above
                            gui.getTextArea().setEditable(false);
                    	}
                    }
                }
                );
	}
	
	public void setThread(Thread thread)
	{
		this.thread = thread;
	}
	
	public Thread getThread()
	{
		return thread;
	}
	
	public void setUsername(String username)
	{
		this.username = username.trim();
	}
	
	public String getUsername()
	{
		return username;
	}
}
