package DirectorTest;

import java.io.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

public class MyThread extends Thread {
	
	private SSLSocket socket = null;
	private Director server = null;
	private int ID = -1;
	private BufferedReader r = null;
	private BufferedWriter w = null;
	private boolean running = true;
	
	public MyThread(Director _server, SSLSocket _socket)
	{
		server = _server; socket = _socket; ID = socket.getPort();
	}
	public void send(String msg)
	{
		try
		{
			w.write(msg,0,msg.length());
			w.newLine();
			w.flush();
		}
		catch (IOException ioe)
		{
			System.out.println(ID + " ERROR sending: " + ioe.getMessage());
			server.handle(ID, ".");
			interrupt();
		}
	}
	public int getID()
	{
		return ID;
	}
	
	public void run()
	{
		System.out.println("Server Thread " + ID + " running.");
		try
		{
			String m = "Connected!";
			server.handle(ID, m);
			while ((m=r.readLine())!= null) {
				server.handle(ID, m);
			}
		} catch (Exception e) {
			if (e.getMessage().equals("Connection reset"))
			{
				System.out.println("Connection problem: " + e.getMessage());
				server.remove(ID);
			}
		}
	}
	public void open() throws IOException
	{
		w = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));
		r = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));

	}
	public void close() throws IOException
	{
		if(socket != null) socket.close();
		if(r != null) r.close();
		if(w != null) w.close();
	}
	
}
