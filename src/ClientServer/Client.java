package ClientServer;

import java.io.*;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class Client implements Runnable {

	private ClientThread client = null;
	private BufferedReader in = null;
	private BufferedReader r = null;
	private SSLSocket socket = null;
	private Thread thread = null;
	private PrintWriter w = null;
	
	
	public static void main(String args[])
	{
		String str = "";
		BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			System.out.println();
			System.out.println("Please enter server address and port: ");
			System.out.print("(e.g. '127.0.0.1 1111'): ");
			try 
			{
				str = sysIn.readLine();
				if(str.equals("."))
				{
					break;
				}
				String strs[] = str.split(" ");
				Client client = new Client(strs[0], Integer.parseInt(strs[1]));
				Thread thread = client.getThread();
				while(thread.isAlive() && thread != null)
				{
					Thread.sleep(1);
				}
			} 
			catch (IOException ioe) 
			{
				System.out.println("Error reading from console: " + ioe.getMessage());
			}
			catch (Exception e)
			{
				System.out.println("Address and port are not in correct format!");
			}
		}
		System.out.println("Goodbye.");
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

	@Override
	protected void finalize( ) throws Throwable
	{
		w.println(".");
		super.finalize();
	}
	public void handle(String msg)
	{
		if(msg.equals("."))
		{
			stop();
		}
		else System.out.println(msg);
	}

	@Override
	public void run()
	{
		System.out.println("Welcome!");
		while (thread != null)
		{
			try
			{
				w.println(in.readLine());
				w.flush();
				Thread.sleep(10);
			}
			catch (IOException ioe)
			{
				System.out.println("Console Error: " + ioe.getMessage());
				stop();
			} 
			catch (InterruptedException e) { }
		}
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
		System.out.println("Disconnecting from server ...");
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
	}
	
	public Thread getThread()
	{
		return thread;
	}

}
