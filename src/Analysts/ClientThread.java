package Analysts;

import java.io.*;
import javax.net.ssl.*;

public class ClientThread extends Thread
{
	private Client client = null;
	private BufferedReader in = null;
	private SSLSocket socket = null;

	public ClientThread(Client _client, SSLSocket _socket)
	{
		client = _client;
		socket = _socket;
		open();
		start();
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
	@Override
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
					client.handle(msg);
					break;
				}
				client.handle(msg);
			}
			catch (Exception e)
			{
				System.out.println("Connection Error. Press <ENTER> to reconnect.");
				client.stop();
				break;
			}

		}
	}
}
