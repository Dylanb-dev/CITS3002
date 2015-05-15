package DirectorTest;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import javax.net.ssl.*;


public class Director implements Runnable {
	
	private SSLServerSocket server = null;
	private Thread thread = null;
	private ArrayList<MyThread> clients = new ArrayList<MyThread>();
	
	public Director(int port, String ksName, char[] ksPass, char[] ctPass)
	{
		try {
			KeyStore ks = KeyStore.getInstance("JKS");
			
			ks.load(new FileInputStream(ksName), ksPass);
			
			KeyManagerFactory kmf = 
					KeyManagerFactory.getInstance("SunX509");
			
			kmf.init(ks, ctPass);
			
			SSLContext sc = SSLContext.getInstance("TLS");
			
			sc.init(kmf.getKeyManagers(), null, null);
						
			SSLServerSocketFactory ssf = sc.getServerSocketFactory();
			//SSLSocket server = (SSLSocket) s.accept();
						
			System.out.println("Binding to port " + port + ", please wait ...");
			//server = new ServerSocket(port);
			server = (SSLServerSocket) ssf.createServerSocket(port);
			System.out.println("Server started: " + server);
			printServerSocketInfo((SSLServerSocket) server);

			start();
		} catch (Exception e) {
			System.out.println("Exception : " + e);
		}
	}
	
	public void run() {
		while(thread != null)
		{
			try
			{
				System.out.println("Waiting for a client ...");
				SSLSocket c = (SSLSocket) server.accept();
				printSocketInfo(c);
				addThread(c);
			}
			catch (IOException ioe)
			{
				System.out.println("Error running: " + ioe);
			}
		}
	}
	
	private int findClient(int ID)
	{
		for(int i = 0; i < clients.size(); i++)
		{
			if(clients.get(i).getID() == ID)
				return i;
		}
		return -1;
	}
	public synchronized void handle(int ID, String input)
	{
		int pos = findClient(ID);
		if(input.equals("."))
		{
			clients.get(pos).send(".");
			remove(ID);
		}
		else if(input.startsWith(".director "))
		{
			input = input.substring(10, input.length());
			for(int i = 0; i < clients.size(); i++)
			{
				if(input.equals(".")) break;
				if(i != pos)
				{
					clients.get(i).send(ID + ": " + input);
				}
			}
		}
		else if(input.startsWith(".bank "))
		{
			clients.get(pos).send("This is not a Bank. Please disconnect with '.' and check your connection settings...");
		}
	}
	public synchronized void remove(int ID)
	{
		int pos = findClient(ID);
		if( pos >= 0 )
		{
			MyThread toTerminate = clients.get(pos);
			System.out.println("Removing client thread " + ID + " at " + pos);
			clients.remove(pos);
			try
			{
				toTerminate.close();
			}
			catch (IOException ioe)
			{
				System.out.println("Error closing thread " + ioe);
			}
			toTerminate.interrupt();
		}
		handle(0, ID + " disconnected ...");
	}
	
	public void addThread(SSLSocket socket)
	{
		System.out.println("Client accepted: " + socket.getInetAddress());
		clients.add(new MyThread(this, socket));
		try {
			clients.get(clients.size()-1).open();
			clients.get(clients.size()-1).start();
		} catch (IOException ioe) {
			System.out.println("Error opening thread: " + ioe);
		}
	}
	
	
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	public void stop() {
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}
	public static void main(String args[]) {
		String ksName = "bin/ServerClient/bank.jks";
		char ksPass[] = "BankJKS".toCharArray();
		char ctPass[] = "BankJKS".toCharArray();
		new Director(2234, ksName, ksPass, ctPass);
	}
	
	
	
	//Print outs for socket and Server
	
	
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
	
	private static void printServerSocketInfo(SSLServerSocket s) {
		System.out.println("Server socket class: "+s.getClass());
		System.out.println("   Socker address = "
				+s.getInetAddress().toString());
		System.out.println("   Socker port = "
				+s.getLocalPort());
		System.out.println("   Need client authentication = "
				+s.getNeedClientAuth());
		System.out.println("   Want client authentication = "
				+s.getWantClientAuth());
		System.out.println("   Use client mode = "
				+s.getUseClientMode());
	} 
}
