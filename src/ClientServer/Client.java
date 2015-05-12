import java.io.*;
import java.net.*;


public class Client {
	
	private Socket socket = null;
	private PrintWriter streamOut = null;
	private BufferedReader console = null;
	
	public Client(String serverName, int serverPort)
	{
		System.out.println("Establishing connection. Please wait ...");
		try
		{
			socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + socket);
			start();
		}
		catch (UnknownHostException uhe)
		{
			System.out.println("Host unknown: " + uhe.getMessage());
		}
		catch (IOException ioe)
		{
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
		
		String line = "";
		while (!line.equals("bye"))
		{
			try
			{
				line = console.readLine();
				streamOut.println(line);
				streamOut.flush();
			}
			catch (IOException ioe)
			{
				System.out.println("Sending error: " + ioe.getMessage());
			}
		}
	}
	
	public void start() throws IOException
	{
		console = new BufferedReader(new InputStreamReader(System.in));
		streamOut = new PrintWriter(socket.getOutputStream());
	}
	
	public void stop()
	{
		try 
		{
			if(console != null) console.close();
			if(streamOut != null) streamOut.close();
			if(socket != null) socket.close();
		}
		catch (IOException ioe)
		{
			System.out.println("Error closing ...");
		}
	}
	
	public static void main(String args[])
	{
		Client client = new Client("127.0.0.1", 1244);
	}

}
