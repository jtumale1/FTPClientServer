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
	private volatile Boolean active = true;
	
	
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
				ServerThread serverThread = new ServerThread(clientSocket, this.active);
				this.threadPool.execute(serverThread);
				clientSocket.close();
			}while(active);
					
		} 
		
		catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
		}
		catch(IllegalArgumentException iae){
			iae.printStackTrace();
		}
		finally{
			// have check here that other threads arent running
			if (this.serverSocket != null)
				try {
					this.serverSocket.close();
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
	}

	
	public static void main(String[] args){
		//TODO parse args for port number
		Server myFtpServer = new Server("localhost", 9000);
		myFtpServer.run();
		
	}

}
