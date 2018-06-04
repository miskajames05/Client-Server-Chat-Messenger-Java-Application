import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

public class Client extends JFrame 
{
	private static final char CLEAR_ARRAYLIST = '0';
	private static final char UPDATE_ONLINE_USRS = '1';
	private static final char REQUEST_TO_CHAT = '2';
	private static final char OTHER_CLIENT_ACCEPTED_CALL = '3';
	private static final char OTHER_CLIENT_REJECTED_CALL = '4';
	private static final char OTHER_CLIENT_LOGOUT = '5';
	private static final char OTHER_CLIENT_ENDING_CALL = '6';
	private static final String FILE = "login.txt";
	private Socket socket = null;
	private JFrame frame;
	private ArrayList<String> onlineUsers = new ArrayList<String>();
	private DefaultListModel<String> listModel;
	private JList<String> list;
	private JScrollPane scroll;
	private JTextField textField;
	private JTextField usernameField;
	private JTextField passwordField;
	private JTextArea textArea;
	private JButton logout;
	private JButton send;
	private JButton endChat;
	private JButton createAccount;
	private JButton cancelAccount;
	private JButton update;
	private JButton login;
	private JButton call;
	private JButton answer;
	private String otherName = "";
	private JButton reject;
	private static BufferedReader br = null;
	private static PrintWriter pw = null;
	private String username;
	private boolean loggedIn = false;

	public static void main(String[] args) throws Exception 
	{
		Socket socket = null;
		Client cli = new Client();
		cli.frame.setVisible(true);

		try
		{
			socket = new Socket("localhost", 5000);
			cli.setSocket(socket);
			pw = new PrintWriter(socket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			cli.chatMode(); 	    
		}
		finally
		{
			socket.close();
			//cli.textArea.append("Server has shut down");
		}
	}

	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}

	public Socket getSocket()
	{
		return socket;
	}

	public boolean insertRecordDB(String nameVal, String passVal) throws FileNotFoundException
	{
		boolean success = false;
		BufferedWriter bw = null;
		String line = nameVal + "," + passVal;
		try
		{
			bw = new BufferedWriter(new FileWriter(FILE, true));
			bw.newLine();
			bw.write(line);
			bw.flush();
			success = true;
			if(success == true)
			{
				pw.println(nameVal + " has created an account\n");
				pw.flush();
			}
			else
			{
				pw.println("Attempt to create account Failed\n");
				pw.flush();
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{
			if(bw != null)
			{
				try
				{
					bw.close();
				}
				catch(IOException ioe2)
				{

				}
			}
		}
		return success;
	}

	//Returns false if inputted username is new 
	public boolean checkInfoRegister(String file, String usrname)
	{
		boolean newUsername = false;
		String tempName = "";

		try
		{
			Scanner scan = new Scanner(new File(file));
			scan.useDelimiter("[,\n]");

			while(scan.hasNext() && !newUsername)
			{
				tempName = scan.next();
				scan.next();

				if(tempName.trim().equals(usrname.trim()))
				{
					//username is taken
					scan.close();
					newUsername = true;
					return newUsername;
				}
			}
			scan.close();
		}
		catch(Exception e)
		{
			System.out.println("Error");
			e.getStackTrace();
		}
		return newUsername;
	}

	//Returns true if both username and password matches DB records
	public boolean checkForCredentials(String file, String usrname, String pass)
	{
		boolean credentialsFound = false;
		String tempName = "";
		String tempPass = "";
		try
		{
			Scanner scan = new Scanner(new File(file));
			scan.useDelimiter("[,\n]");

			while(scan.hasNext() && !credentialsFound)
			{
				tempName = scan.next();
				tempPass = scan.next();

				if(tempName.trim().equals(usrname.trim()) && tempPass.trim().equals(pass.trim()))
				{
					//username and password match
					scan.close();
					credentialsFound = true;
					return credentialsFound;
				}
				else if(tempName.trim().equals(usrname.trim()) && !tempPass.trim().equals(pass.trim()))
				{
					//username matches but password does not
					String line = "Incorrect Password\n";
					textArea.append(line);
				}
			}
			scan.close();
			return false;
		}
		catch(Exception e)
		{
			System.out.println("Error");
			e.printStackTrace();
		}
		return credentialsFound;
	}

	public Client() 
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 605, 356);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		this.endChat = new JButton("End Chat");
		endChat.setBounds(334, 259, 102, 23);
		frame.getContentPane().add(endChat);
		endChat();

		this.textArea = new JTextArea();
		textArea.setBounds(22, 103, 294, 145);
		frame.getContentPane().add(textArea);
		textArea.setEditable(false);

		scroll = new JScrollPane(textArea);
		scroll.setBounds(22, 103, 294, 145);
		frame.add(scroll);

		textField = new JTextField();
		textField.setBounds(22, 259, 294, 48);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		this.send = new JButton("Send");
		send.setBounds(334, 284, 102, 23);
		frame.getContentPane().add(send);
		sendMessage();

		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		list.setBounds(335, 103, 206, 145);
		frame.getContentPane().add(list);

		usernameField = new JTextField();
		usernameField.setColumns(10);
		usernameField.setBounds(22, 35, 171, 23);
		frame.getContentPane().add(usernameField);

		passwordField = new JPasswordField();
		passwordField.setBounds(22, 69, 171, 23);
		frame.getContentPane().add(passwordField);

		login = new JButton("Login");
		login.setBounds(203, 69, 113, 23);
		frame.getContentPane().add(login);	
		login();

		createAccount = new JButton("Create Account");
		createAccount.setBounds(203, 46, 113, 23);
		frame.getContentPane().add(createAccount);
		createUser();

		update = new JButton("Update");
		update.setBounds(366, 69, 102, 23);
		frame.getContentPane().add(update);
		getUsers();

		logout = new JButton("Logout");
		logout.setBounds(366, 35, 102, 23);
		frame.getContentPane().add(logout);
		logout();

		call = new JButton("Call");
		call.setBounds(478, 35, 63, 57);
		frame.getContentPane().add(call);
		selectOnlineUser();

		answer = new JButton("Ans");
		answer.setBounds(446, 259, 63, 57);
		frame.getContentPane().add(answer);
		answer.setEnabled(false);
		acceptCall();

		reject = new JButton("Rej");
		reject.setBounds(519, 259, 63, 57);
		frame.getContentPane().add(reject);
		reject.setEnabled(false);
		rejectCall();

		cancelAccount = new JButton("Cancel Account");
		cancelAccount.setBounds(203, 23, 113, 23);
		frame.getContentPane().add(cancelAccount);
		cancelUser();

		enableDisableButtons(false, false, false, false, false, false, false);
	}

	private void selectOnlineUser()
	{
		call.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!list.isSelectionEmpty())
				{
					String selectedVal = list.getSelectedValue().trim();
					if(!selectedVal.equals(""))
					{
						pw.println("3a" + selectedVal);
					}
				}
			}
		});
	}

	public boolean deleteRecordDB(String nameVal, String passVal) throws IOException
	{
		boolean deletedRecord = false;
		File file = new File(FILE);
		File tempFile = new File("tempFile.txt");
		BufferedWriter bwTemp = new BufferedWriter(new FileWriter(tempFile, true));
		Scanner x = new Scanner(new File(FILE));
		x.useDelimiter("[,\n]");
		int i = 0;
		while(x.hasNext())
		{
			String usr = x.next().trim();
			String pass = x.next().trim();
			if(!usr.equals(nameVal) || !pass.equals(passVal))
			{
				if(i > 0)
				{
					bwTemp.newLine();
				}
				bwTemp.write(usr.trim() + "," + pass.trim());
				i++;
			}
			else
			{
				deletedRecord = true;
			}
		}
		x.close();
		file.delete();
		bwTemp.close();
		File dump = new File(FILE);
		tempFile.renameTo(dump);
		return deletedRecord;
	}

	private void cancelUser()
	{
		cancelAccount.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				String nameVal = usernameField.getText().toString();
				String passVal = passwordField.getText().toString();
				if(!(nameVal.equals("")) && !(passVal.equals("")))
				{
					try 
					{
						deleteRecordDB(nameVal, passVal);
					} 
					catch (IOException e1) 
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
				{
					textArea.append("Fill in all fields to delete account");
					pw.println("Attempt to delete account Failed");
					pw.flush();
				}
			}
		});
	}

	private void createUser()
	{
		createAccount.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String nameVal = usernameField.getText().toString();
				String passVal = passwordField.getText().toString();
				if(!(nameVal.equals("")) && !(passVal.equals("")))
				{
					if(!checkInfoRegister(FILE, nameVal))
					{
						try 
						{
							if(insertRecordDB(nameVal, passVal))
							{
								textArea.append("Account successfully created\n");
								usernameField.setText("");
								passwordField.setText("");
							}
							else
							{
								System.out.println("Error inserting checkInfoRegister");
							}
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					else
					{
						textArea.append("Username is already taken\n");
						pw.println("Attempt to create account Failed");
						pw.flush();
					}
				}
			}
		});
	}

	private void logout()
	{
		logout.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String line = username;
				pw.println("7a" + line);
				pw.flush();
				loggedInStatus(false);
				textArea.setText("");
				listModel.clear();
				enableDisableButtons(false, false, false, false, false, false, false);
			}

		});
	}

	private void login()
	{
		login.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String nameVal = usernameField.getText().toString();
				String passVal = passwordField.getText().toString();
				if(!(nameVal.equals("")) && !(passVal.equals("")))
				{
					if(checkForCredentials(FILE, nameVal, passVal)) //Returns true if credentials match db
					{
						username = nameVal;
						String clientName = "1 " + nameVal;
						String line = "" + nameVal + " is now connected";
						loggedInStatus(true);
						textArea.append(line + "\n");
						pw.println(clientName);
						pw.println(line);
						pw.flush();
					}
				}
				usernameField.setText("");
				passwordField.setText("");
			}
		});
	}

	private void getUsers()
	{
		update.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				String line;
				if(loggedIn == true)
				{
					line = "2"; //2 means update to the server
					pw.println(line);
					pw.flush();
				}
			}
		});
	}

	private void sendMessage()
	{
		send.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String line;
				if(!textField.getText().equals(""))
				{
					line = username + ": " + textField.getText();
					textArea.append(line + "\n");
					textField.setText("");
					pw.println(line);
					pw.flush();
				}
			}
		});
	}

	private void endChat()
	{
		endChat.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String line;
				line = username + " has ended the chat";
				textArea.append(line + "\n");
				textField.setText("");
				pw.println("6a" + line);
				pw.flush();
				otherName = "";
				enableDisableButtons(true, false, false, true, true, false, false);
			}
		});
	}

	private void chatMode() throws IOException
	{
		String message;
		onlineUsers = new ArrayList<String>();
		ableToType(true);
		try
		{
			while((message = br.readLine() + "\n") != null)
			{
				char header = message.charAt(0);
				if(header == CLEAR_ARRAYLIST) 	//'0'
				{
					onlineUsers = new ArrayList<String>();
				}
				else if(header == UPDATE_ONLINE_USRS) //'1'
				{
					message = message.substring(2,message.length()).trim();
					updateOnlineUsrs(message);
				}
				else if(header == REQUEST_TO_CHAT) //'2'
				{
					message = message.substring(2,message.length()).trim();
					otherName = message;
					textArea.append(message + " would like to chat with you\n");
					enableDisableButtons(false, false, false, false, false, true, true);
				}
				else if(header == OTHER_CLIENT_ACCEPTED_CALL) //'3' Client A side: Client B accepted
				{
					otherName = message.substring(2,message.length()).trim();
					message = otherName + " has accepted your call";
					showMessage(message);
					pw.println("4 ");
					send.setEnabled(true);
					endChat.setEnabled(true);
				}
				else if(header == OTHER_CLIENT_REJECTED_CALL) //'4' declining chat
				{
					message = message.substring(2,message.length()).trim();
					showMessage(message);
					pw.println("5 ");
				}
				else if(header == OTHER_CLIENT_LOGOUT)//'5' other client logged out
				{
					message = message.substring(2,message.length()).trim();
					showMessage(message);
					pw.println("7b");
					otherName = "";
					enableDisableButtons(true, false, false, true, true, false, false);
				}
				else if(header == OTHER_CLIENT_ENDING_CALL)//'6' other client ending chat
				{
					message = message.substring(2,message.length()).trim();
					showMessage(message);
					otherName = "";
					pw.println("6b");
					enableDisableButtons(true, false, false, true, true, false, false);
				}
				else if(header == '7')
				{
					enableDisableButtons(false, false, false, false, false, false, false);
					login.setEnabled(false);
					createAccount.setEnabled(false);
					textArea.append("Server has shutdown");
					getSocket().close();
				}
				else
				{
					if(!message.trim().isEmpty())
					{
						showMessage(message.trim());
					}
				}
			}
		}
		catch(SocketException e)
		{
			System.out.println("Socket closed on client side");
			//e.printStackTrace();
		}
		finally{}
	}

	private void acceptCall()
	{
		answer.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				pw.println("3b" + otherName);
				enableDisableButtons(true, true, true, true, true, false, false);
			}
		});
	}

	private void rejectCall()
	{
		reject.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				pw.println("3c" + otherName); //Change to 3c
				enableDisableButtons(true, false, false, true, true, false, false);
			}
		});
	}

	private void updateOnlineUsrs(String message)
	{
		onlineUsers.add(message);
		listModel.clear();
		int i = 0;
		while(i < onlineUsers.size())
		{
			if(!username.equals(onlineUsers.get(i).trim()))
			{
				listModel.addElement(onlineUsers.get(i));
			}
			i++;
		}
	}

	private void enableDisableButtons(boolean logout, boolean send, boolean endChat, 
			boolean update, boolean call, boolean answer, boolean reject)
	{
		this.logout.setEnabled(logout);
		this.send.setEnabled(send);
		this.endChat.setEnabled(endChat);
		this.update.setEnabled(update);
		this.call.setEnabled(call);
		this.answer.setEnabled(answer);
		this.reject.setEnabled(reject);
	}

	private void loggedInStatus(boolean status)
	{
		loggedIn = status;
		if(loggedIn == true)
		{
			login.setEnabled(false);
			createAccount.setEnabled(false);
			cancelAccount.setEnabled(false);
			logout.setEnabled(true);
			update.setEnabled(true);
			call.setEnabled(true);
		}
		else
		{
			login.setEnabled(true);
			createAccount.setEnabled(true);
			cancelAccount.setEnabled(true);
			logout.setEnabled(false);
			update.setEnabled(false);
			call.setEnabled(false);
		}
	}

	public void ableToType(final boolean tof)
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						textField.setEditable(tof);
					}
				});
	}

	//Change/Update chatWindow
	public void showMessage(final String text)
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						textArea.setEditable(true); //disallows text editing in chatWindow
						textArea.append(text + "\n"); //appends text, which was passed in from above
						textArea.setEditable(false);
					}
				}
				);
	}
}

