package ServerClient;

import java.io.*;
import java.net.*;



public class MyThread extends Thread {
	
	private Socket socket = null;
	private Server server = null;
	private int ID = -1;
	private BufferedReader streamIn = null;
	
	public MyThread(Server _server, Socket _socket)
	{
		server = _server; socket = _socket; ID = socket.getPort();
	}
	public void run()
	{
		System.out.println("Server Thread " + ID + " running.");
		while(true)
		{
			try
			{
				String str = streamIn.readLine();
				System.out.println(str);
			}
			catch (IOException ioe) {}
		}
	}
	public void open() throws IOException
	{
		streamIn = new BufferedReader(
		        new InputStreamReader(socket.getInputStream()));
	}
	public void close() throws IOException
	{
		if(socket != null) socket.close();
		if(streamIn != null) streamIn.close();
	}
	
}
