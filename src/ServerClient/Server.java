package ServerClient;

import java.io.*;
import java.net.*;


public class Server implements Runnable {
	
	private ServerSocket server = null;
	private Thread thread = null;
	private MyThread client = null;
	
	public Server(int port)
	{
		try {
			System.out.println("Binding to port " + port + ", please wait ...");
			server = new ServerSocket(port);
			System.out.println("Server started: " + server);
			start();
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}
	
	public void run() {
		while(thread != null)
		{
			try
			{
				System.out.println("Waiting for a client ...");
				addThread(server.accept());
			}
			catch (IOException ioe)
			{
				System.out.println(ioe);
			}
		}
	}
	
	public void addThread(Socket socket)
	{
		System.out.println("Client accepted: " + socket.getInetAddress());
		client = new MyThread(this, socket);
		try {
			client.open();
			client.start();
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
		Server server = new Server(1244);
	}
}
