
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.net.*;

public class Server {

	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private final int BACKLOG = 20;
	private int port;
	private String address = "localhost";
	private ServerSocket serverSocket = null;
	
	public Server(String address, int port){
		this.address = address;
		this.port = port;
	}
	
	public Server(int port){
		this.port = port;
	}
	
	public void run(){
		System.out.println("Server is running");
		//create a socket
		try {
			
			this.serverSocket = new ServerSocket(
					this.port,
					this.BACKLOG
			);
			
			do{
				Socket clientSocket = this.serverSocket.accept();
				ServerThread serverThread = new ServerThread(clientSocket, this.serverSocket);
				this.threadPool.execute(serverThread);
			}while(true);
					
		} 
		
		catch (IOException e) {
 			System.out.println("Error reading socket");
		}
		catch(IllegalArgumentException iae){
			iae.printStackTrace();
		}
		finally{
			// have check here that other threads aren't running
			if (this.serverSocket != null)
				try {
					this.serverSocket.close();
				} 
				catch (IOException e) {
					System.out.println("Error closing server socket.");
				}
		}
		
	}

	
	public static void main(String[] args){
		
		boolean DEVELOPMENT = false;
		if (DEVELOPMENT){
			Server myFtpServer = new Server("localhost", 60000);
			myFtpServer.run();
		}
		else{
			try{
				if(args.length == 1 && Integer.valueOf(args[0]) >= 49152 && Integer.valueOf(args[0]) <= 65535){
					Server myFtpServer = new Server("localhost", Integer.valueOf(args[0]));
					myFtpServer.run();
				}
				else{
					throw new NumberFormatException();
				}
			}
			catch(NumberFormatException nfe){
				System.out.println("Server must be run with this syntax: java Server [port number (49152 - 65535) ]");
			}
		}
		
	}

}