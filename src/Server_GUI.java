import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server_GUI 
{
	JFrame frame;
	private JScrollPane scroll;
	private JTextArea textArea;
	private JButton stop;
	private ArrayList<Thread> threadList;
	private ArrayList<String> nameList;
	private ArrayList<PrintWriter> pwList;
	private ArrayList<ConnectionHandler> connHandleList;
	private ServerSocket servSock = null;
	private boolean serverOn = true;

	public Server_GUI(ServerSocket servSock)
	{
		this.servSock = servSock;

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		textArea = new JTextArea();
		textArea.setBounds(10, 11, 315, 240);
		frame.getContentPane().add(textArea);
		textArea.setEditable(false);

		scroll = new JScrollPane(textArea);
		scroll.setBounds(10, 11, 315, 240);
		frame.add(scroll);

		stop = new JButton("Stop");
		stop.setBounds(335, 77, 89, 55);
		frame.getContentPane().add(stop);
		stopServer();

		frame.setVisible(true);
		threadList = new ArrayList<Thread>();
		nameList = new ArrayList<String>();
		pwList = new ArrayList<PrintWriter>();
		connHandleList = new ArrayList<ConnectionHandler>();
	}

	private void stopServer()
	{
		stop.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				serverOn = false;
				try 
				{
					if(!pwList.isEmpty())
					{
						int i = 0;
						while(i < pwList.size())
						{
							pwList.get(i).println("7");
							i++;
						}
						servSock.close();
					}
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void addUser(String username, Thread thread, PrintWriter pw)
	{
		nameList.add(username.trim());
		threadList.add(thread);
		pwList.add(pw);
	}

	public void removeUser(String username)
	{
		int i = 0;
		while(i < nameList.size())
		{
			if(username.equals(nameList.get(i)))
			{
				nameList.remove(i);
				threadList.remove(i);
				pwList.remove(i);
			}
			i++;
		}
	}

	public ArrayList<String> getNames()
	{
		return nameList;

	}

	public ArrayList<Thread> getThreads()
	{
		return threadList;
	}

	public ArrayList<PrintWriter> getWriters()
	{
		return pwList;
	}

	//Sets ConnHandle Object in ArrayList and returns index
	public int setConnHandle(ConnectionHandler connHandle)
	{
		connHandleList.add(connHandle);
		int index = connHandleList.indexOf(connHandle);
		return index;
	}

	public void removeConnHandle(int index)
	{
		connHandleList.remove(index);
	}

	public ArrayList<ConnectionHandler> getConnHandle()
	{
		return connHandleList;
	}

	//Gets index of connection (between two users) instance of list
	public int getIndex(String usr)
	{
		int index;
		int i = 0;
		while(i < connHandleList.size())
		{
			if(usr.trim().equals(connHandleList.get(i).getUsr1().trim()) || usr.trim().equals(connHandleList.get(i).getUsr2().trim()))
			{
				index = i;
				return index;
			}
			i++;
		}
		return -1; //Not in list
	}

	public boolean isServerOn()
	{
		return serverOn;
	}

	public JTextArea getTextArea()
	{
		return textArea;
	}
}
