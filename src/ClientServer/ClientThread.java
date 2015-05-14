package ClientServer;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class ClientThread extends Thread
{
	private SSLSocket socket = null;
	private Client client = null;
	private BufferedReader in = null;
	
	public ClientThread(Client _client, SSLSocket _socket)
	{
		client = _client;
		socket = _socket;
		open();
		start();
	}
	public void open()
	{
		try
		{
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException ioe)
		{
			System.out.println("Error getting input stream: " + ioe);
			client.stop();
		}
	}
	public void close()
	{
		try
		{
			if(in != null) in.close();
		}
		catch (IOException ioe)
		{
			System.out.println("Error closing input stream: " + ioe);
		}
	}
	public void run()
	{
		String msg = "";
		while(true)
		{
			try
			{
				msg = in.readLine();
				if(msg.equals("."))
				{
					break;
				}
				client.handle(msg);
			}
			catch (IOException ioe)
			{
				System.out.println("Listening error: " + ioe.getMessage());
				client.stop();
				break;
			}
		}
	}
}
