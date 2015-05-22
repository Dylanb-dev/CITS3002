
package Bank;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.*;

import javax.net.ssl.*;


public class Bank implements Runnable {

	private SecureRandom random = new SecureRandom();
	private SSLServerSocket server = null;
	private Thread thread = null;
	private ArrayList<MyThread> clients = new ArrayList<MyThread>();
	private HashMap<String, Integer> loans = new HashMap<String, Integer>();
	private HashMap<String, Integer> deposits = new HashMap<String, Integer>();

	
	public Bank(int port, String ksName, char[] ksPass, char[] ctPass)
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
		else if(input.startsWith(".bank "))
		{
			input = input.substring(6, input.length());
			if(input.equals(".withdraw")){
				System.out.println("eCent withdrawl requested from "+ID);
				String eCent = new BigInteger(130, random).toString(32);
				loans.put(eCent,ID);
				clients.get(pos).send("eCent " + eCent);
				System.out.println("eCent sent to "+ID);
				System.out.println(loans.toString());
				System.out.println(deposits.toString());



			}
			else if(input.startsWith(".deposit ")){
				input = input.substring(9, input.length());
				System.out.println("eCent "+input+" deposit requested from "+ID);
				System.out.println(loans.get(input));
				
				if(deposits.containsKey(input)){
					clients.get(pos).send("eCent is already deposited");
					System.out.println(loans.toString());
					System.out.println(deposits.toString());
				}
				else if(loans.containsKey(input)) {
					deposits.put(input,ID);
					clients.get(pos).send("Thank you for the deposit");
					System.out.println(loans.toString());
					System.out.println(deposits.toString());
					clients.get(loans.get(input)).send("eCent Deposited by " + ID);

				}
				else{
					clients.get(pos).send("Something went wrong...");
					System.out.println(loans.toString());
					System.out.println(deposits.toString());
				}

			}
				
		else if (input.startsWith(".test")) {
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


		else if(input.startsWith(".director "))
		{
			clients.get(pos).send("This is not a Director. Please disconnect with '.' and check your connection settings...");
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
		String ksName = "bin/Bank/key.jks";
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
					str = "2222";
				}
				if(str.equals("."))
				{
					break;
				}
				Bank bank = new Bank(Integer.parseInt(str), ksName, ksPass, ctPass);
				Thread thread = bank.getThread();
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
