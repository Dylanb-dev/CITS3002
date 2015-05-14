package ClientServer;

import java.io.*;
import java.net.*;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class Client implements Runnable {

	private SSLSocket socket = null;
	private PrintWriter w = null;
	private BufferedReader r = null;
	private BufferedReader in = null;
	private ClientThread client = null;
	private Thread thread = null;

	public Client(String serverName, int serverPort)
	{

		SSLSocketFactory f = 
				(SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			socket = (SSLSocket) f.createSocket(serverName, serverPort);
			printSocketInfo(socket);
			socket.startHandshake();	
			start();
		} catch (IOException e) {
			System.err.println(e.toString());
		}
	     
	}
	public void run()
	{
		
		while (thread != null)
		{
			try
			{
				w.println(in.readLine());
				w.flush();
			}
			catch (IOException ioe)
			{
				System.out.println("Good bye. Press RETURN to exit ...");
				stop();
			}
		}
	}
	
	protected void finalize( ) throws Throwable
	{
		w.println(".");
		super.finalize();
	}

	public void handle(String msg)
	{

		if(msg.equals("."))
		{
			System.out.println("Attempting to close thread");
			stop();
		}
		else
			System.out.println(msg);
	}
	public void start() throws IOException
	{
		in = new BufferedReader(
				new InputStreamReader(System.in));
		w = new PrintWriter(socket.getOutputStream());
		if(thread == null)
		{
			client = new ClientThread(this,socket);
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop()
	{

		if(thread != null)
		{
			thread.interrupt();
			thread = null;
		}
		try 
		{		
			if(r != null) r.close();
			if(w != null) w.close();
			if(socket != null) socket.close();
		}
		catch (IOException ioe)
		{
			System.out.println("Error closing ...");
		}

		client.close();
		client.interrupt();
		System.out.println("Thread has closed. Type reconnect to reconnect");

	}

	public static void main(String args[])
	{
		Client client = new Client("127.0.0.1", 1244);
	}
	//Print out for socket

	private static void printSocketInfo(SSLSocket s) {
		System.out.println("Socket class: "+s.getClass());
		System.out.println("   Remote address = "
				+s.getInetAddress().toString());
		System.out.println("   Remote port = "+s.getPort());
		System.out.println("   Local socket address = "
				+s.getLocalSocketAddress().toString());
		System.out.println("   Local address = "
				+s.getLocalAddress().toString());
		System.out.println("   Local port = "+s.getLocalPort());
		System.out.println("   Need client authentication = "
				+s.getNeedClientAuth());
		SSLSession ss = s.getSession();
		System.out.println("   Cipher suite = "+ss.getCipherSuite());
		System.out.println("   Protocol = "+ss.getProtocol());
	}

}
