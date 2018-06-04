import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler 
{
	private String usr1 = "";
	private String usr2 = "";
	private PrintWriter pw1 = null;
	private PrintWriter pw2 = null;
	
	public ConnectionHandler(String usr1, String usr2, PrintWriter pw1, PrintWriter pw2)
	{
		this.usr1 = usr1;
		this.usr2 = usr2;
		this.pw1 = pw1;
		this.pw2 = pw2;
	}
	
	public String getUsr1()
	{
		return usr1;
	}
	
	public String getUsr2()
	{
		return usr2;
	}
	
	public PrintWriter getPw1()
	{
		return pw1;
	}
	
	public PrintWriter getPw2()
	{
		return pw2;
	}
}
