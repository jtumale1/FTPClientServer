import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.net.*;

public class Server {

	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private final int BACKLOG = 20;
	private int port;
	private String address = "localhost";
	private ServerSocket socket;
	
	
	public Server(String address, int port){
		this.address = address;
		this.port = port;
		this.run();
	}
	
	public Server(int port){
		this.port = port;
		this.run();
	}
	
	public void run(){
		//create a socket
		try {
			this.socket = new ServerSocket(
					this.port, 
					this.BACKLOG, 
					InetAddress.getLocalHost()
					);
			//bind port
			this.socket.bind(new InetSocketAddress(this.address, this.port));
			
			Socket client = this.socket.accept();
			//TODO add args to client thread
			this.threadPool.execute(new ClientThread(client));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	public static void main(String[] args) {
		
	}

}
