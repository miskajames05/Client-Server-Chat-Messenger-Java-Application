import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.Color;
public class GUI extends JFrame implements ActionListener
{

	private JFrame frame;
	private JTextField textField;
	private JTextArea textArea;
	private JButton send;
	private JButton endChat;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private Scanner scanner = new Scanner(System.in);
	private JScrollPane scrollPane;
	private JScrollPane scroll;
	private JList list;
	private JTextField textField_1;
	private JTextField textField_2;
	private JButton createAccount;
	private JButton login;
	private JButton update;
	private JButton answer;
	private JButton reject;
	private JButton cancelAccount;

	public GUI() 
	{
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 605, 380);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		this.endChat = new JButton("End Chat");
		endChat.setBounds(334, 283, 102, 23);
		frame.getContentPane().add(endChat);
		
		this.textArea = new JTextArea();
		textArea.setBounds(110, 105, 294, 145);
		frame.getContentPane().add(textArea);
		
		scroll = new JScrollPane(textArea);
		scroll.setBounds(22, 103, 294, 145);
		frame.getContentPane().add(scroll);
		//frame.getContentPane().add(scroll);
		
		textField = new JTextField();
		textField.setBounds(22, 283, 294, 48);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		this.send = new JButton("Send");
		send.setBounds(334, 308, 102, 23);
		frame.getContentPane().add(send);
		frame.setVisible(true);
		//sendMessage();
		
		list = new JList();
		list.setBounds(335, 103, 244, 145);
		frame.getContentPane().add(list);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(22, 35, 171, 23);
		frame.getContentPane().add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(22, 69, 171, 23);
		frame.getContentPane().add(textField_2);
		
		login = new JButton("Login");
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		login.setBounds(203, 69, 113, 23);
		frame.getContentPane().add(login);
		
		createAccount = new JButton("Create Account");
		createAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		createAccount.setBounds(203, 46, 113, 23);
		frame.getContentPane().add(createAccount);
		
		update = new JButton("Update");
		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		update.setBounds(366, 69, 102, 23);
		frame.getContentPane().add(update);
		
		JButton logout = new JButton("Logout");
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		logout.setBounds(366, 35, 102, 23);
		frame.getContentPane().add(logout);
		
		JButton call = new JButton("Call");
		call.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		call.setBounds(478, 35, 63, 57);
		frame.getContentPane().add(call);
		
		answer = new JButton("Ans");
		answer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		answer.setBounds(446, 279, 63, 57);
		frame.getContentPane().add(answer);
		
		reject = new JButton("Rej");
		reject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		reject.setBounds(516, 279, 63, 57);
		frame.getContentPane().add(reject);
		
		cancelAccount = new JButton("Cancel Account");
		cancelAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		cancelAccount.setBounds(203, 23, 113, 23);
		frame.getContentPane().add(cancelAccount);
	}
	
	public void setDataOutput(DataOutputStream dos)
	{
	    this.dos = dos;
	}

	public void setDataInput(DataInputStream dis)
	{
	    this.dis = dis;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == send) 
	    {
	        new Thread(new Runnable()
	        {
	            public void run ()
	            {
	                while(true)
	                {
	                    try
	                    {
	                        String msgsend = scanner.nextLine();
	                        textArea.append("To Client :- "+msgsend+"\n");
	                        //dos.writeUTF(msgsend);    
	                    }
	                    catch(Exception e)
	                    {
	                    	e.printStackTrace();
	                    }   

	                }//end while        
	            }
	        }).start();
	    }
	}
	
	public void chatMode() throws IOException
	{
		String message = "Connection HAHA!";
		sendMessage(message);
		ableToType(true);
		
		do
		{
			message = (String) dis.readUTF();
			showMessage("\n" + message);
		}
		while(!message.equalsIgnoreCase("Client"));
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
	
	public void sendMessage(String message)
	{
		try
		{
			dos.writeUTF("Server: " + message);
			showMessage(message);
			dos.flush();
			
		}
		catch(IOException ioException)
		{
			textArea.append("ERROR: Unable to send message");
		}
	}
	
	//Change/Update chatWindow
	public void showMessage(final String text)
	{
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run()
                    {
                        textArea.setEditable(false); //disallows text editing in chatWindow
                        textArea.append(text); //appends text, which was passed in from above
                    }
                }
                );
	}
	
	public void getMsg(){

	    new Thread(new Runnable()
	    {
	        public void run ()
	        {
	            while(true)
	            {
	                try 
	                {
	                    String msg = dis.readUTF();
	                    textArea.append("From Client :- "+msg+"\n");
	                } catch (IOException e) 
	                {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                finally{}
//	                catch(Exception e)
//	                {
//	                	e.printStackTrace();
//	                }       
	            }//end while
	        }
	    }).start();

	}//end getmsg
}

/*
 *public boolean deleteRecordDB(String nameVal, String passVal) throws IOException
	{
		boolean deletedRecord = false;
		File file = new File(FILE);
		File tempFile = new File("tempFile.txt");
		FileWriter fw = new FileWriter(tempFile, true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		Scanner x = new Scanner(new File(FILE));
		x.useDelimiter("[,\n]");
		while(x.hasNext())
		{
			String usr = x.next().trim();
			String pass = x.next().trim();
			if(!usr.equals(nameVal) && !pass.equals(passVal))
			{
				pw.println(usr + "," + pass + '\n');
			}
			else
			{
				deletedRecord = true;
			}
		}
		x.close();
		pw.flush();
		pw.close();
		file.delete();
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
					deleteRecordDB(nameVal, passVal);
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
*/