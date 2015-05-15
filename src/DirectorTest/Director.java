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
	private HashMap<Integer, String> map = new HashMap<Integer, String>();

	public Director(int port, String ksName, char[] ksPass, char[] ctPass)
	{
		try {
			KeyStore ks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream(ksName), ksPass);

			KeyManagerFactory kmf = 
					KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

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
		else if(input.startsWith(".settings "))
		{
			input = input.substring(10, input.length());
			if(input.equals(".collector"))
			{
				map.put(ID, "collector");
				System.out.println("CollectorID: " +ID +" Type: collector");
			}
			if(input.startsWith(".analyst "))
			{
				input = input.substring(9, input.length());
				map.put(ID, input);
				System.out.println("AnalystID: " +ID +" Type: " + input);
			}
		}
		else if(input.startsWith(".director "))
		{
			input = input.substring(10, input.length());
			if(input.startsWith(".analysis "))
			{

				input = input.substring(10, input.length());

				if(map.get(ID).equals("collector"))
				{
					//send input from director to analyst

					System.out.println("Looking for an analyst for " +input.substring(0, 5));
					int analystID = 0;
					for (Integer key : map.keySet()) {	
						System.out.println(key+" "+map.get(key));
						if(map.get(key).equals(input.substring(0, 5))){
							clients.get(findClient(key)).send("ID "+ID + " data " + input);
							System.out.println("DATA sent to Analyst "+key);
							//map.remove(key); THIS SHIT IS BROKEN
							analystID = key; 
							break;
						}
					}

					System.out.println("Data from Collector Processing by "+analystID);
				}
				else
				{

					//send result from analyst to collector
					int returnID = Integer.parseInt(input.substring(0, 5));
					System.out.println("Sending results to " +returnID);
					String result = input.substring(6, input.length());
					clients.get(findClient(returnID)).send("Analysts ID: "+ID + " result: " + result);
					System.out.println("RESULT sent to Collector "+returnID);
					//map.remove(returnID); BREAKS EVERYTHING

					System.out.println("Result from Analyst sent back to " +returnID);
				}
			}

			else if(input.startsWith(".test ")) 
			{
				for(int i = 0; i < clients.size(); i++)
				{
					if(input.equals(".")) break;
					if(i != pos)
					{
						clients.get(i).send(ID + ": " + input);
					}
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
		String str = "";
		BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			System.out.println();
			System.out.println("Please enter server port: ");
			try 
			{
				str = sysIn.readLine();
				if(str.equals(""))
				{
					str = "1111";
				}
				if(str.equals("."))
				{
					break;
				}
				Director director = new Director(Integer.parseInt(str), ksName, ksPass, ctPass);
				Thread thread = director.getThread();
				while(thread.isAlive() && thread != null)
				{
					Thread.sleep(1);
				}
			}
			catch ( Exception e )
			{
				System.out.println("Error in main: " + e.getMessage());
			}
		}
	}

	public Thread getThread()
	{
		return thread;
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
