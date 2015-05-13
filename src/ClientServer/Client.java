package ClientServer;

import java.io.*;
import java.net.*;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class Client {

	private SSLSocket socket = null;
	private BufferedWriter w = null;
	private BufferedReader r = null;
	private BufferedReader in = null;

	public Client(String serverName, int serverPort)
	{
	      BufferedReader in = new BufferedReader(
	    	      new InputStreamReader(System.in));
	    	      PrintStream out = System.out;
	    	      SSLSocketFactory f = 
	    	         (SSLSocketFactory) SSLSocketFactory.getDefault();
	    	      try {
	    	         SSLSocket c =
	    	           (SSLSocket) f.createSocket(serverName, serverPort);
	    	         printSocketInfo(c);
	    	         c.startHandshake();	
	    	         BufferedWriter w = new BufferedWriter(
	    	            new OutputStreamWriter(c.getOutputStream()));
	    	         BufferedReader r = new BufferedReader(
	    	            new InputStreamReader(c.getInputStream()));
	    	         String m = null;
	    	         while ((m=r.readLine())!= null) {
	    	            out.println(m);
	    	            m = in.readLine();
	    	            w.write(m,0,m.length());
	    	            w.newLine();
	    	            w.flush();
	    	         }
	    	         w.close();
	    	         r.close();
	    	         c.close();
	    	      } catch (IOException e) {
	    	         System.err.println(e.toString());
	    	      }

	}

	public void start() throws IOException
	{
		r = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		w = new BufferedWriter(
				new OutputStreamWriter(socket.getOutputStream()));
	}

	public void stop()
	{
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
	}

	public static void main(String args[])
	{
		new Client("127.0.0.1", 1244);
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
