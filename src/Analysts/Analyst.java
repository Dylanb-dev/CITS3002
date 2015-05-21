package Analysts;

import java.io.*;
import java.util.ArrayList;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class Analyst implements Runnable {

	private AnalystThread client = null;
	private BufferedReader in = null;
	private BufferedReader r = null;
	private SSLSocket directorSocket = null;
	private SSLSocket bankSocket = null;
	private Thread thread = null;
	private PrintWriter directorOut = null;
	private PrintWriter bankOut = null;
	private String Title = "";
	private String DATA = "";
	private ArrayList<String> eCents = new ArrayList<String>();
	private AES_Cipher aes = new AES_Cipher();
	private String recs[] = new String[2];
	private int CollectorID = 0;


	public static void main(String args[])
	{
		String str = "";
		String title = "";
		BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			System.out.print("Type analyst title [5 letter datatype]: ");
			try 
			{
				str = sysIn.readLine().toLowerCase();
				
				if(str.length() == 5)
				{
					title = str;
					break;
				}
				else
				{
					System.out.println("Incorrect datatype");
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		while(true)
		{
			System.out.println();
			System.out.println("Please enter server addresses and ports in the form,");
			System.out.println("'DIRECTOR_ADDRESS PORT BANK_ADDRESS PORT'");
			System.out.print("(e.g. '127.0.0.1 1234 192.168.0.1 4321'): ");
			try 
			{
				str = sysIn.readLine();
				if(str.equals(""))
				{
					str = "127.0.0.1 1111 127.0.0.1 2222";
				}
				if(str.equals("."))
				{
					break;
				}
				String strs[] = str.split(" ");
				Analyst client = new Analyst(strs[0], Integer.parseInt(strs[1]), strs[2], Integer.parseInt(strs[3]), title);
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
				System.out.println("One or more addresses or ports are incorrect!");
			}
		}
		System.out.println("Goodbye.");
	}



	public Analyst(String directorName, int directorPort, String bankName, int bankPort, String title)
	{
		Title = title;
		SSLSocketFactory f = 
				(SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			directorSocket = (SSLSocket) f.createSocket(directorName, directorPort);
			printSocketInfo(directorSocket);
			directorSocket.startHandshake();
			bankSocket = (SSLSocket) f.createSocket(bankName, bankPort);
			printSocketInfo(bankSocket);
			bankSocket.startHandshake();	
			start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			stop();
		}

	}

	protected void finalize( ) throws Throwable
	{
		directorOut.println(".");
		bankOut.println(".");
		super.finalize();
	}

	public void handle(String msg) throws Exception
	{
		if(msg.equals("."))
		{
			stop();
		}
		if(msg.startsWith("eCent "))
		{
			eCents.add(msg.substring(6, msg.length()));
			System.out.println("eCent added: "+ eCents.toString());
		}
		if(msg.startsWith("ID "))
		{
			System.out.println(msg);
			System.out.println(msg.substring(3, 8));

			CollectorID = Integer.parseInt(msg.substring(3, 8));
			String rec = msg.substring(20, msg.length());
			System.out.println("STRING FOR DECYPTION '" + rec + "'");
			directorOut.println(".director .received "+CollectorID);
			directorOut.flush();
			System.out.println(".director .received "+CollectorID);
			System.out.println("Sent received to Director");



			try{
				rec = AES_Cipher.decrypt(rec);
			}  catch (Exception e) {
				e.printStackTrace();
			}
			String recs[] = rec.split(" ",2);
			eCents.add(recs[0]);
			bankOut.println(".bank .deposit "+eCents.get(0));	
			bankOut.flush();
			System.out.println("data and eCent: " + rec);

		}	

		if(msg.startsWith("Thank you for the deposit")){
			System.out.println("Successfully Deposited eCent, Performing Analysis");
			eCents.remove(0);
			System.out.println("Deleted eCent");
			Thread.sleep(10000); //Put thread to sleep to simulate analysis
			System.out.println("Successfully Completed Analysis");
			directorOut.println(".director .completed "+CollectorID);
			directorOut.flush();
			System.out.println("Sent Completed message to Director");

			
		}


		else System.out.println(msg);
	}



	@Override
	public void run()
	{
		System.out.println("Welcome! Analyst "+Title);
		System.out.println("Type '.help' for help");

		directorOut.println(".settings .analyst " + Title);
		directorOut.flush();

		while (thread != null)
		{
			try
			{
				String str = in.readLine();

				if(str.equals("."))
				{
					directorOut.println(str);
					directorOut.flush();
					bankOut.println(str);
					bankOut.flush();
				}

				else if(str.startsWith(".director "))
				{
					if(str.startsWith(".director .analysis")){
						String data = str.substring(26, str.length());
						if(eCents.size() == 0){
							System.out.println("No eCents");
						}
						else{
							try {
								DATA = AES_Cipher.encrypt(eCents.get(0) +" "+ data);
							} catch (Exception e) {
								e.printStackTrace();
							}
							System.out.println(str.substring(0, 26)+ DATA);
							directorOut.println(str.substring(0, 26)+ DATA);
							directorOut.flush();

						}
					}

				}
				else if(str.startsWith(".bank "))
				{
					bankOut.println(str);
					bankOut.flush();
				}
				
				else if(str.startsWith(".help"))
				{
					System.out.println("You can use the following commands:");
					System.out.println("\t.director .analysis [collectorID] [results]");
					System.out.println("\t.bank .withdraw");
					System.out.println("\t.bank .deposit");
					System.out.println("\t. to close thread");
					bankOut.flush();
				}
				
				
				
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
		directorOut = new PrintWriter(directorSocket.getOutputStream());
		bankOut = new PrintWriter(bankSocket.getOutputStream());
		if(thread == null)
		{
			client = new AnalystThread(this,directorSocket);
			client = new AnalystThread(this,bankSocket);
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
			if(directorOut != null) directorOut.close();
			if(directorSocket != null) directorSocket.close();
			if(bankOut != null) bankOut.close();
			if(bankSocket != null) bankSocket.close();
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

	//Print out for socket
	private static void printSocketInfo(SSLSocket s) {
		System.out.println("   Remote address = "
				+s.getInetAddress().toString());
		System.out.println("   Remote port = "+s.getPort());
		System.out.println("   Local address = "
				+s.getLocalAddress().toString());
		System.out.println("   Local port = "+s.getLocalPort());
		SSLSession ss = s.getSession();
		System.out.println("   Cipher suite = "+ss.getCipherSuite());
		System.out.println("   Protocol = "+ss.getProtocol());
	}
	
	
}
