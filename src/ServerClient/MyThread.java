package ServerClient;

import java.io.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

public class MyThread extends Thread {
	
	private SSLSocket socket = null;
	private SSLServerSocket server = null;
	private int ID = -1;
	private BufferedReader r = null;
	private BufferedWriter w = null;
	
	public MyThread(SSLServerSocket _server, SSLSocket _socket)
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
				String m = "Connect to thread #" + ID;
				w.write(m,0,m.length());
				w.newLine();
				w.flush();
				while ((m=r.readLine())!= null) {
					if (m.equals(".")) break;
					w.write(m,0,m.length());
					w.newLine();
					w.flush();
				}
				close();
				
			} catch (Exception e) {
				System.err.println(e.toString());
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
